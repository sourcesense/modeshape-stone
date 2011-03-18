package com.sourcesense.stone.extensions;

import javax.naming.NamingException;
import javax.naming.Reference;
import net.jcip.annotations.ThreadSafe;
import org.modeshape.common.annotation.Category;
import org.modeshape.common.annotation.Description;
import org.modeshape.common.annotation.Label;
import org.modeshape.graph.connector.RepositoryConnection;
import org.modeshape.graph.connector.RepositoryContext;
import org.modeshape.graph.connector.RepositorySource;
import org.modeshape.graph.connector.RepositorySourceCapabilities;
import org.modeshape.graph.connector.RepositorySourceException;

@ThreadSafe
public class JackrabbitRepositorySource implements RepositorySource {

    @Description( i18n = JackrabbitConnectorI18n.class, value = "namePropertyDescription" )
    @Label( i18n = JackrabbitConnectorI18n.class, value = "namePropertyLabel" )
    @Category( i18n = JackrabbitConnectorI18n.class, value = "namePropertyCategory" )
    private volatile String name;

    @Description( i18n = JackrabbitConnectorI18n.class, value = "retryLimitPropertyDescription" )
    @Label( i18n = JackrabbitConnectorI18n.class, value = "retryLimitPropertyLabel" )
    @Category( i18n = JackrabbitConnectorI18n.class, value = "retryLimitPropertyCategory" )
    private volatile int retryLimit;

    private RepositoryContext repositoryContext;

    private volatile RepositorySourceCapabilities capabilities = new RepositorySourceCapabilities(true, true, false, true, true);

    @Override
    public Reference getReference() throws NamingException {
        throw new RuntimeException("Method getReference not yet implemented");
    }

    @Override
    public void initialize( RepositoryContext context ) throws RepositorySourceException {
        this.repositoryContext = context;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RepositoryConnection getConnection() throws RepositorySourceException {
        return new JackrabbitRepositoryConnection();
    }

    @Override
    public int getRetryLimit() {
        return retryLimit;
    }

    @Override
    public void setRetryLimit( int retryLimit ) {
        this.retryLimit = retryLimit < 0 ? 0 : retryLimit;
    }

    @Override
    public RepositorySourceCapabilities getCapabilities() {
        return capabilities;
    }

    @Override
    public void close() {
        repositoryContext = null;
    }
}
