package com.game.server.world.ecs;

import java.util.*;

/**
 * Central registry for world server entities and their components.
 *
 * <p>The manager is the single source of truth for which entities are alive and
 * what components they carry. All component access goes through typed
 * {@link ComponentStore} instances that this class creates on demand.</p>
 *
 * <p>Not thread-safe: systems must access it on the game loop thread only.</p>
 *
 * @since 0.1.0
 */
public class EntityManager {
    private final Set<EntityId> alive = new HashSet<>();
    private final Map<Class<?>, ComponentStore<?>> stores = new HashMap<>();

    /**
     * Creates a new entity and returns its unique identifier.
     * @return the freshly created entity's identifier
     */
    public EntityId create(){
        EntityId id = EntityId.next();
        alive.add(id);
        return id;
    }

    /**
     * Destroys the entity and removes all of its components from every store.
     * @param id the entity to destroy
     */

    public void destroy(EntityId id){
        alive.remove(id);
        for (ComponentStore<?> store : stores.values()) {
            store.remove(id);
        }
    }

    /** Returns whether the entity is currently alive. */

    public boolean isAlive(EntityId id){
        return alive.contains(id);
    }

    /** Returns an unmodifiable snapshot of all living entity identifiers. */
    public Set<EntityId> all() {
        return Collections.unmodifiableSet(alive);
    }

    /** Returns the total number of living entities. */
    public int count() {
        return alive.size();
    }

    /**
     * Attaches a component to an entity, replacing any existing component of the same type.
     * @param <C>       the component type
     * @param id        the target entity
     * @param component the component instance to attach
     */
    public <C> void put(EntityId id, C component){
        if (!isAlive(id)) {
            throw new IllegalArgumentException("Cannot attach component to non-existent entity: " + id);
        }
        @SuppressWarnings("unchecked")
        ComponentStore<C> store = (ComponentStore<C>) stores.computeIfAbsent(
                component.getClass(), k -> new ComponentStore<>()
        );
        store.put(id, component);
    }
    /**
     * Returns the component of the given type attached to the entity, if present.
     * @param <C>            the component type
     * @param id             the entity to look up
     * @param componentClass the component class token
     * @return the component, or empty if not attached
     */
    public <C> Optional<C> get(EntityId id, Class<C> componentClass) {
        @SuppressWarnings("unchecked")
        ComponentStore<C> store = (ComponentStore<C>) stores.get(componentClass);
        if (store == null) {
            return Optional.empty();
        }
        return store.get(id);
    }

    /** Returns whether the entity has a component of the given type. */
    public <C> boolean has(EntityId id, Class<C> componentClass) {
        ComponentStore<?> store = stores.get(componentClass);
        return store != null && store.has(id);
    }
    /**
     * Returns the {@link ComponentStore} for the given component type, creating it if absent.
     * <p>Systems that iterate all entities with a given component should hold a reference
     * to the store for the duration of the tick.</p>
     * @param <C>            the component type
     * @param componentClass the component class token
     * @return the store for the given type
     */
    public <C> ComponentStore<C> storeOf(Class<C> componentClass) {
        @SuppressWarnings("unchecked")
        ComponentStore<C> store = (ComponentStore<C>) stores.computeIfAbsent(
                componentClass, k -> new ComponentStore<>()
        );
        return store;
    }
}
