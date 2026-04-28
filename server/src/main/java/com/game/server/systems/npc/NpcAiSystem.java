package com.game.server.systems.npc;

import com.game.server.components.combat.AttackIntentComponent;
import com.game.server.components.combat.CombatStatsComponent;
import com.game.server.components.combat.HealthComponent;
import com.game.server.components.npc.AiComponent;
import com.game.server.components.npc.AiState;
import com.game.server.components.npc.AiStateComponent;
import com.game.server.components.npc.NpcComponent;
import com.game.server.components.npc.RespawnComponent;
import com.game.server.components.world.PlayerComponent;
import com.game.server.components.world.TransformComponent;
import com.game.server.components.world.VelocityComponent;
import com.game.server.ecs.component.ComponentStore;
import com.game.server.ecs.entity.EntityId;
import com.game.server.ecs.entity.EntityManager;
import com.game.server.ecs.system.GameSystem;
import com.game.shared.math.Vec2;
import com.game.shared.time.GameClock;

/**
 * Very small NPC AI system that chases nearby players and attacks in range.
 *
 * @since 0.1.0
 */
public final class NpcAiSystem implements GameSystem {
    @Override
    public void tick(EntityManager entities, GameClock clock) {
        ComponentStore<NpcComponent> npcs = entities.storeOf(NpcComponent.class);
        ComponentStore<AiComponent> aiStore = entities.storeOf(AiComponent.class);
        ComponentStore<TransformComponent> transforms = entities.storeOf(TransformComponent.class);
        ComponentStore<HealthComponent> healths = entities.storeOf(HealthComponent.class);
        ComponentStore<RespawnComponent> respawns = entities.storeOf(RespawnComponent.class);
        ComponentStore<CombatStatsComponent> combatStats = entities.storeOf(CombatStatsComponent.class);
        ComponentStore<AiStateComponent> aiStates = entities.storeOf(AiStateComponent.class);
        ComponentStore<PlayerComponent> players = entities.storeOf(PlayerComponent.class);
        ComponentStore<VelocityComponent> velocities = entities.storeOf(VelocityComponent.class);
        ComponentStore<AttackIntentComponent> attacks = entities.storeOf(AttackIntentComponent.class);

        for (var entry : npcs.all()) {
            EntityId npcId = entry.getKey();
            AiComponent ai = aiStore.get(npcId).orElse(null);
            TransformComponent npcTransform = transforms.get(npcId).orElse(null);
            HealthComponent health = healths.get(npcId).orElse(null);
            RespawnComponent respawn = respawns.get(npcId).orElse(null);
            CombatStatsComponent stats = combatStats.get(npcId).orElse(null);
            if (ai == null || npcTransform == null || health == null || respawn == null || stats == null) {
                continue;
            }
            if (!health.alive() || respawn.waitingForRespawn()) {
                velocities.put(npcId, new VelocityComponent(Vec2.ZERO));
                aiStates.put(npcId, new AiStateComponent(AiState.IDLE));
                continue;
            }

            TargetPlayer target = findNearestPlayer(npcTransform.position(), ai.aggroRange(), transforms, healths, respawns, players);
            if (target == null) {
                steerTowardHome(npcId, npcTransform.position(), respawn.spawnPosition(), ai, velocities);
                if (respawn.spawnPosition().subtract(npcTransform.position()).lengthSquared() > ai.roamRadius() * ai.roamRadius()) {
                    aiStates.put(npcId, new AiStateComponent(AiState.RETURN));
                } else {
                    aiStates.put(npcId, new AiStateComponent(AiState.IDLE));
                }
                continue;
            }

            Vec2 toTarget = target.position().subtract(npcTransform.position());
            float attackRangeSquared = stats.attackRange() * stats.attackRange();
            if (toTarget.lengthSquared() <= attackRangeSquared) {
                velocities.put(npcId, new VelocityComponent(Vec2.ZERO));
                attacks.put(npcId, new AttackIntentComponent(clock.tick()));
                aiStates.put(npcId, new AiStateComponent(AiState.CHASE));
            } else {
                velocities.put(
                        npcId,
                        new VelocityComponent(toTarget.normalized().scale(ai.moveSpeed()))
                );
                aiStates.put(npcId, new AiStateComponent(AiState.CHASE));
            }
        }
    }

    private static void steerTowardHome(
            EntityId npcId,
            Vec2 currentPosition,
            Vec2 homePosition,
            AiComponent ai,
            ComponentStore<VelocityComponent> velocities
    ) {
        Vec2 homeOffset = homePosition.subtract(currentPosition);
        if (homeOffset.lengthSquared() > ai.roamRadius() * ai.roamRadius()) {
            velocities.put(npcId, new VelocityComponent(homeOffset.normalized().scale(ai.moveSpeed() * 0.6f)));
        } else {
            velocities.put(npcId, new VelocityComponent(Vec2.ZERO));
        }
    }

    private static TargetPlayer findNearestPlayer(
            Vec2 npcPosition,
            float aggroRange,
            ComponentStore<TransformComponent> transforms,
            ComponentStore<HealthComponent> healths,
            ComponentStore<RespawnComponent> respawns,
            ComponentStore<PlayerComponent> players
    ) {
        float maxDistanceSquared = aggroRange * aggroRange;
        TargetPlayer best = null;
        for (var entry : players.all()) {
            EntityId playerId = entry.getKey();
            TransformComponent playerTransform = transforms.get(playerId).orElse(null);
            HealthComponent health = healths.get(playerId).orElse(null);
            RespawnComponent respawn = respawns.get(playerId).orElse(null);
            if (playerTransform == null || health == null || respawn == null || !health.alive() || respawn.waitingForRespawn()) {
                continue;
            }

            Vec2 offset = playerTransform.position().subtract(npcPosition);
            float distanceSquared = offset.lengthSquared();
            if (distanceSquared > maxDistanceSquared) {
                continue;
            }

            if (best == null || distanceSquared < best.distanceSquared()) {
                best = new TargetPlayer(playerTransform.position(), distanceSquared);
            }
        }
        return best;
    }

    private record TargetPlayer(Vec2 position, float distanceSquared) {
    }
}
