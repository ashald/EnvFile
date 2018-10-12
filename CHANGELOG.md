# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## Unreleased

### Added

- Gradle build system support (#68)
- Automatic versioning from git tags (#68)
- Integration with Goland (#67)
- Announce support for `.env` file extension
- Run Configuration defined environment variables are now displayed in EnvFile tab as individual entry 
- Optional environment variable Substitution ([#16]) using [StringSubstitutor]

### Fixed

- Unicode sequence handling in `.env` files ([#38])
- Backslash being removed from values in `.env` files ([#52]) 
- White text on white background when `Light` theme is used ([#61])

### Changed

- Core API extension `envfileParser` replaced with `envFileProvider` 
- Shorten path to relative for files within project dir

## 2.1.0 - 2017-03-14

### Added

- File paths can be edited manually with double-click
- Display 5 most recently used files within current IDE session

### Fixed

- Environment variables are not set in IDEA

## 2.0.0 - 2017-01-16

### Added

- Integration with IDEA
- Integration with RubyMine
- Support for multiple files

### Changed

- The `.env` parser now strips trailing `"` and `'`
- UI is moved to a separate tab
- API for extension point `envfileParser`
- The way how plugin settings saved into project definition

### Removed

- Extension point `envfileFormat`

## 1.0.1 - 2016-05-09

### Fixed
- UI components overlap with multiple projects opened within same window

### Changed
- Change Log to [Keep a CHANGELOG](http://keepachangelog.com) format


## 1.0.0 - 2015-07-18

### Added
- Added support for .env file format
- Added support for YAML dictionary format
- Added support for JSON dictionary format (handled by YAML parser)
- Initial Release

[16]: https://github.com/Ashald/EnvFile/issues/16
[38]: https://github.com/Ashald/EnvFile/issues/38
[52]: https://github.com/Ashald/EnvFile/issues/52
[61]: https://github.com/Ashald/EnvFile/issues/61

[StringSubstitutor]: https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/StringSubstitutor.html