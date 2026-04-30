---
name: client-ui-screen-architecture
description: Use when adding or modifying client screens, auth flows, menu layouts, or UI rendering in this repo. Applies the client architecture boundaries, keeps screens thin, keeps reusable visuals in client/ui, and avoids SpriteBatch or ShapeRenderer ownership bugs.
---

# Client UI Screen Architecture

Use this skill when working on `client` screens, UI layout, HUD-like overlays, or shared UI rendering.

## Read First

- `/home/fer/Documentos/game/ARCHITECTURE_CLIENT.md`
- `/home/fer/Documentos/game/.agents/skills/game-rebuild-architecture/SKILL.md`

## What Goes Where

- `screens/`: screen lifecycle, screen transitions, high-level composition, input routing
- `ui/components/`: reusable widgets and panels
- `ui/layouts/`: layout math and bounds helpers
- `ui/core/`: screen composition helpers such as documents/sections
- `ui/render/`: reusable UI drawing primitives and render-state helpers
- `ui/theme/`: fonts, palette, theme constants
- `controllers/`: reaction to user actions and async flow
- `render/world/`: draw-only world scene and world HUD/overlay rendering

## Screen Rules

- keep screens thin: compose sections, route input, trigger transitions
- do not put network calls directly in screens; use controllers/services
- prefer a dedicated screen for a separate flow rather than one overloaded screen
- if a layout change is screen-specific, change the screen file first
- if a visual treatment is reusable, move it into `ui/render/` or `ui/components/`

## Render-State Rules

- do not manually scatter `SpriteBatch.begin()` and `SpriteBatch.end()` through many classes
- use `client/ui/render/UiRenderState` when switching between text and shapes
- if a renderer draws shapes and then resumes text, explicitly call `UiRenderState.beginText(gameClient)` before font draw calls
- render helpers may change render mode, so any raw `BitmapFont.draw(...)` after panels/shapes must re-enter text mode first

## Layout Workflow

1. Start in the screen class and locate layout constants.
2. Prefer named constants for panel sizes, gaps, padding, row spacing, and anchor points.
3. Compute positions from `UiRect` and `UiViewportLayout` instead of scattering hard-coded coordinates.
4. Only move logic into `ui/components/` when at least two screens benefit.

## Responsibility Checks

Before finishing, confirm:

- screen is orchestrating, not doing service/network work
- controller handles async action flow
- renderer is draw-only
- UI code does not mutate world rules
- shared visuals are under `client/ui`, not mixed into unrelated world code

## Current Repo Guidance

- `LoginScreen`, `CharacterSelectScreen`, and `CharacterCreateScreen` are the reference auth screens
- `UiRenderer` and `UiRenderState` are the shared client UI drawing entry points
- if `GameScreen` grows again, prefer extracting more world-overlay composition out of the screen rather than adding more mixed responsibilities there
