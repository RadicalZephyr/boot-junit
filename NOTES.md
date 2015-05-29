Okay, some crib notes about what I think I understand about
adzerk/boot-test right now.

First off, they run some things inside of pod's in order to keep their
dependencies isolated. This is the whole point of pods and why they're
useful, but it's pretty slick how they're making that happen.

Secondly, my first impression that you *must* specify what ns's to
test was flat out wrong. On reading the code it is clear that you can
explicitly specify the namespaces to test, OR boot-test will just find
all of the namespaces in your source dirs and search them for clojure
namespaces using tools.namespace.

Finally, a lot of the code in boot-test is actually doing what the
jUnit test runner will accomplish for me once I pass it a list of
classes. This includes searching for the tests to run, then running
them, then outputting intermediate results, then aggregating data
about the results and finally throwing the results data if the tests
had failures.

If I use what the jUnit console runner uses, I can get the output for
free.  So the main thing I need to do is implement the class finding
analogues and then pull apart the java object into a useful clojure
data structure of the results.
