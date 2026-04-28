package com.game.client.network.world.codec;

import com.game.shared.protocol.world.EquipmentSlot;
import com.game.shared.protocol.world.EquippedItemPacket;
import com.game.shared.protocol.world.InventoryItemPacket;
import com.game.shared.protocol.world.InventoryUpdatePacket;

import java.util.ArrayList;
import java.util.List;

/**
 * Decodes inventory update packets on the client.
 *
 * @since 0.1.0
 */
public final class InventoryUpdateCodec {
    public InventoryUpdatePacket decode(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Missing inventory update.");
        }

        String[] parts = line.split("\\|", -1);
        if (parts.length < 4 || !"INVENTORY_UPDATE".equals(parts[0])) {
            throw new IllegalArgumentException("Unexpected world response: " + line);
        }

        List<InventoryItemPacket> inventoryItems = new ArrayList<>();
        if (!parts[2].isBlank()) {
            for (String encodedItem : parts[2].split(";", -1)) {
                String[] itemParts = encodedItem.split(",", -1);
                if (itemParts.length != 6) {
                    throw new IllegalArgumentException("Malformed inventory item: " + encodedItem);
                }
                inventoryItems.add(new InventoryItemPacket(
                        Integer.parseInt(itemParts[0]),
                        itemParts[1],
                        itemParts[2],
                        Integer.parseInt(itemParts[3]),
                        Boolean.parseBoolean(itemParts[4]),
                        itemParts[5].isBlank() ? null : EquipmentSlot.valueOf(itemParts[5])
                ));
            }
        }

        List<EquippedItemPacket> equippedItems = new ArrayList<>();
        if (!parts[3].isBlank()) {
            for (String encodedItem : parts[3].split(";", -1)) {
                String[] itemParts = encodedItem.split(",", -1);
                if (itemParts.length != 3) {
                    throw new IllegalArgumentException("Malformed equipped item: " + encodedItem);
                }
                equippedItems.add(new EquippedItemPacket(
                        EquipmentSlot.valueOf(itemParts[0]),
                        itemParts[1],
                        itemParts[2]
                ));
            }
        }

        return new InventoryUpdatePacket(Integer.parseInt(parts[1]), inventoryItems, equippedItems);
    }
}
