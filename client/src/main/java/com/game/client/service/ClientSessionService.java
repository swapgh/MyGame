package com.game.client.service;

import com.game.client.model.LoginSessionState;
import com.game.client.model.WorldSession;

/**
 * Stores client session state across login and world entry.
 *
 * @since 0.1.0
 */
public final class ClientSessionService {
    private volatile LoginSessionState loginSession;
    private volatile WorldSession worldSession;

    /**
     * Stores the authenticated login session and clears any active world session.
     *
     * @param loginSession login session state
     */
    public void storeLoginSession(LoginSessionState loginSession) {
        this.loginSession = loginSession;
        this.worldSession = null;
    }

    /**
     * Stores the active world session.
     *
     * @param worldSession world session state
     */
    public void storeWorldSession(WorldSession worldSession) {
        this.worldSession = worldSession;
    }

    /**
     * Returns the current login session.
     *
     * @return the login session, or {@code null}
     */
    public LoginSessionState loginSession() {
        return loginSession;
    }

    /**
     * Returns the current world session.
     *
     * @return the world session, or {@code null}
     */
    public WorldSession worldSession() {
        return worldSession;
    }

    /**
     * Clears all client-side session state.
     */
    public void clear() {
        loginSession = null;
        worldSession = null;
    }
}
