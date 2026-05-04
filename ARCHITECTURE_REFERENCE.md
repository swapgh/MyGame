# Architecture Reference

This is the single structure reference for the project.

Detailed module guides:

- `ARCHITECTURE_CLIENT.md`
- `ARCHITECTURE_SERVER.md`
- `ARCHITECTURE_SHARED.md`

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
│   ├── ui/             core/ + layouts/ + components/ + render/ + theme/
│   ├── render/         core/ + world/SceneRenderer + overlay/WorldHudRenderer
│   ├── input/          InputManager, KeyBindings, KeyboardInput, MouseInput, TextInputBuffer, WorldInputFrame
│   ├── network/        socket/ + packet/ + auth/ + world/codec/ + PingService
│   ├── service/        AuthService, WorldService, WorldActionService
│   ├── model/          AuthenticatedSession, WorldFrameState, UI-facing state
│   ├── world/          sync/ + ecs/ + npc/
│   └── settings/       ClientConfig
│
├── server/src/main/java/com/game/server/
│   ├── auth/           app/ + config/ + login/ + registration/ + characters/ + sessions/ + security/ + database/ + network/
│   ├── world/          app/ + config/ + loop/ + map/ + commands/ + factories/ + inventory/ + network/ + snapshot/ + tick/
│   ├── ecs/            entity/ + component/ + query/ + store/ + system/ + WorldContext
│   ├── components/     world/ + combat/ + inventory/ + npc/ + loot/
│   ├── systems/        world/ + combat/ + npc/ + loot/
│   ├── items/          definition/
│   ├── npc/            definition/
│   ├── loot/           definition/
│   ├── database/       DatabasePool, DatabaseConfig, TransactionManager
│   ├── command/        validated server-only intents
│   ├── service/        use-case coordination
│   ├── security/       auth/token/password helpers
│   └── config/         ServerConfigLoader
│
└── shared/src/main/java/com/game/shared/
    ├── math/           Vec2, Bounds, Direction
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

## Client UI Direction

For LibGDX client screens, prefer this split:

- `screens/` owns the screen lifecycle and delegates
- `controllers/` owns flow and server-facing actions
- `ui/core/` owns document rendering, shared UI orchestration, and screen composition primitives
- `ui/layouts/` computes bounds and alignment
- `ui/components/` owns interactive controls and reusable framed sections
- `ui/render/` owns shared UI drawing primitives and render-state helpers
- `ui/theme/` owns fonts, palette, and theme constants

Keep it simple:

- do not invent a DSL or external markup format unless the Java composition layer becomes a real bottleneck
- favor a small document/composition layer over pushing layout, rendering, and input back into each screen

## Client Resource Direction

Client-owned visual resources should live under `client/src/main/resources/assets/ui/`.

Suggested subfolders:

- `assets/ui/fonts/`
- `assets/ui/themes/`
- `assets/ui/animations/`

Rule:

- only create new buckets when we actually have assets for them
