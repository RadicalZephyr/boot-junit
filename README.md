# boot-junit

[](dependency)
```clojure
[radicalzephyr/boot-junit "0.2.0"] ;; latest release
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

Optionally, you can configure the `junit` task with the packages to be
searched for jUnit tests.

    (task-options!
     junit {:packages '#{radicalzephyr.boot_junit.test}})

If no packages are specified, the task will automatically try to run
all tests in packages defined in the current project.

Finally, I typically define a `test` task for ease of use.

    (deftask test
      "Compile and run my jUnit tests."
      []
      (comp (javac)
            (junit)))

Now just run `boot test`!
