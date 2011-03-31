package com.sourcesense.stone.extensions.webconsolesecurityprovider.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class SlingWebConsoleSecurityProviderTest {
    
    @Test
    @Ignore
    public void shouldNotAuthenticateUsersWithWrongCredentials() throws Exception {
        
        SlingWebConsoleSecurityProvider slingWebConsoleSecurityProvider = new SlingWebConsoleSecurityProvider();
        assertNull(slingWebConsoleSecurityProvider.authenticate("wrong user", "wrong password"));
    }
    
    @Test
    @Ignore
    public void shouldAuthenticateUserWithNameAdminAndPasswordAdmin() throws Exception {
        
        SlingWebConsoleSecurityProvider slingWebConsoleSecurityProvider = new SlingWebConsoleSecurityProvider();
        assertNotNull(slingWebConsoleSecurityProvider.authenticate("admin", "admin"));
    }
    
    @Test
    @Ignore
    public void shouldAuthorizeAnyone() throws Exception {
        
        SlingWebConsoleSecurityProvider slingWebConsoleSecurityProvider = new SlingWebConsoleSecurityProvider();
        assertTrue(slingWebConsoleSecurityProvider.authorize("anyObject", "anyRole"));
    }
}
