(ns radicalzephyr.boot-junit
  {:boot/export-tasks true}
  (:require [boot.core :as core])
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

(defn failure->map [failure]
  {:description (.. failure (getDescription) (toString))
   ;;:exception (.getException failure)
   :message (.getMessage failure)})

(defn result->map [result]
  {:successful? (.wasSuccessful result)
   :run-time (.getRunTime result)
   :run     (.getRunCount result)
   :ignored (.getIgnoreCount result)
   :failed  (.getFailureCount result)
   :failures (map failure->map (.getFailures result))})

(defn find-tests-in-package [^String package]
  (let [^Configuration config
        (.. (ConfigurationBuilder.)
            (setUrls (ClasspathHelper/forPackage package (into-array ClassLoader [])))
            (setScanners (into-array Scanner [(TypeAnnotationsScanner.)
                                              (MethodAnnotationsScanner.)]))
            (filterInputsBy (.. (FilterBuilder.)
                                (includePackage (into-array String [package])))))
        reflections (Reflections. config)
        test-methods (.getMethodsAnnotatedWith reflections
                                               org.junit.Test)]
    (->> test-methods
         (map (memfn getDeclaringClass))
         set
         vec)))

(defn find-all-tests [packages]
  (->> packages
       (map str)
       (mapcat find-tests-in-package)))

(def run-listener
  (proxy [RunListener]
      []
    (testRunStarted [description]
      (println "Test started!"))
    (testRunFinished [result]
      (println "\nTest run finished!"))
    (testStarted [description]
      (print "."))
    (testFailure [failure]
      (print "E"))))

(core/deftask junit
  "Run the jUnit test runner."
  [p packages PACKAGE #{sym} "The set of Java packages to run tests in."]
  (core/with-pre-wrap fileset
    (if (seq packages)
      (let [^JUnitCore core (doto (JUnitCore.)
                              (.addListener run-listener))
            result (.run core
                         (into-array Class
                                     (find-all-tests packages)))]
        (when (> (.getFailureCount result) 0)
          (println (result->map result))))
      (println "No packages were tested."))
    fileset))
