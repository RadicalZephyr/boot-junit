(ns radicalzephyr.boot-junit
  {:boot/export-tasks true}
  (:require [boot.core :as core]
            [clojure.string :as str]
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
        (when @ignored-tests
          (println "Ignored:")
          (println)
          (doseq [ignored @ignored-tests]
            (println (style (.getDisplayName ignored) :yellow))
            (println)))

        (when (> (.getFailureCount result) 0)
          (println (result->map result))))

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
