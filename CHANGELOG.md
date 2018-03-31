# Change Log
All notable changes to this project will be documented in this
file. This change log follows the conventions
of [keepachangelog.com](http://keepachangelog.com/).


## [Unreleased]

[Unreleased]: https://github.com/RadicalZephyr/boot-junit/compare/0.2.0...HEAD


## [0.3.0] - 2017-01-22
### Added

- Better handling and messages for when the user may have forgotten to
  run `javac`

### Changed

- Updated version of cljunit to 0.2.0
- Change `:paths/--paths` option to `:packages/--packages` with
  corresponding change to how they're specified (change '/' to '.')
- Renamed `:classes/--classes` option to `:class-names/--class-names`
  to emphasize that you need to pass the string class name

[0.3.0]: https://github.com/RadicalZephyr/boot-junit/compare/0.2.1...0.3.0

## [0.2.1] - 2015-07-11
### Added

- Added an option `:classes/--classes` to specify specific classes to
  run tests from.

[0.2.1]: https://github.com/RadicalZephyr/boot-junit/compare/0.2.0...0.2.1

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
