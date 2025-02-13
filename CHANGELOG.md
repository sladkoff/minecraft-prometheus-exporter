# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v3.1.2] - 2025-02-13

### Fixed
- [#287](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/287): Async metrics are not awaited properly

## [v3.1.1] - 2025-02-11

### Fixed
- [#285](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/285): Do not pin threads when handling sync metrics

### Changed
- Updated internal dependencies

## [v3.1.0] - 2024-03-01

### Added
- [#113](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/113): Add support for health checking

## [v3.0.0] - 2024-02-29

### Added
- [#198](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/198): Add rudimentary support for Folia (see README.md for limitations)

### Changed
- [#228](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/228): [Breaking] Update minimum Java version to 17

### Fixed
- [#155](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/155): World size metric lags server with big world
- [#208](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/208): Failed to find tick times buffer via reflection in 1.20.4
- [#226](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/226): Server lags when exporting chunk and player metrics

## [v2.6.0] - 2024-01-02

Special thanks to all new and old contributors :star:

### Added
- [#176](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/176): Add whitelisted players count metric `mc_whitelisted_players`

### Changed
- [#157](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/157): Change default port for new installations

### Fixed
- [#197](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/197): Plugin crashes on Azul JVM

## [v2.5.0] - 2022-04-11

Special thanks to all new and old contributors :star:

### Added
- [#128](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/128): New world size metric `mc_world_size`

## [v2.4.3] - 2022-04-11

### Fixed

Special thanks to all new and old contributors :star:

- [#125](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/125):
  Fix accumulating unloaded worlds
- [#139](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/139): Update json-path
- [#140](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/140): Update prometheus-client
- [#141](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/141): Update Jetty
- [#143](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/143):
  Fix warn logs due to collection of 'unknown' statistics

## [v2.4.2] - 2021-06-25

### Fixed

- [#97](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/97): 
  Player stats files are loaded even if 'player_statistic' is disabled
- [#91](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/91): Update Jetty
- [#87](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/87): Update json-path
- [#86](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/86): Update prometheus-client

## [v2.4.1] - 2021-05-20

Special thanks to all new and old contributors :star:

### Fixed

- [#74](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/74): Excessive logging
- [#75](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/75): Crash on startup
- [#77](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/77): Exception log on startup
- [#71](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/71): Update Jetty

## [v2.4.0] - 2021-03-07

Special thanks to all new and old contributors :star:

### Fixed

- [#32](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/32): player_statistic not persisted over server restarts

### Changes

- Updated internal dependencies

## [v2.3.0] - 2021-01-08

Special thanks to all new and old contributors :star:

### Fixed
- [#46](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/46): Inaccuracies in metrics `mc_entities_total` and `mc_villagers_total` 
- [#53](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/53): Opt-out of legacy material support to improve performance

### Changes
- [#52](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/52): Update Jetty to fix security vulnerabilities

## [v2.2.0] - 2020-05-16

Special thanks to all new and old contributors :star:

### Added
- [#33](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/33): New villager metric `mc_villagers_total`
- [#41](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/39): New server tick duration metrics `mc_tick_duration_*`
- [#42](https://github.com/sladkoff/minecraft-prometheus-exporter/pull/42): gzip transport support

### Fixed
- [#23](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/23): TPS inaccuracy in `mc_tps` 

## [v2.1.1] - 2020-04-12

### Fixed
- [#38](https://github.com/sladkoff/minecraft-prometheus-exporter/issues/38): Exception `NoSuchMethodError` during metric collection on server versions < 1.14 

## [v2.1.0] - 2020-03-23

### Added
- New JVM threads metrics `mc_jvm_threads_*`
- New JVM GC metrics `mc_jvm_gc_*`
### Changed
- Metric `mc_entities_total` has new labels `type`, `alive`, `spawnable`.
- Metric `mc_jvm_memory` has new label `allocated`.
### Removed
- Metric `mc_living_entities_total` -> use `mc_entities_total` instead.

## [v2.0.1] - 2020-01-03
### Fixed
- Error `Failed to read player statistic: Label cannot be null.` on metrics `player_online` and `player_statistic`. 
  If the server doesn't return a name for a player, the player's UID is used as label instead. (https://github.com/sladkoff/minecraft-prometheus-exporter/issues/17)

### Added
- Labels `uid`/`player_uid` for metrics `player_online` and `player_statistic` in addition to the player name.

## [v2.0.0] - 2019-12-29
### Changed
- :warning: [Breaking] Metric `mc_players_total` no longer has a `state` label. It exports only the number of unique players now. 
  Use `mc_players_online_total` for online player count.

### Added
- Metrics can be enabled individually via config `enable_metrics`. Since the format of the config.yml has changed it is advised to delete your existing file to 
  generate a new clean one. 

## [v1.3.0] - 2019-05-28
### Added
- This changelog
- Add `host` config property
### Changed
- Listen on `localhost` interface by default

## [v1.2.0] - 2018-11-05
### Added
- Experimental player statistics

## [v1.1.0] - 2017-03-18
### Added
- Export tickrate (TPS)

## [v1.0.1] - 2017-02-19
### Fixed
- Concurrency issues
### Changed
- Return 404 on unsupported request URI's

## [v1.0.0] - 2017-02-14
### Added 
- Export JVM memory usage


## v0.1.0 - 2017-02-09
### Added
- Initial exporter

[v3.0.0]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v2.6.0...v3.0.0
[v2.6.0]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v2.5.0...v2.6.0
[v2.5.0]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v2.4.2...v2.5.0
[v2.4.2]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v2.4.1...v2.4.2
[v2.4.1]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v2.4.0...v2.4.1
[v2.4.0]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v2.3.0...v2.4.0
[v2.3.0]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v2.2.0...v2.3.0
[v2.2.0]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v2.1.1...v2.2.0
[v2.1.1]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v2.1.0...v2.1.1
[v2.1.0]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v2.0.1...v2.1.0
[v2.0.1]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v2.0.0...v2.0.1
[v2.0.0]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v1.3.0...v2.0.0
[v1.3.0]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v1.2.0...v1.3.0
[v1.2.0]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v1.1.0...v1.2.0
[v1.1.0]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v1.0.1...v1.1.0
[v1.0.1]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v1.0.0...v1.0.1
[v1.0.0]: https://github.com/sladkoff/minecraft-prometheus-exporter/compare/v0.1.0...v1.0.0
