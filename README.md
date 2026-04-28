# Game

Phase 0 skeleton for a 2D orthogonal top-down multiplayer RPG in Java using Gradle and LibGDX later.

## Baseline

- Build tool: Gradle
- Java target: Java 25
- Recommended runtime strategy: target Java 25 LTS across development and builds
- Architecture: `shared` + `server` + `client`
- Server: authoritative ECS
- Client: visual ECS + prediction
- Protocol: shared module
- Data: JSON/YAML first
- Database: PostgreSQL
- Networking: TCP first, UDP later if needed

## Java Version Policy

Use Java 25 as the project baseline. Keep the Gradle toolchain and local development environment aligned to Java 25 unless the project deliberately decides to downgrade or retarget later.

## Current Scope

This phase is structure only.

- Multi-module Gradle repository
- Package directories for future code
- Config, data, logs, tools, and resource folders
- No gameplay implementation yet
- No `.java` classes generated yet

## Project Layout

```text
game/
├── settings.gradle
├── build.gradle
├── gradle.properties
├── README.md
├── config/
├── data/
├── logs/
├── tools/
├── shared/
├── server/
└── client/
```

## Notes

- The package tree is scaffolded to match the intended architecture, but source files are intentionally absent.
- LibGDX setup is planned for the client phase, not this skeleton phase.
- Empty directories are tracked with `.gitkeep` files where needed.
- The development roadmap lives in `ROADMAP.md` to keep this front page focused.

## Development

```bash
./gradlew build
./gradlew projects
```
