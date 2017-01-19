# boot-junit

[](dependency)
```clojure
[radicalzephyr/boot-junit "0.2.1"] ;; latest release
```
[](/dependency)

Boot task for running jUnit Java tests.

- Provides a `junit` task

## Usage

First, make sure the root of your java sources and test sources are
included in your boot `:source-paths`:

```clojure
(set-env!
  :source-paths #{"src/main/java" "src/test/java"}
  ...)
```

If JUnit is not already included as a dependency, you will need to include it. Maven dependency information for JUnit4 can be found [here](https://github.com/junit-team/junit4/wiki/Use-with-Maven). Then, you can then do something like this in your build.boot:

```clojure
(set-env!
  ...
  :dependencies [junit/junit              "4.12"  :scope "test"]
                [radicalzephyr/boot-junit "0.2.1" :scope "test"]
  ...)
```

To make the `junit` task available in your Boot task pipeline:

```clojure
(require '[radicalzephyr.boot-junit :refer (junit)])
```

Optionally, you can configure the `junit` task with the packages to be
searched for jUnit tests.

```clojure
(task-options!
  junit {:packages '#{radicalzephyr.boot_junit.test}})
```

If no packages are specified, the task will automatically try to run
all tests in packages defined in the current project.

Finally, I typically define a `test` task for ease of use.

```clojure
(deftask test
  "Compile and run my jUnit tests."
  []
  (comp (javac)
        (junit)))
```

Now just run `boot test`!
