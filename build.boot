(set-env!
 :source-paths #{"src"}
 :dependencies '[[org.clojure/clojure  "1.6.0"      :scope "provided"]
                 [boot/core            "2.0.0-rc12" :scope "provided"]
                 [junit                "4.12"       :scope "provided"]])

(require '[radicalzephyr.boot-junit :refer [junit]])

(def +version+ "0.1.0")

(task-options!
 pom  {:project     'radicalzephyr/boot-junit
       :version     +version+
       :description "Run some jUnit tests in boot!"
       :url         "https://github.com/radicalzephyr/boot-junit"
       :scm         {:url "https://github.com/radicalzephyr/boot-junit"}
       :license     {"Eclipse Public License"
                     "http://www.eclipse.org/legal/epl-v10.html"}}
 junit {:packages '#{radicalzephyr.boot_junit.test}})
