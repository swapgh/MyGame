# Phase Notes

Short project resume by phase.

Use `ROADMAP.md` as the main tracker.
Use this file only for short summaries and important notes.

## Phase 0: Project Skeleton

### Status

Done.

### Summary

- Gradle multi-module layout created
- Base folders and config/data/tools structure created
- Repository prepared for incremental phase-by-phase work

### Notes

- The project targets Java 21, so builds need a Java 21 JDK available locally.

## Phase 1: Shared Protocol

### Status

Done.

### Summary

- Shared packet base and opcodes added
- Shared math/time/util types added
- First protocol structure for auth/world/error added

### Notes

- `TickRate.tickDurationMillis()` uses integer division, so it is approximate by design.

## Phase 2: Auth Server

### Status

Done for the current milestone, with development-mode caveats still noted below.

### Summary

- Auth server bootstrap and config loading added
- TCP auth socket server shell added
- Login, registration, session, and character services added
- In-memory DAO layer added
- Test auth protocol and shell client added
- Successful login can return a session token and character list in the current temporary flow

### Notes

- `PasswordHasher` currently uses SHA-256 as a placeholder only.
- Replace with BCrypt or Argon2 before treating auth as real.
- `ServerConfigLoader` is still a minimal handwritten parser for the current simple YAML shape.
- Current protocol is temporary and line-based
- PostgreSQL is configured, but runtime storage is still in-memory

## Phase 3: World Server Skeleton

### Current Status

Done for the current skeleton milestone.

### Summary

- World server bootstrap added
- Fixed-tick world loop and ECS core added
- TCP world socket shell added
- Basic zone and map loading added
- Empty world snapshot flow added

### Notes

- The world intentionally runs empty for now.
- Player spawning, movement, and replication belong to later phases.

## Phase 4: Client Skeleton

### Current Status

Done for the current skeleton milestone.

### Summary

- Desktop LibGDX launcher and client Gradle task added
- Keyboard-driven login, register, character select, loading, and error screens added
- Auth-to-world flow reaches a minimal in-world screen with a placeholder square

### Notes

- Verification is still blocked on this machine until a Java 25 JDK is installed.
- The client still uses temporary line-based protocol flow from earlier phases.

## Phase 5: Player In The World

### Current Status

Not started.

### Summary

- Placeholder player movement, replication, prediction, and interpolation

## Phase 6: Combat

### Current Status

Not started.

### Summary

- Attack, damage, health, death, and respawn flow

## Phase 7: NPCs

### Current Status

Not started.

### Summary

- NPC definitions, AI, spawn tables, and loot drops

## Phase 8: Inventory And Items

### Current Status

Not started.

### Summary

- Item definitions, inventory, pickups, and equipment

## Phase 9: Quests

### Current Status

Not started.

### Summary

- Quest definitions, progress tracking, and rewards

## Phase 10: Polish

### Current Status

Not started.

### Summary

- Replace placeholders with sprites, animation, audio, UI polish, and minimap
