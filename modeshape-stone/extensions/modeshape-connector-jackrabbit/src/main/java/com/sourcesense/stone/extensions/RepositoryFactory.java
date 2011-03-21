package com.sourcesense.stone.extensions;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.jcr2spi.RepositoryImpl;
import org.apache.jackrabbit.jcr2spi.config.RepositoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryFactory {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Repository createRepository( String url ) throws RepositoryException {
        logger.info("Creating repository from url: " + url);
        RepositoryConfig config = new WebdavRepositoryConfig(url);
        return RepositoryImpl.create(config);
    }

}
