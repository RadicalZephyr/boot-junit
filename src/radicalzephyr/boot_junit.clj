(ns radicalzephyr.boot-junit
  {:boot/export-tasks true}
  (:require [boot.core :as core]
            [boot.pod  :as pod]
            [clojure.string :as str]
            [clojure.pprint :refer [cl-format]]
            [clansi.core    :refer [style]])
  (:import org.junit.runner.JUnitCore
           org.junit.runner.notification.RunListener
           (org.reflections Reflections
                            Configuration)
           (org.reflections.scanners Scanner
                                     TypeAnnotationsScanner
                                     MethodAnnotationsScanner)
           (org.reflections.util ClasspathHelper
                                 ConfigurationBuilder
                                 FilterBuilder)))

(def pod-deps
  '[[radicalzephyr/clansi "1.2.0" :exclusions [org.clojure/clojure]]
    [clj-stacktrace       "0.2.8" :exclusions [org.clojure/clojure]]])

(defn init [fresh-pod]
  (doto fresh-pod
    (pod/with-eval-in
      (require '[clojure.pprint :refer [cl-format]]
               '[clj-stacktrace.core :refer [parse-exception]]
               '[clojure.string :as str]
               '[clansi.core    :refer [style]])
      (import org.junit.runner.JUnitCore
              org.junit.runner.notification.RunListener)
      (defn- print-ignored-tests [ignored-tests]
        (when (seq ignored-tests)
          (println "Ignored:")
          (println)
          (doseq [[i ignored] (map-indexed vector ignored-tests)]
            (printf "  %d) %s\n"
                    (inc i) (style (.getDisplayName ignored) :yellow))
            (println))))

      (defn- print-failed-tests [test-failures]
        (when (seq test-failures)
          (println "Failed:")
          (println)
          (doseq [[i failure] (map-indexed vector test-failures)]
            (printf "  %d) %s\n"
                    (inc i) (.getTestHeader failure))
            (printf "     %s\n" (style (.getTrace failure)
                                       :red))
            (println))))

      (defn- print-test-summary [result]
        (printf "Finished in %s seconds\n" (float (/ (.getRunTime result) 1000)))
        (let [run-count     (.getRunCount      result)
              ignore-count  (.getIgnoreCount   result)
              failure-count (.getFailureCount  result)
              test-count (+ run-count ignore-count)]
          (println (style (cl-format nil "~D test~:P, ~D failure~:P~[~;, ~:*~D ignored~]~%"
                                     test-count failure-count ignore-count)
                          (if (> failure-count 0) :red :green)))))
      (defn run-listener [packages]
        (let [running-tests (atom #{})
              ignored-tests (atom #{})]
          (proxy [RunListener]
              []
            (testRunStarted [description]
              (println "Running jUnit tests for"
                       (str/join ", " packages)))

            (testRunFinished [result]
              (print "\n\n")
              (print-ignored-tests @ignored-tests)
              (print-failed-tests (.getFailures result))
              (print-test-summary result))

            (testStarted [description]
              (swap! running-tests conj description))

            (testIgnored [description]
              (swap! ignored-tests conj description)
              (print (style "*" :yellow)))

            (testFinished [description]
              (when (@running-tests description)
                (swap! running-tests disj description)
                (print (style "." :green))))

            (testFailure [failure]
              (let [description (.getDescription failure)]
                (when (@running-tests description)
                  (swap! running-tests disj description)
                  (print (style "F" :red)))))))))))

(defn build-package-config [^String package]
  (.. (ConfigurationBuilder.)
      (setUrls (ClasspathHelper/forPackage package (into-array ClassLoader [])))
      (setScanners (into-array Scanner [(TypeAnnotationsScanner.)
                                        (MethodAnnotationsScanner.)]))
      (filterInputsBy (.. (FilterBuilder.)
                          (includePackage (into-array String [package]))))))

(defn- find-tests-in-package [package]
  (let [^Configuration config (build-package-config package)
        reflections (Reflections. config)
        test-methods (.getMethodsAnnotatedWith reflections
                                               org.junit.Test)]
    (map (memfn getDeclaringClass) test-methods)))

(defn- find-all-tests [packages]
  (->> packages
       (map str)
       (mapcat find-tests-in-package)
       set))

(defn- path->package-name [path]
  (str/join "." (-> path
                    (str/split #"/")
                    drop-last)))

(defn- all-packages [user-files]
  (->> user-files
       (map core/tmp-path)
       (map path->package-name)
       set))

(core/deftask junit
  "Run the jUnit test runner."
  [p packages PACKAGE #{sym} "The set of Java packages to run tests in."]
  (let [worker-pods (pod/pod-pool (update-in (core/get-env) [:dependencies] into pod-deps)
                                  :init init)]
    (core/cleanup (worker-pods :shutdown))
    (core/with-pre-wrap fileset
      (let [worker-pod (worker-pods :refresh)
            packages (or (seq packages)
                         (all-packages (core/input-files fileset)))]
        (if (seq packages)
          (let [result (pod/with-eval-in worker-pod
                         (let [^JUnitCore core (doto (JUnitCore.)
                                                 (.addListener (run-listener '~packages)))
                               result (.run core
                                            (into-array Class
                                                        ~(find-all-tests packages)))]
                           {:failures (.getFailureCount result)}))]
            (when (> (:failures result) 0)
              (throw (ex-info "Some tests failed or errored" {}))))
          (println "No packages were tested.")))
      fileset)))
