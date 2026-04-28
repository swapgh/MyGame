package com.game.server.world.network.codec;

import com.game.shared.protocol.world.EquipmentSlot;
import com.game.shared.protocol.world.EquippedItemPacket;
import com.game.shared.protocol.world.InventoryItemPacket;
import com.game.shared.protocol.world.InventoryUpdatePacket;

import java.util.ArrayList;
import java.util.List;

/**
 * Encodes and decodes inventory update packets.
 *
 * @since 0.1.0
 */
public final class InventoryUpdateCodec {
    public String encode(InventoryUpdatePacket inventoryUpdate) {
        String inventoryItems = inventoryUpdate.inventoryItems().stream()
                .map(item -> String.join(
                        ",",
                        Integer.toString(item.slotIndex()),
                        CodecSupport.sanitize(item.itemId()),
                        CodecSupport.sanitize(item.displayName()),
                        Integer.toString(item.quantity()),
                        Boolean.toString(item.equippable()),
                        item.equipmentSlot() == null ? "" : item.equipmentSlot().name()
                ))
                .reduce((left, right) -> left + ";" + right)
                .orElse("");
        String equippedItems = inventoryUpdate.equippedItems().stream()
                .map(item -> String.join(
                        ",",
                        item.equipmentSlot().name(),
                        CodecSupport.sanitize(item.itemId()),
                        CodecSupport.sanitize(item.displayName())
                ))
                .reduce((left, right) -> left + ";" + right)
                .orElse("");
        return String.join(
                "|",
                "INVENTORY_UPDATE",
                Integer.toString(inventoryUpdate.capacity()),
                inventoryItems,
                equippedItems
        );
    }

    public InventoryUpdatePacket decode(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4 || !"INVENTORY_UPDATE".equals(parts[0])) {
            throw new IllegalArgumentException("Unexpected inventory update: " + line);
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
