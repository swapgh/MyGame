package com.game.server.world.systems;

import com.game.server.world.components.DroppedLootComponent;
import com.game.server.world.components.HealthComponent;
import com.game.server.world.components.LootComponent;
import com.game.server.world.components.LootDropStateComponent;
import com.game.server.world.components.NpcComponent;
import com.game.server.world.components.RespawnComponent;
import com.game.server.world.components.TransformComponent;
import com.game.server.world.ecs.ComponentStore;
import com.game.server.world.ecs.EntityId;
import com.game.server.world.ecs.EntityManager;
import com.game.server.world.definitions.ItemDefinition;
import com.game.server.world.ecs.GameSystem;
import com.game.shared.math.Vec2;
import com.game.shared.time.GameClock;

import java.util.Map;

/**
 * Spawns simple world loot entities when NPCs die.
 *
 * @since 0.1.0
 */
public final class LootDropSystem implements GameSystem {
    private final Map<String, ItemDefinition> itemDefinitions;

    /**
     * Creates a loot drop system backed by item definitions.
     *
     * @param itemDefinitions loaded item definitions
     */
    public LootDropSystem(Map<String, ItemDefinition> itemDefinitions) {
        this.itemDefinitions = Map.copyOf(itemDefinitions);
    }

    @Override
    public void tick(EntityManager entities, GameClock clock) {
        ComponentStore<NpcComponent> npcs = entities.storeOf(NpcComponent.class);
        ComponentStore<LootComponent> lootStore = entities.storeOf(LootComponent.class);
        ComponentStore<LootDropStateComponent> lootDropStates = entities.storeOf(LootDropStateComponent.class);
        ComponentStore<HealthComponent> healths = entities.storeOf(HealthComponent.class);
        ComponentStore<RespawnComponent> respawns = entities.storeOf(RespawnComponent.class);
        ComponentStore<TransformComponent> transforms = entities.storeOf(TransformComponent.class);

        for (var entry : npcs.all()) {
            EntityId npcId = entry.getKey();
            HealthComponent health = healths.get(npcId).orElse(null);
            RespawnComponent respawn = respawns.get(npcId).orElse(null);
            LootComponent loot = lootStore.get(npcId).orElse(null);
            LootDropStateComponent dropState = lootDropStates.get(npcId).orElse(null);
            TransformComponent transform = transforms.get(npcId).orElse(null);
            if (health == null || respawn == null || loot == null || dropState == null || transform == null) {
                continue;
            }

            if (health.alive()) {
                if (dropState.droppedForCurrentLife()) {
                    lootDropStates.put(npcId, new LootDropStateComponent(false));
                }
                continue;
            }

            if (dropState.droppedForCurrentLife() || loot.drops().isEmpty()) {
                continue;
            }

            EntityId lootEntityId = entities.create();
            String itemId = loot.drops().getFirst();
            entities.put(lootEntityId, new TransformComponent(transform.position().add(new Vec2(14.0f, 0.0f))));
            entities.put(lootEntityId, new DroppedLootComponent(itemId, resolveDisplayName(itemId), entry.getValue().definitionId()));
            lootDropStates.put(npcId, new LootDropStateComponent(true));
        }
    }

    private String resolveDisplayName(String itemId) {
        ItemDefinition definition = itemDefinitions.get(itemId);
        if (definition != null) {
            return definition.name();
        }
        return itemId.replace('_', ' ');
    }
}
