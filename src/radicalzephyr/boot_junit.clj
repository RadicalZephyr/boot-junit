(ns radicalzephyr.boot-junit
  {:boot/export-tasks true}
  (:require [boot.core :as core]
            [boot.pod  :as pod]
            [boot.util  :as util]
            [clojure.string :as str]))

(def pod-deps
  '[[radicalzephyr/cljunit "0.2.0-SNAPSHOT"]])

(defn- init [fresh-pod]
  (pod/with-eval-in fresh-pod
    (require '[cljunit.core :refer [run-tests-in-classes]])))

(defn- construct-class-name [prefix path]
  (let [path-seq (-> path
                     (str/replace prefix "")
                     (str/replace #"\.java$" "")
                     (str/split #"/"))]
    (->> path-seq
         (remove empty?)
         (interpose ".")
         (apply str))))

(defn- path->class-name [class-name]
  (let [prefix (str (core/tmp-dir class-name))
        file (core/tmp-file class-name)]
    (construct-class-name prefix (str file))))

(defn- path->package-name [path]
  (str/join "." (-> path
                    (str/split #"/")
                    drop-last)))

(defn- all-class-names [user-files]
  (->> user-files
       (core/by-ext [".java"])
       (map path->class-name)))

(core/deftask junit
  "Run the jUnit test runner."
  [c class-names  CLASSNAME #{str} "The set of Java class names to run tests from."
   p packages     PACKAGE   #{str} "The set of package names to run tests from."]
  (let [worker-pods (pod/pod-pool (update-in (core/get-env) [:dependencies] into pod-deps)
                                  :init init)]
    (core/cleanup (worker-pods :shutdown))
    (core/with-pre-wrap fileset
      (let [worker-pod (worker-pods :refresh)
            all-class-names (all-class-names (core/input-files fileset))]
        (if-let [result (pod/with-eval-in worker-pod
                          (run-tests-in-classes '~all-class-names
                                                :classes ~class-names
                                                :packages ~packages))]
          (when (> (:failures result) 0)
            (throw (ex-info "Some tests failed or errored" {})))
          (util/warn "Nothing was tested.")))
      fileset)))
