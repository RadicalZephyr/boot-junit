# boot-junit

[](dependency)
```clojure
[radicalzephyr/boot-junit "0.2.1"] ;; latest release
```
[](/dependency)

Boot task for running jUnit Java tests.

- Provides `javac*` and `junit` tasks

## Usage

First, make sure the root of your java sources and test sources are
included in your boot `:source-paths`:

    (set-env!
     :source-paths #{"src/main/java" "src/test/java"}
     ...)

The task will automatically try to run all tests in packages defined
in the current project.

Finally, I typically define a `test` task for ease of use.

``` clojure
(deftask test
  "Compile and run my jUnit tests."
  []
  (comp (javac*)
        (junit)))
```

Now just run `boot test`!

### `watch`-ing your tests

If you want to run your `junit` tests inside a boot `watch` pipeline
(and really, isn't that what we all want, deep down?), you __must__
use the provided `javac*` task. Some day I'll write about why that's
necessary.

# Important Note

DO NOT use the `javac*` task for building artifacts like jar-files or
war-files. __It does not produce `.class` files!!!!__
