# Client Architecture

This document defines the target architecture for the LibGDX desktop client.


## Goals

- keep rendering, UI, input, networking, and world state separate
- keep screen classes thin
- let controllers react to user actions without owning rendering
- keep the client presentation-focused while the server stays authoritative
- make UI, assets, and dialogue easier to change without rewriting large classes

## Dependency Direction

```text
screens -> controllers -> service -> network
screens -> ui
screens -> render
controllers -> world / service
render -> world / model / assets
ui/render -> ui/theme
ui -> controllers
world -> shared
network -> shared
```

Rules:

- `ui` never opens sockets
- `render` never decides gameplay rules
- `controllers` never draw
- `screens` orchestrate and delegate
- `service` owns app/session/audio/asset work, not per-frame gameplay rules

## Folder Structure

```text
client/src/main/java/com/game/client/
├── app/                    # boot, lifecycle, client config, shared context
├── screens/                # full-screen states and transitions
├── ui/                     # reusable client UI
│   ├── core/               # UI orchestration and composition primitives
│   ├── components/         # widgets, panels, HUD pieces
│   ├── layouts/            # bounds and layout helpers
│   ├── render/             # shared UI drawing primitives and render-state helpers
│   └── theme/              # palette, fonts, theme constants
├── controllers/            # reacts to user actions and screen events
├── render/                 # draw-only world and world-overlay rendering helpers
│   ├── core/               # shared render context, camera helpers, contracts
│   ├── world/
│   │   ├── entity/         # entity and character rendering
│   │   ├── map/            # tile maps, zones, terrain layers
│   │   ├── effect/         # particles, weather, transient visual effects
│   │   └── debug/          # hitboxes, nav debug, diagnostics
│   └── overlay/            # HUD, damage text, screen overlays
├── input/                  # raw input, bindings, action mapping
├── network/                # sockets, packet routing, codecs, connection services
│   ├── core/               # shared networking primitives and connection state
│   ├── auth/               # auth server API and auth packet routing
│   │   └── handler/
│   ├── world/              # world/game server API and world packet routing
│   │   └── handler/
│   ├── codec/              # client-side packet encoding/decoding
│   └── socket/             # low-level socket implementation
├── world/                  # client-side world view, sync, interpolation, prediction
│   ├── ecs/
│   ├── sync/
│   ├── system/
├── service/                # auth/session/audio/assets/persistence-adjacent client services
├── model/                  # client-only state shapes
└── settings/               # local client settings and config readers
```

## Folder Responsibilities

| Folder | Responsibility | Must Never Own |
| :--- | :--- | :--- |
| `app/` | lifecycle, startup wiring, shared client context | world rules, screen layout details |
| `screens/` | compose one app state and delegate | raw networking logic, heavy rendering code |
| `ui/` | visible widgets, panels, shared UI composition | sockets, authoritative world logic |
| `controllers/` | react to clicks, keys, and screen actions | drawing, low-level socket code |
| `render/` | draw world scene and world overlays | business flow, session state |
| `input/` | convert devices into typed actions | UI layout, networking |
| `network/` | talk to the server, encode/decode packets | UI state, gameplay decisions |
| `world/` | local world copy, prediction, interpolation, NPC presentation state | direct UI manipulation |
| `service/` | auth/session/audio/assets and app-wide tasks | per-frame ECS gameplay rules |
| `model/` | client-only data | rendering framework types mixed with logic |

## Naming Rules

- `Screen`: full-page state such as `LoginScreen`
- `Panel`: a reusable UI section such as `InventoryPanel`
- `Widget` or component name: the smallest reusable control such as `TextFieldWidget`
- `Controller`: reacts to user actions and delegates, such as `LoginController`
- `Service`: app/network/session/audio/assets work, such as `SessionService`
- `System`: per-frame client update logic, such as `InterpolationSystem`
- `Renderer`: draw-only class, such as `SceneRenderer`
- `Manager`: only when a class truly owns many objects or transitions
- `Handler`: one reaction to one packet or event
- `Applier`: maps incoming snapshots into client state
- `Loader`: reads data files into plain data objects

## Practical Rules

- `GameScreen` should not permanently own input orchestration, UI updates, world sync, and rendering all at once
- move user action flow into `controllers/`
- move draw code into `render/`
- move sync/interpolation into `world/`
- move reusable UI into `ui/components/`

## Network Direction

Prefer this shape:

```text
network/
├── core/
│   ├── ClientConnection.java
│   ├── ConnectionState.java
│   ├── ServerEndpoint.java
│   ├── NetworkClient.java
│   └── NetworkError.java
├── auth/
│   ├── AuthClient.java
│   ├── AuthConnection.java
│   ├── AuthPacketRouter.java
│   └── handler/
├── world/
│   ├── WorldClient.java
│   ├── WorldConnection.java
│   ├── WorldPacketRouter.java
│   └── handler/
├── codec/
│   ├── ClientPacketEncoder.java
│   ├── ClientPacketDecoder.java
│   └── PacketCodecRegistry.java
└── socket/
    ├── SocketClient.java
    ├── SocketListener.java
    └── TcpSocketClient.java
```

Rules:

- keep auth and world packet handling separate
- keep low-level transport in `socket/`
- keep packet encode/decode in `codec/`
- only introduce extra `sender/` folders if packet sending becomes duplicated enough to justify it

## Render Direction

Prefer this shape:

```text
render/
├── core/
├── world/
│   ├── entity/
│   ├── map/
│   ├── effect/
│   └── debug/
└── overlay/
```

Rules:

- split `render/world/` only when those concerns actually have their own classes
- `overlay/` is for world HUD, target panels, floating labels, and on-screen presentation tied to gameplay view
- renderers draw only; they do not apply world state mutations
- reusable menu/auth UI drawing belongs in `ui/render/`, not under `render/overlay/`

## UI Direction

Use this split:

- `screens/` owns lifecycle and screen-level composition
- `ui/core/` owns shared UI orchestration and composition helpers
- `ui/components/` owns widgets, panels, and HUD pieces
- `ui/layouts/` owns layout math and bounds helpers
- `ui/render/` owns shared UI drawing primitives and `UiRenderState`
- `ui/theme/` owns fonts, palette, and theme constants

Keep it simple:

- do not introduce XML/DSL markup just for the sake of it
- if UI composition in Java becomes painful later, add a loader-driven layer then

## Client Resources

Client-owned resources should live under:

```text
client/src/main/resources/assets/
├── ui/
│   ├── fonts/
│   ├── themes/
│   └── animations/
├── textures/
├── sprites/
├── sounds/
├── music/
├── maps/
└── dialogue/
```

Rules:

- `assets/ui/` is for client UI-only resources
- `dialogue/` can be data-driven even if the runtime controller stays in Java
- only add new folders when real assets exist

## NPC Placement

NPC-related client code should not live at the top level.

Prefer:

- `world/npc/dialogue/` for client dialogue runtime and loaders
- `world/npc/hint/` for client-only visual behaviour hints

Rule:

- authoritative NPC AI belongs on the server
- the client only owns NPC presentation, dialogue flow, and visual hinting

## Examples

Login flow:

1. `LoginScreen` shows the panel and forwards actions
2. `LoginController` validates and calls `AuthService`
3. `AuthService` uses `network/auth/`
4. response updates screen state and triggers screen transition

World flow:

1. `GameScreen` orchestrates scene + HUD
2. `input/` emits actions
3. world controller/service sends intents through `network/world/`
4. snapshot appliers update `world/`
5. renderers draw from client world state
