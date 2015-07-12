(ns radicalzephyr.boot-junit
  {:boot/export-tasks true}
  (:require [boot.core :as core]
            [boot.pod  :as pod]
            [clojure.string :as str]))

(def pod-deps
  '[[radicalzephyr/cljunit "0.1.1-SNAPSHOT"]])

(defn- init [fresh-pod]
  (pod/with-eval-in fresh-pod
    (require '[cljunit.core :refer [run-test-classes
                                    run-tests-in-packages]])))

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
  [p paths    PATH  #{str} "The set of source paths to search for tests."
   c classes  CLASS #{str} "The set of Java class names to run as tests."]
  (let [worker-pods (pod/pod-pool (update-in (core/get-env) [:dependencies] into pod-deps)
                                  :init init)]
    (core/cleanup (worker-pods :shutdown))
    (core/with-pre-wrap fileset
      (let [worker-pod (worker-pods :refresh)
            packages (or (seq (map path->package-name paths))
                         (all-packages (core/input-files fileset)))]
        (cond
          (seq classes)
          (let [result (pod/with-eval-in worker-pod
                         (run-test-classes '~packages '~classes))]
            (when (> (:failures result) 0)
              (throw (ex-info "Some tests failed or errored" {}))))

          (seq packages)
          (let [result (pod/with-eval-in worker-pod
                         (run-tests-in-packages '~packages))]
            (when (> (:failures result) 0)
              (throw (ex-info "Some tests failed or errored" {}))))

          :else (println "No packages were tested.")))
      fileset)))
