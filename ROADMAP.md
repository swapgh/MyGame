# Roadmap

## Current Status

Completed: Phases 0-5

Current stage: Phase 6

This file is the working plan for the project so we can track progress without cluttering the GitHub front page.

## Phases

1. [x] Phase 0: skeleton
   Gradle multi-module setup, empty source folders, `.gitignore`, and `README`. No gameplay implementation yet.
2. [x] Phase 1: shared protocol
   Packets, opcodes, math (`Vec2`/`Vec3`), `GameClock`, and `Result`. The contract between client and server, with no game logic yet.
3. [x] Phase 2: auth server
   Socket server, login/register, password hashing, session tokens, PostgreSQL integration, and account/character DAOs. A test client can connect and log in.
4. [x] Phase 3: world server skeleton
   Game loop, ECS core (`EntityManager`, `ComponentStore`, `GameSystem`), and basic map loading. The server runs and ticks, but the world is empty.
5. [x] Phase 4: client skeleton
   LibGDX window, `ScreenManager`, login flow, character select, and world entry. The world is still minimal.
6. [x] Phase 5: player in the world
   Movement system, collision, snapshot system, client prediction, and interpolation. A placeholder square moves smoothly and other entities appear.
7. [ ] Phase 6: combat
   Attack commands, damage formula, health components, and death/respawn.
8. [ ] Phase 7: NPCs
   NPC definitions, AI system, spawn tables, and loot drops.
9. [ ] Phase 8: inventory and items
   Item definitions, inventory component, loot pickup, and equipment.
10. [ ] Phase 9: quests
    Quest definitions, progress tracking, and rewards.
11. [ ] Phase 10: polish
    Sprites replacing squares, animations, audio, UI windows, and minimap.
