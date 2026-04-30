package com.game.client.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WorldFeedbackServiceTest {
    @Test
    void keepsLatestInteractionMessageForAShortDuration() {
        WorldFeedbackService service = new WorldFeedbackService();

        service.captureInteractionMessage("Vendor: Welcome.", 1L, 1_000L);

        assertEquals("Vendor: Welcome.", service.currentInteractionMessage(1_500L));
        assertNull(service.currentInteractionMessage(4_501L));
    }

    @Test
    void ignoresAlreadyConsumedInteractionVersions() {
        WorldFeedbackService service = new WorldFeedbackService();

        service.captureInteractionMessage("Vendor: Welcome.", 2L, 1_000L);
        service.captureInteractionMessage("Vendor: Welcome again.", 1L, 2_000L);

        assertEquals("Vendor: Welcome.", service.currentInteractionMessage(2_500L));
    }
}
