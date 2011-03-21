package com.sourcesense.stone.extensions;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.naming.Reference;
import net.jcip.annotations.ThreadSafe;
import org.modeshape.common.annotation.Category;
import org.modeshape.common.annotation.Description;
import org.modeshape.common.annotation.Label;
import org.modeshape.common.i18n.I18n;
import org.modeshape.connector.jcr.JcrConnectorI18n;
import org.modeshape.connector.jcr.JcrRepositorySource;
import org.modeshape.graph.connector.RepositoryConnection;
import org.modeshape.graph.connector.RepositoryContext;
import org.modeshape.graph.connector.RepositorySourceCapabilities;
import org.modeshape.graph.connector.RepositorySourceException;

@ThreadSafe
public class JackrabbitRepositorySource extends JcrRepositorySource {

    @Description( i18n = JcrConnectorI18n.class, value = "urlPropertyDescription" )
    @Label( i18n = JcrConnectorI18n.class, value = "urlPropertyLabel" )
    @Category( i18n = JcrConnectorI18n.class, value = "urlPropertyCategory" )
    private volatile String url;
    
    private volatile RepositorySourceCapabilities capabilities = new RepositorySourceCapabilities(true, true, false, true, true);

    private CredentialsFactory credentialsFactory;
    private RepositoryFactory repositoryFactory;
    private RepositoryConnectionFactory repositoryConnectionFactory;

    @Override
    public synchronized Reference getReference() {
        throw new RuntimeException("Method getReference not yet implemented");
    }
    
    @Override
    public void initialize( RepositoryContext context ) throws RepositorySourceException {
        super.initialize(context);
        this.credentialsFactory = new CredentialsFactory();
        this.repositoryFactory = new RepositoryFactory();
        this.repositoryConnectionFactory = new RepositoryConnectionFactory();
    }
    
    @Override
    public RepositoryConnection getConnection() throws RepositorySourceException {
        if (null == url || 0 == url.trim().length()) {
            I18n msg = JcrConnectorI18n.propertyIsRequired;
            throw new RepositorySourceException(url, msg.text("url"));
        }
        
        try {
            setRepository(getRepositoryFactory().createRepository(url));
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        
        Credentials credentials = getCredentialsFactory().createCredentials(getUsername(), getPassword());
        return getRepositoryConnectionFactory().createRepositoryConnection(this, getRepository(), credentials);
    }

    @Override
    public RepositorySourceCapabilities getCapabilities() {
        return capabilities;
    }

    public String getUrl() {
        return url;
    }
    
    public void setUrl( String url ) {
        this.url = url;
    }

    protected RepositoryFactory getRepositoryFactory() {
        return repositoryFactory;
    }

    protected CredentialsFactory getCredentialsFactory() {
        return credentialsFactory;
    }

    protected RepositoryConnectionFactory getRepositoryConnectionFactory() {
        return repositoryConnectionFactory;
    }
}
