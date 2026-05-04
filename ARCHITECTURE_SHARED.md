# Shared Architecture

This document defines the target architecture for the shared module.

It uses `Option2_shared` as the base because it is the clearest and safest version.

## Goals

- keep shared independent from LibGDX and server libraries
- define packets, DTOs, enums, constants, and pure utility types once
- provide a stable protocol contract for both client and server
- avoid duplicating transport data or protocol rules

## Dependency Direction

```text
client -> shared
server -> shared
shared -> no client-only or server-only framework code
```

Rules:

- `shared` must compile without the client or the server
- `shared` may describe data used by both sides
- `shared` must not contain gameplay systems, repositories, UI, or socket implementations

## Folder Structure

```text
shared/src/main/java/com/game/shared/
├── ids/                    # shared identifiers such as SharedEntityId
├── math/                   # transport-safe math/value types such as Vec2
├── protocol/               # packet contracts and protocol enums
│   ├── core/               # Packet, Opcode, protocol framing values
│   ├── auth/               # login/register/character packets
│   ├── world/              # movement, spawn, combat, interaction packets
│   └── error/              # shared error packets/codes
├── time/                   # shared clock or tick-rate types
└── util/                   # pure helpers only
```

Rule:

- keep packet records under `protocol/...` because that is the structure the repo already uses
- only add extra top-level folders like `dto/` or `constants/` when the codebase truly needs them

## Naming Rules

- `Packet`: transport message, especially while the current repo still uses packet-oriented naming
- `Request`: client to server when a request/response pair is explicit
- `Response`: direct reply from server
- `Update` or `Notification`: server push
- `Id`: shared identifier wrapper
- `Constants`: grouped immutable constants when a dedicated constants type becomes necessary

Examples:

- `LoginRequest`
- `LoginResponse`
- `PlayerSnapshotUpdate`
- `CharacterDto`
- `ProtocolVersion`

## What Belongs Here

- packet ids and protocol version values
- transport-safe DTOs
- shared enums like direction or error codes
- pure constants
- pure math/byte/validation helpers
- shared identifiers such as entity ids
- packet records for combat, interaction, spawning, and snapshots

## What Does Not Belong Here

- LibGDX classes such as textures, stages, cameras
- server sockets, channels, repositories, SQL entities
- password hashes, session state, account secrets
- authoritative systems like combat, AI, movement
- client screens, UI widgets, renderers

## Packet Rules

Every packet should be plain data.

- packets do not serialize themselves
- codecs live in client/server modules
- packet ids stay stable
- retired ids are never reused
- packet classes should remain free of LibGDX, sockets, or database concerns

Suggested ranges:

- `1xx` auth
- `2xx` world
- `3xx` combat
- `4xx` chat
- `5xx` dialogue

## Protocol Versioning

- bump the protocol version for breaking packet changes
- keep compatibility checks near handshake/login
- let client and server reject mismatched builds safely

## Request vs Command

This boundary is important:

- `shared`: `AttackIntentRequest`
- `server`: `AttackCommand`

Transport belongs in `shared`.
Validated authoritative execution belongs in `server`.
