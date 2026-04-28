package com.game.client.network;

import com.game.shared.ecs.SharedEntityId;
import com.game.shared.protocol.world.AttackPacket;
import com.game.shared.protocol.world.WorldSnapshotPacket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldPacketCodecTest {
    @Test
    void decodesCombatFieldsFromWorldSnapshot() {
        WorldPacketCodec codec = new WorldPacketCodec();

        WorldSnapshotPacket snapshot = codec.decodeSnapshot(
                "WORLD_SNAPSHOT|42|7|7,10.0,20.0,0.0,0.0,82,100,true,0;8,40.0,50.0,0.0,0.0,0,100,false,18"
        );

        assertEquals(42L, snapshot.serverTick());
        assertEquals(2, snapshot.entities().size());
        assertEquals(82, snapshot.entities().get(0).currentHealth());
        assertTrue(snapshot.entities().get(0).alive());
        assertFalse(snapshot.entities().get(1).alive());
        assertEquals(18L, snapshot.entities().get(1).respawnTicksRemaining());
    }

    @Test
    void encodesAttackPacket() {
        WorldPacketCodec codec = new WorldPacketCodec();

        String encoded = codec.encode(new AttackPacket(new SharedEntityId(17L)));

        assertEquals("ATTACK|17", encoded);
    }
}
