# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.1.2] - 2025-12-20
### Fixed
- NoSuchMethodError crash on 1.21.11

## [2.1.1] - 2025-10-02
### Fixed
- Escaping not always working in commands

## [2.1.0] - 2025-06-29
### Changed
- Use stonecutter to support multiple versions
- Bundle fabric-permission-api
- Improved error messages

### Fixed
- Inconsistent command argument resolution 

## [2.0.1] - 2025-02-03
### Added
- Command exception debugger

### Fixed
- Vec2 argument type
- Vec3 argument type
- Brigadier argument parsing for empty arguments

## [2.0.0+beta.2] - 2024-10-04
### Added
- Path node cache for improved performance

## [2.0.0+beta.1] - 2024-09-20

Reworked command configuration _(backwards compatible)_ and replaced command requirements with a more powerful command modifier system

### Added
- Command modifiers
- Command node redirects
- Command to determine the command node paths

### Removed
- Command requirements