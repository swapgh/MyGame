package com.game.server.world.network;

import com.game.server.world.app.WorldServerMain.WorldApplication;
import com.game.server.world.commands.AttackHandler;
import com.game.server.world.commands.ChatHandler;
import com.game.server.world.commands.EnterHandler;
import com.game.server.world.commands.InteractHandler;
import com.game.server.world.commands.InventoryHandler;
import com.game.server.world.commands.MoveHandler;
import com.game.shared.protocol.world.AttackPacket;
import com.game.shared.protocol.world.ChatMessagePacket;
import com.game.shared.protocol.world.EquipItemPacket;
import com.game.shared.protocol.world.EntityMovePacket;
import com.game.shared.protocol.world.EnterWorldPacket;
import com.game.shared.protocol.world.InteractPacket;
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
        router.register(EnterWorldPacket.class, new EnterHandler(application));
        router.register(EntityMovePacket.class, new MoveHandler(application));
        router.register(AttackPacket.class, new AttackHandler(application));
        router.register(InteractPacket.class, new InteractHandler(application));
        router.register(PickupLootPacket.class, new InventoryHandler(application));
        router.register(EquipItemPacket.class, new InventoryHandler(application));
        router.register(ChatMessagePacket.class, new ChatHandler(application));
    }
}
