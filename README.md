# Game

Java multiplayer RPG prototype built with Gradle, LibGDX, a shared protocol module, an auth server, and a world server.

## Stack

- Java 21
- Gradle multi-module build
- LibGDX desktop client
- authoritative server simulation
- JSON and YAML data/config files
- PostgreSQL-oriented persistence layer

## Modules

- `client`: LibGDX desktop client, UI, input, prediction, interpolation, rendering
- `server`: auth server, world server, authoritative gameplay logic
- `shared`: protocol, math, time, and shared utility types

## Runtime Layout

```text
game/
├── client/
├── server/
├── shared/
├── config/
├── data/
├── tools/
└── README.md
```

Client UI assets live under `client/src/main/resources/assets/ui/`, with room for `fonts/`, `themes/`, and later `animations/` when the client needs them.

## Commands

```bash
./gradlew build
./gradlew test
./gradlew :client:runClient
./gradlew :server:runAuthServer
./gradlew :server:runWorldServer
```

## Environment Notes

This repo works on Ubuntu + VS Code and Windows + IntelliJ.

- Gradle is the source of truth.
- The project expects JDK 21.
- Use the Gradle wrapper included in the repo instead of installing Gradle manually.
- VS Code uses `.vscode/launch.json` as a convenience.
- IntelliJ should open the repo as a Gradle project.

The full setup and troubleshooting guide lives in `GUIDE.md`.

## Structure Reference

The canonical structure guide lives in `ARCHITECTURE_REFERENCE.md`.
Detailed module baselines live in `ARCHITECTURE_CLIENT.md`, `ARCHITECTURE_SERVER.md`, and `ARCHITECTURE_SHARED.md`.
