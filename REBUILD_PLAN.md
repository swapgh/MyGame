# Rebuild Plan

This project will be rebuilt in small playable slices.

Goal of the rebuild:

- clean module boundaries
- clean class responsibilities
- no mixed screen/network/world logic
- one enemy
- one vendor NPC
- `WASD` movement
- `E` interact with vendor when in range
- `TAB` target cycling
- auto-target when attacking with no current target
- `E` attack when a hostile target is selected and valid

## Construction Rules

Use these as the build rules for every phase:

- `ARCHITECTURE_CLIENT.md`
- `ARCHITECTURE_SERVER.md`
- `ARCHITECTURE_SHARED.md`

Naming rules:

- `UI` shows things
- `Controller` reacts to user actions
- `Service` handles app/network/session/save work
- `System` runs simulation or per-frame/per-tick logic
- `Renderer` draws
- `Model` stores data
- `Manager` owns many objects or transitions
- `Handler` reacts to one specific event or packet

## Phase Order

### Phase 1: Foundation

Deliverables:

- clean folder baseline for `client`, `server`, and `shared`
- protocol/version/constants baseline
- client boot and server boot still compile
- placeholder package structure matches architecture docs
- no gameplay features yet

Exit criteria:

- `./gradlew test` passes
- architecture docs and package structure agree

### Phase 2: Session and World Entry

Deliverables:

- login/auth handshake works
- client can request world entry
- server creates one player session
- player spawns into a minimal world snapshot

Exit criteria:

- client reaches an empty world scene from login

### Phase 3: Movement Slice

Deliverables:

- `WASD` input mapping
- client sends movement intent
- server applies authoritative movement
- client receives corrected position
- basic map/world rendering

Exit criteria:

- player can move around a simple map

### Phase 4: Vendor NPC Slice

Deliverables:

- one vendor NPC entity exists
- interaction range check on server
- `E` interacts when the player is near the vendor
- client shows a simple vendor interaction UI/state

Exit criteria:

- player can walk to vendor and interact with `E`

### Phase 5: Targeting Slice

Deliverables:

- hostile entities are targetable
- `TAB` cycles targets
- auto-target selects a nearby hostile when attacking without a target
- selected target is shown in the client UI

Exit criteria:

- player can select or auto-select the enemy cleanly

### Phase 6: First Enemy Combat Slice

Deliverables:

- one hostile enemy entity exists
- `E` attacks when a hostile target is selected and valid
- server validates range/cooldown/target
- health updates replicate to client
- enemy can be damaged and defeated

Exit criteria:

- player can move, target, and attack the first enemy

## Input Rules

- `WASD` = movement
- `TAB` = cycle hostile targets
- `E` = interact if a valid interactable is in range
- `E` = attack if a valid hostile target is selected
- if `E` is pressed with no hostile target, auto-target a valid nearby hostile and attack if possible

Priority rule:

1. nearby interactable vendor when interaction context is active
2. hostile combat target when attack context is active

This must be implemented explicitly so `E` stays predictable.

## First Gameplay Scope

Minimal world content:

- one player
- one enemy
- one vendor NPC
- one small map/arena

No extra systems until this slice is stable:

- no inventory complexity beyond what vendor interaction needs
- no quest system
- no loot system
- no advanced AI tree
- no extra screens beyond the minimum flow

## What Starts Now

Current starting work:

1. define phased roadmap
2. codify architecture as a repo skill
3. scaffold clean baseline packages
4. then implement Phase 1 foundation
