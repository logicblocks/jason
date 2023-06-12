# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com)
and this project adheres to
[Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.0] — 2023-06-12

### Added

- `->json` and `<-json` convenience functions that don't modify object keys at
  all.

### Changed

- Forked from b-social/jason.

### Removed

- `->open-banking-json` and `<-open-banking-json` convenience functions since
  they are too specific for this library.
- The default support for Joda date and time types has been removed due to the
  introduction of `java.time`. Support can still be added by consumers of this
  library when required.

## [0.1.5] — 2020-02-04

### Added

- `->open-banking-json` and `<-open-banking-json` convenience functions for
  handling Open Banking specific casings.

## [0.1.4] — 2019-12-04

### Changed

- The `camel-snake-kebab` dependency has been switched to a release version
  rather than being pinned to a commit.

## [0.1.3] — 2019-09-16

### Added

- `jason.convenience` namespace with convenience coders for wire and db JSON.

### Changed

- The default object mapper no longer performs key conversion, no longer
  pretty prints and no longer supports converting dates.

## [0.1.2] — 2019-09-10

### Added

- Better handling of `nil` on encode and `nil`/`""` on decode.

## [0.1.1] — 2019-09-10

### Fixed

- Incorrect method used for no-args version of `new-json-decoder`.

## [0.1.0] — 2019-09-10

### Added

- A `*default-object-mapper*`.
- No-args versions of `new-json-encoder` and `new-json-decoder` that use the
  default `ObjectMapper`.

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

[0.1.0]: https://github.com/b-social/jason/compare/0.0.4...0.1.0

[0.1.1]: https://github.com/b-social/jason/compare/0.1.0...0.1.1

[0.1.2]: https://github.com/b-social/jason/compare/0.1.1...0.1.2

[0.1.3]: https://github.com/b-social/jason/compare/0.1.2...0.1.3

[0.1.4]: https://github.com/b-social/jason/compare/0.1.3...0.1.4

[0.1.5]: https://github.com/b-social/jason/compare/0.1.4...0.1.5

[1.0.0]: https://github.com/logicblocks/jason/compare/0.1.5...1.0.0
[Unreleased]: https://github.com/logicblocks/jason/compare/1.0.0...HEAD
