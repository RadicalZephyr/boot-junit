# boot-junit

[](dependency)
```clojure
[radicalzephyr/boot-junit "0.1.0"] ;; latest release
```
[](/dependency)

Boot task for running jUnit Java tests.

- Provides a `junit` task

## Usage

First, make sure the root of your java sources and test sources are
included in your boot `:source-paths`:

    (set-env!
     :source-paths #{"src/main/java" "src/test/java"}
     ...)

Then, configure the `junit` task with the packages to be searched for
jUnit tests. Currently this must be done manually, but in the future
we plan to support inferring packages from the given source-paths.

    (task-options!
     junit {:packages '#{radicalzephyr.boot_junit.test}})


Then I typically define a `test` task for ease of use.

    (deftask test
      "Compile and run my jUnit tests."
      []
      (comp (javac)
            (junit)))

Now just run `boot test`!
