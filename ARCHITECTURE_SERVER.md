# Server Architecture

This document defines the target architecture for the authoritative game server.

## Goals

- keep the server authoritative
- separate low-level networking from gameplay logic
- keep handlers thin
- keep systems focused on per-tick rules
- keep persistence outside the game loop
- make NPC behaviour and dialogue easier to extend

## Dependency Direction

```text
net -> handler -> service
service -> repository
service -> session
service -> world
world -> ecs
system -> ecs / world / npc
handler -> command
repository -> database
server -> shared
shared -> never server
```

Rules:

- `net` knows bytes and connections, not gameplay rules
- `handler` validates and delegates
- `service` coordinates use cases
- `system` owns authoritative per-tick logic
- `repository` owns persistence only
- `shared` owns packets and DTOs, never server logic

## Folder Structure

```text
server/src/main/java/com/game/server/
├── app/                    # entry points, bootstrap, config wiring
├── net/                    # sockets, framing, packet router, codecs
│   └── codec/
├── handler/                # one class per packet type or request type
├── service/                # high-level use cases across world/repos/sessions
├── command/                # validated server-only commands derived from requests
├── system/                 # authoritative tick-based gameplay rules
├── ecs/                    # entity/component storage and ECS support types
│   ├── component/          # pure runtime data attached to entities
│   ├── query/              # reusable ECS queries and selection helpers
│   ├── store/              # component stores and entity registries
│   └── system/             # ecs-driven authoritative logic if kept under ecs/
├── session/                # account/session/connection mapping
├── repository/             # CRUD and persistence gateways
│   └── db/
├── world/                  # active world containers, zones, registries
│   ├── map/
│   ├── snapshot/
│   └── tick/
├── model/                  # server-only models
├── security/               # password hashing, tokens, permissions, rate limits
└── config/                 # server-readable definitions and boot config
```

## Folder Responsibilities

| Folder | Responsibility | Must Never Own |
| :--- | :--- | :--- |
| `app/` | startup order, wiring, config bootstrap | gameplay rules, SQL logic |
| `net/` | bytes in, bytes out, routing to handlers | combat, inventory, DB access |
| `handler/` | bridge one request type into service/command calls | fat business logic, entity loops |
| `service/` | use-case coordination | direct per-frame entity loops |
| `command/` | validated server-only intents consumed by systems | shared packet contracts |
| `system/` | authoritative per-tick rules | sockets, SQL, UI concerns |
| `ecs/` | runtime entity/component data structures, queries, and ECS support | login flow, persistence orchestration |
| `session/` | map connections to authenticated players | gameplay rules |
| `repository/` | persistence only | gameplay mechanics |
| `world/` | active world state, snapshots, tick loop coordination | auth, packet parsing |
| `model/` | server-only models such as accounts and templates | shared transport concerns |
| `security/` | auth and permission helpers | world simulation |

## Request vs Command

Keep this boundary explicit:

- `shared`: `MoveIntentRequest`
- `server`: `MoveCommand`

Rules:

- requests are transport data coming from the client
- commands are validated server-side intents ready for the world loop
- handlers transform requests into commands or service calls

## Naming Rules

- `Handler`: reacts to one packet or request type
- `Router`: dispatches packet ids to handlers
- `Service`: orchestrates a use case across systems/repos/sessions
- `System`: authoritative tick logic
- `Repository`: persistence gateway for one aggregate
- `Session`: one connection context or account/player mapping
- `Behaviour`: one NPC AI state or strategy
- `Loader`: reads definitions from disk
- `Command`: validated server-only intention

## Hard Lines

- no SQL inside `system/`
- no direct socket code inside `system/`
- no game rules inside `net/`
- no large gameplay logic inside `handler/`
- no password hashes or server secrets in `shared`

## ECS Direction

The server ECS is the runtime heart of the game.

Use ECS for:

- entity state that changes during play
- per-tick authoritative rules
- data that applies to some entities but not others
- rules that need to scale across many entities consistently

Prefer this split:

```text
ecs/
├── component/      # data only
├── query/          # reusable entity selection helpers
├── store/          # component stores and entity registries
└── system/         # systems that operate on ECS data
```

Rules:

- components are data only
- systems contain per-tick logic
- queries help systems find matching entities cleanly
- stores own component access and entity registration

Examples:

- `PositionComponent`
- `VelocityComponent`
- `HealthComponent`
- `InventoryComponent`
- `NpcAiComponent`
- `PathfindingComponent`

Good component:

```java
public record PositionComponent(float x, float y) {
}
```

Bad component:

```java
public final class PositionComponent {
    public void sendSnapshot() { }
}
```

That kind of networking behaviour belongs elsewhere.

## When To Use Component vs System vs Service

Use a `Component` when:

- the data belongs to one entity
- the data may exist on some entities but not others
- the data is part of runtime world state

Use a `System` when:

- the rule runs every tick or every world update
- the rule reads/writes entity components
- the rule applies across many entities

Use a `Service` when:

- the work coordinates repositories, sessions, commands, or world entry
- the work is request-driven rather than tick-driven
- the work is application/business flow, not simulation flow

Examples:

- login = `AuthService`
- entering the world = `WorldService`
- movement validation each tick = `MovementSystem`
- NPC chasing a target each tick = `NpcAiSystem`
- health values on entities = `HealthComponent`

## ECS-First NPC Direction

For the current project phase, keep NPC runtime behaviour inside ECS.

Prefer:

- `ecs/component/NpcAiComponent`
- `ecs/component/PathfindingComponent`
- `ecs/system/NpcAiSystem`
- `ecs/system/PathfindingSystem`

Rules:

- AI state is entity state, so it belongs naturally in components
- pathfinding data can also live as component data when it varies per entity
- per-tick AI and pathfinding rules belong in systems
- do not create a separate top-level `npc/` area until NPC-specific code becomes large enough to justify it

Later, if NPC-specific code grows substantially, it can be extracted into a more explicit area such as:

- `entities/npc/`
- or `world/npc/`

But for now, ECS is the right home.

## Practical Placement Guide

When adding new code, ask:

1. Is this per-entity runtime state?
   Put it in `ecs/component/`.
2. Is this a rule that runs each tick?
   Put it in `ecs/system/` or `system/`.
3. Is this request/use-case coordination?
   Put it in `service/`.
4. Is this one packet reaction?
   Put it in `handler/`.
5. Is this persistence?
   Put it in `repository/`.
6. Is this world-specific runtime organization such as snapshots or tick coordination?
   Put it in `world/`.

## Example Flows

Login flow:

1. `net/` decodes `LoginRequest`
2. router dispatches to `LoginPacketHandler`
3. handler calls `AuthService`
4. `AuthService` uses `AccountRepository` and `SessionManager`
5. response goes back through the network layer

Movement flow:

1. client sends `MoveIntentRequest`
2. handler validates session and creates `MoveCommand`
3. command enters the world loop
4. `MovementSystem` applies speed/collision rules
5. snapshot broadcast system sends updated state
