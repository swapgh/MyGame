# Architecture Reference

This file is a guide for where code should generally live.

It is a reference, not a strict rulebook.

Some classes already in the repo are temporary, experimental, or simplified for the current phase. They can stay for now if they help us move forward, but new work should try to follow this structure unless there is a good reason not to.

## Runtime Baseline

- Build tool: Gradle
- Java version: JDK 25 toolchain
- Architecture: `shared` + `server` + `client`
- Server model: authoritative ECS
- Client model: visual ECS + prediction
- Protocol contract: `shared` module
- Data files: JSON/YAML first
- Database: PostgreSQL
- Networking: TCP first, UDP later only if it becomes necessary

## Ownership Rules

- `shared`: protocol, math, time, ids, pure utility code shared by server and client
- `server`: authoritative world state, combat, movement, collision, persistence, NPC AI, rules
- `client`: input, prediction, interpolation, rendering, UI, visual-only entity state

## Important Distinction

Client `components` and `systems` are allowed.

They are not authoritative gameplay logic.

Examples:

- Server `HealthComponent`: real gameplay health used to resolve combat
- Client `HealthBarComponent`: display-only information used to draw UI
- Server `CollisionSystem`: decides legal movement
- Client `InterpolationSystem`: smooths visual movement between snapshots

## Current Repo Shape

The repo is still early-stage, so not every folder is fully populated yet.

That is expected.

The goal is:

1. keep current working files
2. add missing scaffolding where it helps
3. move toward the target structure over time

## Client Folder Intent

### `client/app`

High-level app bootstrap and client lifecycle.

Examples:

- `ClientMain`
- `GameClient`
- `ClientState`
- `ClientStateMachine`

### `client/network`

Socket and packet flow.

Examples:

- `AuthClient`
- `WorldClient`
- `ServerConnection`
- packet codecs and routers

### `client/input`

Keyboard, mouse, bindings, and input-to-command translation.

Examples:

- `InputManager`
- `KeyboardInput`
- `MouseInput`
- `KeyBindings`
- `WorldInputFrame`
- `TextInputBuffer`

### `client/ecs`

Client-side ECS primitives for visual entities.

Examples:

- `ClientEntityId`
- `ClientEntityManager`
- `ClientComponentStore`
- `ClientSystem`
- `ClientSystemScheduler`

### `client/components`

Visual or prediction-side entity state.

Examples:

- `TransformComponent`
- `VelocityComponent`
- `InterpolationComponent`
- `PredictionComponent`
- `RenderableComponent`
- `NameplateComponent`
- `WorldEntityRenderState`

### `client/systems`

Client-side processing of visual state.

Examples:

- `SnapshotApplySystem`
- `InterpolationSystem`
- `PredictionSystem`
- `RenderSystem`
- `HudSystem`

### `client/ui`

HUD, widgets, screen navigation, and UI helpers.

Examples:

- `ScreenManager`
- `WorldHudRenderer`
- future UI managers and widgets

### `client/screens`

Actual LibGDX screens.

These should orchestrate behavior, not contain all logic forever.

### `client/world`

Client-side world models, snapshot-derived state, tile/map helpers.

Examples:

- `WorldViewModel`
- future map or zone view classes

## Server Folder Intent

### `server/auth`

Authentication server flow.

### `server/world`

Authoritative world simulation.

Main rule:

- real gameplay rules belong here

Examples:

- `MovementSystem`
- `CollisionSystem`
- `CombatSystem`
- `SnapshotSystem`

## Shared Folder Intent

### `shared/protocol`

Packets, opcodes, packet validation, packet registration.

### `shared/math`

Pure math types.

### `shared/time`

Simulation clock types.

### `shared/ecs`

Shared ids or protocol-facing ECS helpers only.

## Working Rule For New Files

When adding a class, ask:

1. Is this authoritative gameplay logic?
2. Is this client-only visual logic?
3. Is this protocol/shared contract?

Put it in the module that owns the answer.
