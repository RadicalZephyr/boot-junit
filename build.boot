(set-env!
 :source-paths #{"src"}
 :resource-paths #{"src"}
 :dependencies '[[org.clojure/clojure     "1.6.0" :scope "provided"]
                 [boot/core               "2.1.0" :scope "provided"]
                 [junit                   "4.12"  :scope "test"]
                 [radicalzephyr/cljunit "0.2.0-SNAPSHOT" :scope "test"]
                 [radicalzephyr/bootlaces "0.1.15-SNAPSHOT"]])

(require '[radicalzephyr.bootlaces :refer :all]
         '[radicalzephyr.boot-junit :refer [junit javac*]])

(def +version+ "0.3.0-SNAPSHOT")

(bootlaces! +version+)

(task-options!
 pom  {:project     'radicalzephyr/boot-junit
       :version     +version+
       :description "Run some jUnit tests in boot!"
       :url         "https://github.com/radicalzephyr/boot-junit"
       :scm         {:url "https://github.com/radicalzephyr/boot-junit"}
       :license     {"Eclipse Public License"
                     "http://www.eclipse.org/legal/epl-v10.html"}})
