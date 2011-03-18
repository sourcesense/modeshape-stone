package com.sourcesense.stone.extensions;

import java.util.concurrent.TimeUnit;
import javax.transaction.xa.XAResource;
import org.modeshape.graph.ExecutionContext;
import org.modeshape.graph.cache.CachePolicy;
import org.modeshape.graph.connector.RepositoryConnection;
import org.modeshape.graph.connector.RepositorySourceException;
import org.modeshape.graph.request.Request;

public class JackrabbitRepositoryConnection implements RepositoryConnection{

    private final JackrabbitRepositorySource repositorySource;

    public JackrabbitRepositoryConnection( JackrabbitRepositorySource repositorySource ) {
        this.repositorySource = repositorySource;
    }

    @Override
    public String getSourceName() {
        return repositorySource.getName();
    }

    @Override
    public XAResource getXAResource() {
        return null;
    }

    @Override
    public boolean ping( long time,
                         TimeUnit unit ) throws InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public CachePolicy getDefaultCachePolicy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void execute( ExecutionContext context,
                         Request request ) throws RepositorySourceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

}
