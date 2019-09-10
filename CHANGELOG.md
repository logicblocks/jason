# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com)
and this project adheres to 
[Semantic Versioning](http://semver.org/spec/v2.0.0.html).


## [Unreleased]

## [0.0.4] — 2019-09-10
### Added
- Documentation.

## [0.0.3] — 2019-09-09
### Fixed
- Documentation.

## [0.0.2] — 2019-09-09
### Added
- Factory function for creating mappers.
- Macro for defining mappers in a namespace. 
- Better meta key handling which uses standard key conversion while retaining 
  meta indicator.
- Support for all of `org.joda.time` and `java.time`.

### Changed
- Use `jsonista` for underlying JSON support.

### Removed
- Concrete mappers for `wire` and `db` types.

## [0.0.1] — 2019-09-09
Released without _CHANGELOG.md_.

[0.0.1]: https://github.com/b-social/jason/compare/0.0.1...0.0.1
[0.0.2]: https://github.com/b-social/jason/compare/0.0.1...0.0.2
[0.0.3]: https://github.com/b-social/jason/compare/0.0.2...0.0.3
[0.0.4]: https://github.com/b-social/jason/compare/0.0.3...0.0.4
[Unreleased]: https://github.com/b-social/jason/compare/0.0.4...HEAD
