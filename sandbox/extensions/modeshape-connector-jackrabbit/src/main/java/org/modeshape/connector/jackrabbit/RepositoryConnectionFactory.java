package org.modeshape.connector.jackrabbit;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import org.modeshape.connector.jcr.JcrRepositoryConnection;
import org.modeshape.connector.jcr.JcrRepositorySource;
import org.modeshape.graph.connector.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryConnectionFactory {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RepositoryConnection createRepositoryConnection( JcrRepositorySource repositorySource,
                                                            Repository repository,
                                                            Credentials credentials ) {
        logger.info("Creating a repository connection");
        JcrRepositoryConnection jcrRepositoryConnection = new JcrRepositoryConnection(repositorySource, repository, credentials);
        logger.info("Repository connection created");
        return jcrRepositoryConnection;
    }

}
