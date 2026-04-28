package com.game.server.world.systems;

import com.game.server.world.components.TransformComponent;
import com.game.server.world.components.VelocityComponent;
import com.game.server.world.ecs.ComponentStore;
import com.game.server.world.ecs.EntityId;
import com.game.server.world.ecs.EntityManager;
import com.game.server.world.ecs.GameSystem;
import com.game.shared.math.Vec2;
import com.game.shared.time.GameClock;

import java.util.ArrayList;
import java.util.List;

/**
 * Clamps entities to the simple Phase 5 test world bounds.
 *
 * @since 0.1.0
 */
public final class CollisionSystem implements GameSystem {
    private static final float MIN_X = 20.0f;
    private static final float MIN_Y = 20.0f;
    private static final float MAX_X = 1240.0f;
    private static final float MAX_Y = 680.0f;
    private static final float ENTITY_SIZE = 40.0f;
    private static final float ENTITY_RADIUS = ENTITY_SIZE * 0.5f;
    private static final float MIN_DISTANCE = ENTITY_SIZE;

    /**
     * Applies simple bounds collision to world transforms.
     *
     * @param entities the shared entity manager for this tick
     * @param clock the current game clock snapshot
     */
    @Override
    public void tick(EntityManager entities, GameClock clock) {
        ComponentStore<TransformComponent> transforms = entities.storeOf(TransformComponent.class);
        ComponentStore<VelocityComponent> velocities = entities.storeOf(VelocityComponent.class);
        for (var entry : transforms.all()) {
            TransformComponent transform = entry.getValue();
            Vec2 position = transform.position();
            float clampedX = Math.max(MIN_X + ENTITY_RADIUS, Math.min(MAX_X - ENTITY_RADIUS, position.x()));
            float clampedY = Math.max(MIN_Y + ENTITY_RADIUS, Math.min(MAX_Y - ENTITY_RADIUS, position.y()));
            if (clampedX != position.x() || clampedY != position.y()) {
                transforms.put(entry.getKey(), new TransformComponent(new Vec2(clampedX, clampedY)));
            }
        }

        List<EntityId> entityIds = new ArrayList<>();
        for (var entry : transforms.all()) {
            entityIds.add(entry.getKey());
        }

        for (int index = 0; index < entityIds.size(); index++) {
            EntityId leftId = entityIds.get(index);
            TransformComponent leftTransform = transforms.get(leftId).orElse(null);
            if (leftTransform == null) {
                continue;
            }

            for (int otherIndex = index + 1; otherIndex < entityIds.size(); otherIndex++) {
                EntityId rightId = entityIds.get(otherIndex);
                TransformComponent rightTransform = transforms.get(rightId).orElse(null);
                if (rightTransform == null) {
                    continue;
                }

                Vec2 delta = rightTransform.position().subtract(leftTransform.position());
                float distance = delta.length();
                if (distance >= MIN_DISTANCE) {
                    continue;
                }

                Vec2 normal = distance == 0.0f ? new Vec2(1.0f, 0.0f) : delta.scale(1.0f / distance);
                float overlap = MIN_DISTANCE - distance;
                Vec2 correction = normal.scale(overlap * 0.5f);

                Vec2 nextLeft = clampToWorld(leftTransform.position().subtract(correction));
                Vec2 nextRight = clampToWorld(rightTransform.position().add(correction));
                transforms.put(leftId, new TransformComponent(nextLeft));
                transforms.put(rightId, new TransformComponent(nextRight));

                dampVelocity(velocities, leftId);
                dampVelocity(velocities, rightId);

                leftTransform = transforms.get(leftId).orElse(leftTransform);
            }
        }
    }

    private static Vec2 clampToWorld(Vec2 position) {
        float clampedX = Math.max(MIN_X + ENTITY_RADIUS, Math.min(MAX_X - ENTITY_RADIUS, position.x()));
        float clampedY = Math.max(MIN_Y + ENTITY_RADIUS, Math.min(MAX_Y - ENTITY_RADIUS, position.y()));
        return new Vec2(clampedX, clampedY);
    }

    private static void dampVelocity(ComponentStore<VelocityComponent> velocities, EntityId entityId) {
        VelocityComponent velocity = velocities.get(entityId).orElse(null);
        if (velocity != null) {
            velocities.put(entityId, new VelocityComponent(velocity.velocity().scale(0.75f)));
        }
    }
}
