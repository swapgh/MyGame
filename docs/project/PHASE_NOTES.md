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

Not started.

### Summary

- Next target after auth
- Will add world bootstrap, tick loop, ECS base, and empty map/world structure

## Phase 4: Client Skeleton

### Current Status

Not started.

### Summary

- Will add LibGDX client bootstrap and auth-to-world flow

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
