package com.game.server.world.components;

import com.game.shared.protocol.world.EquipmentSlot;

import java.util.EnumMap;
import java.util.Map;

/**
 * Tracks currently equipped item ids by equipment slot.
 *
 * @param equippedItemIds item ids keyed by equipment slot
 * @since 0.1.0
 */
public record EquipmentComponent(Map<EquipmentSlot, String> equippedItemIds) {
    public EquipmentComponent {
        EnumMap<EquipmentSlot, String> copy = new EnumMap<>(EquipmentSlot.class);
        copy.putAll(equippedItemIds);
        equippedItemIds = Map.copyOf(copy);
    }
}
