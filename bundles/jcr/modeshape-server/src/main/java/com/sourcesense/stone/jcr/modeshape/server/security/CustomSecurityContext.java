package com.sourcesense.stone.jcr.modeshape.server.security;

import org.modeshape.jcr.api.SecurityContext;

/**
 * TODO fill me
 *
 * @version $Id$
 */
public class CustomSecurityContext implements SecurityContext {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserName() {
        return "admin";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasRole( String roleName ) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logout() {
    }

}
