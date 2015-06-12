(ns radicalzephyr.boot-junit
  {:boot/export-tasks true}
  (:require [boot.core :as core]
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

(defn- failure->map [failure]
  {:description (.. failure (getDescription) (toString))
   ;;:exception (.getException failure)
   :message (.getMessage failure)})

(defn- result->map [result]
  {:successful? (.wasSuccessful result)
   :run-time (.getRunTime result)
   :run     (.getRunCount result)
   :ignored (.getIgnoreCount result)
   :failed  (.getFailureCount result)
   :failures (map failure->map (.getFailures result))})

(defn- build-package-config [^String package]
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

(defn- print-ignored-tests [ignored-tests]
  (when (seq ignored-tests)
    (println "Ignored:")
    (println)
    (doseq [ignored ignored-tests]
      (println (style (.getDisplayName ignored) :yellow))
      (println))))

(defn- print-failed-tests [test-failures]
  (when (seq test-failures)
    (println "Failed:")
    (println)
    (doseq [failure test-failures]
      (println (.getTestHeader failure))
      (println (style (.getTrace failure)
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

(defn- run-listener [packages]
  (let [running-tests (atom #{})
        ignored-tests (atom #{})]
    (proxy [RunListener]
        []
      (testRunStarted [description]
        (println "Running jUnit tests for "
                 (str/join ", " packages)))

      (testRunFinished [result]
        (println "\n")
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
            (print (style "F" :red))))))))

(core/deftask junit
  "Run the jUnit test runner."
  [p packages PACKAGE #{sym} "The set of Java packages to run tests in."]
  (core/with-pre-wrap fileset
    (if (seq packages)
      (let [^JUnitCore core (doto (JUnitCore.)
                              (.addListener (run-listener packages)))
            result (.run core
                         (into-array Class
                                     (find-all-tests packages)))]
        (when (> (.getFailureCount result) 0)
          (throw (ex-info "Some tests failed or errored" {}))))
      (println "No packages were tested."))
    fileset))
