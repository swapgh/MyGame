package com.game.server.world.systems;

import com.game.server.world.components.TransformComponent;
import com.game.server.world.components.VelocityComponent;
import com.game.server.world.ecs.ComponentStore;
import com.game.server.world.ecs.EntityId;
import com.game.server.world.ecs.EntityManager;
import com.game.server.world.ecs.GameSystem;
import com.game.shared.math.Vec2;
import com.game.shared.time.GameClock;

/**
 * Applies velocity to entity transforms each world tick.
 *
 * @since 0.1.0
 */
public final class MovementSystem implements GameSystem {
    /**
     * Advances all movable entities by one tick.
     *
     * @param entities the shared entity manager for this tick
     * @param clock the current game clock snapshot
     */
    @Override
    public void tick(EntityManager entities, GameClock clock) {
        float deltaSeconds = (float) clock.tickRate().tickDurationSeconds();
        ComponentStore<TransformComponent> transforms = entities.storeOf(TransformComponent.class);
        ComponentStore<VelocityComponent> velocities = entities.storeOf(VelocityComponent.class);

        for (var entry : velocities.all()) {
            EntityId entityId = entry.getKey();
            VelocityComponent velocity = entry.getValue();
            TransformComponent transform = transforms.get(entityId).orElse(null);
            if (transform == null) {
                continue;
            }

            Vec2 nextPosition = transform.position().add(velocity.velocity().scale(deltaSeconds));
            transforms.put(entityId, new TransformComponent(nextPosition));
        }
    }
}
