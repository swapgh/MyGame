package com.game.server.world.network;

import com.game.server.world.app.WorldServerMain.WorldApplication;
import com.game.server.world.network.handlers.AttackPacketHandler;
import com.game.server.world.network.handlers.ChatPacketHandler;
import com.game.server.world.network.handlers.EnterWorldPacketHandler;
import com.game.server.world.network.handlers.InventoryPacketHandler;
import com.game.server.world.network.handlers.MovementPacketHandler;
import com.game.shared.protocol.world.AttackPacket;
import com.game.shared.protocol.world.ChatMessagePacket;
import com.game.shared.protocol.world.EquipItemPacket;
import com.game.shared.protocol.world.EntityMovePacket;
import com.game.shared.protocol.world.EnterWorldPacket;
import com.game.shared.protocol.world.PickupLootPacket;

/**
 * Registers world packet handlers by packet type.
 *
 * @since 0.1.0
 */
public final class WorldPacketHandlers {
    private WorldPacketHandlers() {
    }

    /**
     * Registers the currently supported world packet handlers.
     *
     * @param router the world packet router
     * @param application the world application collaborators
     */
    public static void register(WorldPacketRouter router, WorldApplication application) {
        router.register(EnterWorldPacket.class, new EnterWorldPacketHandler(application));
        router.register(EntityMovePacket.class, new MovementPacketHandler(application));
        router.register(AttackPacket.class, new AttackPacketHandler(application));
        router.register(PickupLootPacket.class, new InventoryPacketHandler(application));
        router.register(EquipItemPacket.class, new InventoryPacketHandler(application));
        router.register(ChatMessagePacket.class, new ChatPacketHandler(application));
    }
}
