# World Server

Short Phase 3 summary.

## Current State

- world server bootstrap exists
- fixed-tick loop exists
- basic ECS core exists
- TCP world socket shell exists
- starter zone and map loading exist
- world model includes `World`, `Area`, and `AreaGrid`
- active connections are tracked with `WorldConnectionManager`
- entering the world returns an empty `WorldSnapshotPacket`

## Important Notes

- the world still runs empty on purpose
- there is a no-op `EmptyWorldSystem` registered so the loop has a real system to tick
- world packet handling is still minimal
- player spawning and movement are for later phases
