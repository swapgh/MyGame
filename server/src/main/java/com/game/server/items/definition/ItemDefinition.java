package com.game.server.items.definition;

import com.game.shared.protocol.world.EquipmentSlot;

/**
 * Data-driven item definition for phase 8 inventory and equipment.
 *
 * @param id unique item id
 * @param name user-facing item name
 * @param equipmentSlot equipment slot when equippable, or {@code null}
 * @param baseDamageBonus base damage bonus granted while equipped
 * @param attackRangeBonus attack range bonus granted while equipped
 * @since 0.1.0
 */
public record ItemDefinition(
        String id,
        String name,
        EquipmentSlot equipmentSlot,
        int baseDamageBonus,
        float attackRangeBonus
) {
    /**
     * Returns whether the item is equippable.
     *
     * @return {@code true} when the item supports an equipment slot
     */
    public boolean equippable() {
        return equipmentSlot != null;
    }
}
