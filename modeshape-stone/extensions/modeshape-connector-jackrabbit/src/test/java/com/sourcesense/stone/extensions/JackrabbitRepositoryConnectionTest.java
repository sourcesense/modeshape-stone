package com.sourcesense.stone.extensions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;

public class JackrabbitRepositoryConnectionTest {

    private JackrabbitRepositorySource repositorySource;
    private JackrabbitRepositoryConnection repositoryConnection;

    @Before
    public void setup() {
        repositorySource = new JackrabbitRepositorySource();
        repositoryConnection = new JackrabbitRepositoryConnection(repositorySource);
    }
    
    @Test
    public void shouldReturnRepositorySourceName() throws Exception {
        repositorySource.setName("aName");
        
        assertEquals("aName", repositoryConnection.getSourceName());
    }
    
    @Test
    public void shouldReturnANullXAResourceBecauseNotAvailable() throws Exception {
        assertNull(repositoryConnection.getXAResource());
    }
}
