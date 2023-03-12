# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning].

## 3.4.3 - 2023-03-12
Support for gradle unit test

## 3.4.1 - 2022-10-04

### Fixed

- Plugin affecting env vars even when disabled

## 3.4.0 - 2022-10-03

### Added

- When environment variables file is executed, it now receives all environment variables accumulated by previous entries
- Log stderr from execution of environment variables files into IDE event console

## 3.3.0 - 2022-10-02

### Added

- Multi-line variable support in `.env` files ([#127]) - special thanks to [@ledoyen](https://github.com/ledoyen)
- Ignore for `export ` prefix in `.env` files
- Include parent environment variables (when enabled) into environment substitution ([#165]) - special thanks to [@jansorg](https://github.com/jansorg)
- An option to execute file and parse content from standard output

### Fixed

- Incompatibility with IDEA 2022.2 ([#151]) - special thanks to [@HassanAbouelela](https://github.com/HassanAbouelela)
- Experimental features checkbox is properly enabled/disabled when plugin is enabled/disabled

## 3.2.2 - 2021-09-02

### Fixed

- Incompatibility with IDEA 2021 ([#151]) - special thanks to [@tosmun](https://github.com/tosmun) for contribution

## 3.2.1 - 2020-03-30

### Fixed

- Incompatibility with IDEA 2020 ([#38]) - special thanks to [@pilzm](https://github.com/pilzm) for contribution 

## 3.2.0 - 2019-05-24

### Added

- Experimental integration for external system run configurations (such as Gradle) ([#38])

## 3.1.2 - 2019-05-21

### Fixed

- Try to recover from `AssertionError: Already disposed: Project`, reported via email and in ([#83])

## 3.1.1 - 2019-04-24

### Fixed

- Substitute parent process env vars in IDEA - wasn't working before ([#81])

## 3.1.0 - 2018-11-28

### Added

- EnvFile can substitute Jet Brains path macro references ([#70])
- Add an option to ignore missing files ([#72])

## 3.0.1 - 2018-10-15

### Fixed

- Fix language support announcement

## 3.0.0 - 2018-10-11

### Added

- Gradle build system support ([#68])
- Automatic versioning from git tags ([#68])
- Integration with Goland ([#67])
- Announce support for `.env` file extension
- Run Configuration defined environment variables are now displayed in EnvFile tab as individual entry 
- Optional environment variable substitution ([#16]) using [StringSubstitutor]

### Fixed

- Unicode sequence handling in `.env` files [(#32)]
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
- Change Log to [Keep a CHANGELOG] format


## 1.0.0 - 2015-07-18

### Added
- Added support for .env file format
- Added support for YAML dictionary format
- Added support for JSON dictionary format (handled by YAML parser)
- Initial Release

[#16]: https://github.com/Ashald/EnvFile/issues/16
[#38]: https://github.com/Ashald/EnvFile/issues/32
[#38]: https://github.com/Ashald/EnvFile/issues/38
[#52]: https://github.com/Ashald/EnvFile/issues/52
[#61]: https://github.com/Ashald/EnvFile/issues/61
[#67]: https://github.com/Ashald/EnvFile/issues/67
[#68]: https://github.com/Ashald/EnvFile/issues/68
[#70]: https://github.com/Ashald/EnvFile/issues/70
[#72]: https://github.com/Ashald/EnvFile/issues/72
[#81]: https://github.com/Ashald/EnvFile/issues/81
[#81]: https://github.com/Ashald/EnvFile/issues/83
[#100]: https://github.com/ashald/EnvFile/issues/100
[#127]: https://github.com/ashald/EnvFile/issues/127
[#151]: https://github.com/ashald/EnvFile/issues/151
[#165]: https://github.com/ashald/EnvFile/issues/165

[Keep a CHANGELOG]:     http://keepachangelog.com
[Semantic Versioning]:  http://semver.org/
[StringSubstitutor]:    https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/StringSubstitutor.html
