package com.sourcesense.stone.extensions;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.naming.Reference;
import net.jcip.annotations.ThreadSafe;
import org.apache.jackrabbit.spi2dav.Spi2davRepositoryServiceFactory;
import org.modeshape.common.annotation.Category;
import org.modeshape.common.annotation.Description;
import org.modeshape.common.annotation.Label;
import org.modeshape.common.i18n.I18n;
import org.modeshape.connector.jcr.JcrRepositorySource;
import org.modeshape.graph.connector.RepositoryConnection;
import org.modeshape.graph.connector.RepositoryContext;
import org.modeshape.graph.connector.RepositorySourceCapabilities;
import org.modeshape.graph.connector.RepositorySourceException;

@ThreadSafe
public class JackrabbitRepositorySource extends JcrRepositorySource {

    @Description( i18n = JackrabbitConnectorI18n.class, value = "namePropertyDescription" )
    @Label( i18n = JackrabbitConnectorI18n.class, value = "namePropertyLabel" )
    @Category( i18n = JackrabbitConnectorI18n.class, value = "namePropertyCategory" )
    private volatile String name;

    @Description( i18n = JackrabbitConnectorI18n.class, value = "retryLimitPropertyDescription" )
    @Label( i18n = JackrabbitConnectorI18n.class, value = "retryLimitPropertyLabel" )
    @Category( i18n = JackrabbitConnectorI18n.class, value = "retryLimitPropertyCategory" )
    private volatile int retryLimit;

    @Description( i18n = JackrabbitConnectorI18n.class, value = "urlPropertyDescription" )
    @Label( i18n = JackrabbitConnectorI18n.class, value = "urlPropertyLabel" )
    @Category( i18n = JackrabbitConnectorI18n.class, value = "urlPropertyCategory" )
    private volatile String url;
    
    private RepositoryContext repositoryContext;
    private volatile RepositorySourceCapabilities capabilities = new RepositorySourceCapabilities(true, true, false, true, true);
    private Spi2davRepositoryServiceFactory spi2davRepositoryServiceFactory;

    private CredentialsFactory credentialsFactory;
    private RepositoryFactory repositoryFactory;
    private RepositoryConnectionFactory repositoryConnectionFactory;

    @Override
    public synchronized Reference getReference() {
        throw new RuntimeException("Method getReference not yet implemented");
    }
    
    @Override
    public void initialize( RepositoryContext context ) throws RepositorySourceException {
        this.repositoryContext = context;
        this.spi2davRepositoryServiceFactory = new Spi2davRepositoryServiceFactory();
        this.credentialsFactory = new CredentialsFactory();
        this.repositoryFactory = new RepositoryFactory();
        this.repositoryConnectionFactory = new RepositoryConnectionFactory();
    }
    
    @Override
    public RepositoryConnection getConnection() throws RepositorySourceException {
        if (null == url || 0 == url.trim().length()) {
            I18n msg = JackrabbitConnectorI18n.propertyIsRequired;
            throw new RepositorySourceException(url, msg.text("url"));
        }
        
        Repository repository = null;
        try {
            repository = repositoryFactory.createRepository(url);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        
        Credentials credentials = credentialsFactory.createCredentials(getUsername(), getPassword());
        return repositoryConnectionFactory.createRepositoryConnection(this, repository, credentials);
    }

    protected Spi2davRepositoryServiceFactory getSpi2davRepositoryServiceFactory() {
        return this.spi2davRepositoryServiceFactory;
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
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl( String url ) {
        this.url = url;
    }
}
