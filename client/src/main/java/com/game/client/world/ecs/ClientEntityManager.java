package com.game.client.world.ecs;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Small client-side entity manager for visual ECS state.
 *
 * @since 0.1.0
 */
public final class ClientEntityManager {
    private final Set<ClientEntityId> alive = new HashSet<>();
    private final Map<Class<?>, ClientComponentStore<?>> stores = new HashMap<>();

    public ClientEntityId create() {
        ClientEntityId id = ClientEntityId.next();
        alive.add(id);
        return id;
    }

    public void destroy(ClientEntityId entityId) {
        alive.remove(entityId);
        for (ClientComponentStore<?> store : stores.values()) {
            store.remove(entityId);
        }
    }

    public boolean isAlive(ClientEntityId entityId) {
        return alive.contains(entityId);
    }

    public Set<ClientEntityId> all() {
        return Collections.unmodifiableSet(alive);
    }

    public <C> void put(ClientEntityId entityId, C component) {
        if (!isAlive(entityId)) {
            throw new IllegalArgumentException("entity is not alive: " + entityId);
        }
        @SuppressWarnings("unchecked")
        ClientComponentStore<C> store = (ClientComponentStore<C>) stores.computeIfAbsent(
                component.getClass(), ignored -> new ClientComponentStore<>()
        );
        store.put(entityId, component);
    }

    public <C> Optional<C> get(ClientEntityId entityId, Class<C> componentType) {
        @SuppressWarnings("unchecked")
        ClientComponentStore<C> store = (ClientComponentStore<C>) stores.get(componentType);
        return store == null ? Optional.empty() : store.get(entityId);
    }

    public <C> ClientComponentStore<C> storeOf(Class<C> componentType) {
        @SuppressWarnings("unchecked")
        ClientComponentStore<C> store = (ClientComponentStore<C>) stores.computeIfAbsent(
                componentType, ignored -> new ClientComponentStore<>()
        );
        return store;
    }
}
