# Change Log
All notable changes to this project will be documented in this
file. This change log follows the conventions
of [keepachangelog.com](http://keepachangelog.com/).


## [Unreleased]
### Added

- A new `javac*` task for dynamically recompiling java code

### Changed

- No longer runs jUnit tests inside of a pod (to enable test reloading)
- Renamed the `packages` option to `paths` and changed the expected
  format/separator to match ("." -> "/")

[Unreleased]: https://github.com/RadicalZephyr/boot-junit/compare/0.2.0...HEAD


## [0.2.0] - 2015-07-04
### Changed

- Simplified assertion stacktraces to only the important information
- More improvements to the output
- Now runs the jUnit tests inside of a pod
- Pulled out core jUnit runner functionality into a [separate library][cljunit]

[0.2.0]: https://github.com/RadicalZephyr/boot-junit/compare/0.1.1...0.2.0
[cljunit]: https://github.com/RadicalZephyr/cljunit


## [0.1.1] - 2016-10-21
### Changed

- Improved test formatting (heavily based on Rspec's simple format)

[0.1.1]: https://github.com/RadicalZephyr/boot-junit/compare/0.1.0...0.1.1


## [0.1.0] - 2016-06-08
### Added

- A new `junit` task for finding and running jUnit tasks

[0.1.0]: https://github.com/RadicalZephyr/boot-junit/compare/12e098a...0.1.0
