package com.sourcesense.stone.jcr.modeshape.server.security;

import org.modeshape.jcr.api.SecurityContext;


public class CustomSecurityContext implements SecurityContext {

    @Override
    public String getUserName() {
        return "admin";
    }

    @Override
    public boolean hasRole( String roleName ) {
        return true;
    }

    @Override
    public void logout() {
    }

}
