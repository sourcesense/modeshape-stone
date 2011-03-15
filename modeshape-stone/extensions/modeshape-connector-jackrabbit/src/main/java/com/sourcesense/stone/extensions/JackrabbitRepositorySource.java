package com.sourcesense.stone.extensions;

import javax.naming.NamingException;
import javax.naming.Reference;
import org.modeshape.graph.connector.RepositoryConnection;
import org.modeshape.graph.connector.RepositoryContext;
import org.modeshape.graph.connector.RepositorySource;
import org.modeshape.graph.connector.RepositorySourceCapabilities;
import org.modeshape.graph.connector.RepositorySourceException;

public class JackrabbitRepositorySource implements RepositorySource {

    @Override
    public Reference getReference() throws NamingException {
        return null;
    }

    @Override
    public void initialize( RepositoryContext context ) throws RepositorySourceException {
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public RepositoryConnection getConnection() throws RepositorySourceException {
        return null;
    }

    @Override
    public int getRetryLimit() {
        return 0;
    }

    @Override
    public void setRetryLimit( int limit ) {
    }

    @Override
    public RepositorySourceCapabilities getCapabilities() {
        return null;
    }

    @Override
    public void close() {
    }
}
