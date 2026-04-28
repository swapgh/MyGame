package com.game.server.systems.combat;

import com.game.server.components.combat.AttackIntentComponent;
import com.game.server.components.combat.CombatStateComponent;
import com.game.server.components.combat.CombatStatsComponent;
import com.game.server.components.combat.HealthComponent;
import com.game.server.components.npc.RespawnComponent;
import com.game.server.components.world.TransformComponent;
import com.game.server.components.world.VelocityComponent;
import com.game.server.ecs.component.ComponentStore;
import com.game.server.ecs.entity.EntityId;
import com.game.server.ecs.entity.EntityManager;
import com.game.server.ecs.system.GameSystem;
import com.game.shared.math.Vec2;
import com.game.shared.time.GameClock;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Resolves queued attack intents into authoritative damage and deaths.
 *
 * @since 0.1.0
 */
public final class CombatSystem implements GameSystem {
    @Override
    public void tick(EntityManager entities, GameClock clock) {
        ComponentStore<AttackIntentComponent> attackIntents = entities.storeOf(AttackIntentComponent.class);
        ComponentStore<CombatStatsComponent> combatStats = entities.storeOf(CombatStatsComponent.class);
        ComponentStore<CombatStateComponent> combatStates = entities.storeOf(CombatStateComponent.class);
        ComponentStore<TransformComponent> transforms = entities.storeOf(TransformComponent.class);
        ComponentStore<HealthComponent> healths = entities.storeOf(HealthComponent.class);
        ComponentStore<RespawnComponent> respawns = entities.storeOf(RespawnComponent.class);
        ComponentStore<VelocityComponent> velocities = entities.storeOf(VelocityComponent.class);

        for (var entry : new ArrayList<>(attackIntents.all())) {
            EntityId attackerId = entry.getKey();
            attackIntents.remove(attackerId);

            CombatStatsComponent stats = combatStats.get(attackerId).orElse(null);
            CombatStateComponent state = combatStates.get(attackerId).orElse(null);
            TransformComponent attackerTransform = transforms.get(attackerId).orElse(null);
            HealthComponent attackerHealth = healths.get(attackerId).orElse(null);
            RespawnComponent attackerRespawn = respawns.get(attackerId).orElse(null);
            if (stats == null
                    || state == null
                    || attackerTransform == null
                    || attackerHealth == null
                    || attackerRespawn == null
                    || !attackerHealth.alive()
                    || attackerRespawn.waitingForRespawn()) {
                continue;
            }

            if ((clock.tick() - state.lastAttackTick()) < stats.attackCooldownTicks()) {
                continue;
            }

            TargetResult targetResult = findNearestLivingTarget(
                    attackerId,
                    attackerTransform.position(),
                    stats.attackRange(),
                    transforms,
                    healths,
                    respawns
            );
            if (targetResult == null) {
                continue;
            }

            HealthComponent targetHealth = targetResult.health();
            int damage = calculateDamage(stats.baseDamage(), stats.attackRange(), targetResult.distance());
            int remainingHealth = Math.max(0, targetHealth.currentHealth() - damage);
            healths.put(targetResult.targetId(), new HealthComponent(remainingHealth, targetHealth.maxHealth()));
            combatStates.put(attackerId, new CombatStateComponent(clock.tick()));

            if (remainingHealth == 0) {
                RespawnComponent targetRespawn = respawns.get(targetResult.targetId()).orElse(null);
                if (targetRespawn != null) {
                    respawns.put(
                            targetResult.targetId(),
                            new RespawnComponent(
                                    targetRespawn.spawnPosition(),
                                    targetRespawn.respawnDelayTicks(),
                                    clock.tick() + targetRespawn.respawnDelayTicks()
                            )
                    );
                }
                velocities.put(targetResult.targetId(), new VelocityComponent(Vec2.ZERO));
            }
        }
    }

    static int calculateDamage(int baseDamage, float attackRange, float distance) {
        float rangeAdvantage = Math.max(0.0f, attackRange - distance);
        int distanceBonus = Math.round(rangeAdvantage / 45.0f);
        return Math.max(1, baseDamage + distanceBonus);
    }

    private static TargetResult findNearestLivingTarget(
            EntityId attackerId,
            Vec2 attackerPosition,
            float attackRange,
            ComponentStore<TransformComponent> transforms,
            ComponentStore<HealthComponent> healths,
            ComponentStore<RespawnComponent> respawns
    ) {
        float maxDistanceSquared = attackRange * attackRange;
        return transforms.all().stream()
                .filter(entry -> !entry.getKey().equals(attackerId))
                .map(entry -> {
                    EntityId targetId = entry.getKey();
                    HealthComponent health = healths.get(targetId).orElse(null);
                    RespawnComponent respawn = respawns.get(targetId).orElse(null);
                    if (health == null || respawn == null || !health.alive() || respawn.waitingForRespawn()) {
                        return null;
                    }
                    float distanceSquared = attackerPosition.subtract(entry.getValue().position()).lengthSquared();
                    if (distanceSquared > maxDistanceSquared) {
                        return null;
                    }
                    return new TargetResult(targetId, health, (float) Math.sqrt(distanceSquared));
                })
                .filter(target -> target != null)
                .min(Comparator.comparing(TargetResult::distance))
                .orElse(null);
    }

    private record TargetResult(EntityId targetId, HealthComponent health, float distance) {
    }
}
