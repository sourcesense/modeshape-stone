package com.sourcesense.stone.extensions;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import org.modeshape.connector.jcr.JcrRepositoryConnection;
import org.modeshape.connector.jcr.JcrRepositorySource;
import org.modeshape.graph.connector.RepositoryConnection;

public class RepositoryConnectionFactory {

    public RepositoryConnection createRepositoryConnection( JcrRepositorySource repositorySource,
                                                            Repository repository,
                                                            Credentials credentials ) {
        return new JcrRepositoryConnection(repositorySource, repository, credentials);
    }

}
