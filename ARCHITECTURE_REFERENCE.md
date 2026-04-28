# Architecture Reference

This is the single structure reference for the project.

It describes:

- where code should live
- how data folders are used
- when a class should be split
- the target compact structure we want to preserve

## Rules For Splitting Classes

### Screen

Split a screen if:

- it contains business logic or direct service calls
- it owns state beyond what is visible
- it grows beyond roughly 150 lines of render or layout logic

Rule:

- a screen should draw and delegate

Example:

- `GameScreen` should not permanently own world orchestration, input handling, and rendering logic all at once
- move non-render flow into `WorldScreenController` as needed

### Controller

Split a controller if:

- it handles more than one independent business flow
- it grows beyond roughly 200 lines

### Handler or Command

Split a handler if:

- it validates, executes, responds, and persists all in one place
- it grows beyond roughly 80 lines

Rule:

- commands should validate and delegate

### Service

Split a service if:

- it mixes unrelated domains
- it contains multiple workflows that can evolve separately

Example:

- if `InventoryService` grows too much, pickup and equipment logic can become separate services

### System

Split a system if:

- it iterates over too many unrelated component groups
- it starts mixing separate simulation concerns

### Repository or DAO

Split a DAO if:

- it queries unrelated tables or unrelated aggregates

### Loader

Split a loader if:

- it reads more than one schema
- it reads more than one file format

### Renderer

Split a renderer if:

- it draws fundamentally different responsibilities

Rule:

- scene and HUD rendering should stay separate

## Data Layout

```text
data/
├── client/
│   ├── animations/
│   ├── sprites/
│   ├── textures/
│   ├── sounds/
│   ├── music/
│   └── ui/
├── items/
│   ├── item-definitions.json
│   └── loot-tables.json
├── npcs/
│   ├── npc-definitions.json
│   └── spawn-tables.json
├── world/
│   ├── maps/
│   ├── zones/
│   └── navmesh/
└── quests/
    └── quest-definitions.json
```

Rule:

- `data/client/` is read by the client at runtime
- `data/items`, `data/npcs`, `data/world`, and `data/quests` are authoritative server-side startup data
- the client should not directly own gameplay truth from those server data folders

## Target Compact Structure

```text
game/
├── client/src/main/java/com/game/client/
│   ├── app/            ClientMain, GameClient, ClientState, ClientStateMachine, ClientShutdownHook
│   ├── screens/        Screen + auth/ + world/ + menu/
│   ├── controllers/    auth/ + world/
│   ├── ui/             ScreenController
│   ├── render/         ClientUiRenderer, ClientUiPalette, UiFont + world/SceneRenderer, HudRenderer
│   ├── input/          InputManager, KeyBindings, KeyboardInput, MouseInput, TextInputBuffer, WorldInputFrame
│   ├── network/        socket/ + packet/ + auth/ + world/codec/ + PingService
│   ├── world/          sync/ + ecs/
│   └── settings/       ClientConfig
│
├── server/src/main/java/com/game/server/
│   ├── auth/           app/ + config/ + login/ + registration/ + characters/ + sessions/ + security/ + database/ + network/
│   ├── world/          app/ + config/ + loop/ + map/ + commands/ + factories/ + inventory/ + network/
│   ├── ecs/            entity/ + component/ + system/ + WorldContext
│   ├── components/     world/ + combat/ + inventory/ + npc/ + loot/
│   ├── systems/        world/ + combat/ + npc/ + loot/
│   ├── items/          definition/
│   ├── npc/            definition/
│   ├── loot/           definition/
│   ├── database/       DatabasePool, DatabaseConfig, TransactionManager
│   └── config/         ServerConfigLoader
│
└── shared/src/main/java/com/game/shared/
    ├── math/           Vec2, Vec3, Bounds, Direction
    ├── ids/            SharedEntityId
    ├── protocol/       core/ + auth/ + world/ + error/
    ├── time/           GameClock, TickRate
    └── util/           Result, Validation
```

## Working Rule For New Files

When adding a class, ask:

1. Is this authoritative gameplay logic?
2. Is this client-only visual logic?
3. Is this shared protocol or pure utility logic?

Put it in the module that owns that answer.
