(set-env!
 :source-paths #{"src"}
 :resource-paths #{"src"}
 :dependencies '[[org.clojure/clojure         "1.6.0"  :scope "provided"]
                 [boot/core                   "2.1.0"  :scope "provided"]
                 [radicalzephyr/clansi        "1.2.0" ]
                 [junit                       "4.12"  ]
                 [org.reflections/reflections "0.9.10"]
                 [org.glassfish/javax.servlet "3.0"   ]
                 [radicalzephyr/bootlaces     "0.1.12"]])

(require '[radicalzephyr.bootlaces :refer :all]
         '[radicalzephyr.boot-junit :refer [junit]])

(def +version+ "0.1.1")

(bootlaces! +version+)

(task-options!
 pom  {:project     'radicalzephyr/boot-junit
       :version     +version+
       :description "Run some jUnit tests in boot!"
       :url         "https://github.com/radicalzephyr/boot-junit"
       :scm         {:url "https://github.com/radicalzephyr/boot-junit"}
       :license     {"Eclipse Public License"
                     "http://www.eclipse.org/legal/epl-v10.html"}}
 junit {:packages '#{radicalzephyr.boot_junit.test}})
