package com.game.server.world.ecs;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Stores one component type per entity for a single component class.
 * <p>Each component class gets its own {@code ComponentStore} instance, held
 * inside {@link EntityManager}. Components are plain Java objects.</p>
 * @param <C> the component type stored in this store
 * @since 0.1.0
 */
public class ComponentStore<C> {
    private final Map<EntityId, C> data = new HashMap<>();

    /**
     * Attaches (or replaces) a component for the given entity.
     * @param id        the entity that owns the component
     * @param component the component instance to attach
     */
    public void put(EntityId id, C component){
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
        data.put(id, component);
    }
    /**
     * Returns the component attached to the entity, if present.
     * @param id the entity to look up
     * @return the component, or empty if none is attached
     */
    public Optional<C> get(EntityId id){
        return Optional.ofNullable(data.get(id));
    }
    /**
     * Returns whether the entity has a component in this store.
     * @param id the entity to check
     * @return {@code true} if the entity has a component
     */
    public boolean has(EntityId id){
        return data.containsKey(id);
    }
    /**
     * Removes the component attached to the entity, if any.
     * @param id the entity whose component should be removed
     */
    public void remove(EntityId id){
        data.remove(id);
    }
    /**
     * Returns an unmodifiable view of all entity-to-component mappings.
     * @return all entries in this store
     */
    public Collection<Map.Entry<EntityId, C>> all(){
        return Collections.unmodifiableCollection(data.entrySet());
    }

    /** Return the number of components currently held. */
    public int size() {
        return data.size();
    }
}
