package com.game.client.world.ecs;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Stores one client component type per entity.
 *
 * @param <C> the component type
 * @since 0.1.0
 */
public final class ClientComponentStore<C> {
    private final Map<ClientEntityId, C> data = new HashMap<>();

    public void put(ClientEntityId entityId, C component) {
        if (component == null) {
            throw new IllegalArgumentException("component cannot be null");
        }
        data.put(entityId, component);
    }

    public Optional<C> get(ClientEntityId entityId) {
        return Optional.ofNullable(data.get(entityId));
    }

    public boolean has(ClientEntityId entityId) {
        return data.containsKey(entityId);
    }

    public void remove(ClientEntityId entityId) {
        data.remove(entityId);
    }

    public Collection<Map.Entry<ClientEntityId, C>> all() {
        return Collections.unmodifiableCollection(data.entrySet());
    }
}
