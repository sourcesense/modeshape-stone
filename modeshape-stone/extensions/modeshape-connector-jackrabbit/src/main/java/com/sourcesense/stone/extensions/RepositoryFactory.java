package com.sourcesense.stone.extensions;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.jcr2spi.RepositoryImpl;
import org.apache.jackrabbit.jcr2spi.config.RepositoryConfig;

public class RepositoryFactory {

    public Repository createRepository( String url ) throws RepositoryException {
        RepositoryConfig config = new WebdavRepositoryConfig(url);
        return RepositoryImpl.create(config);
    }

}
