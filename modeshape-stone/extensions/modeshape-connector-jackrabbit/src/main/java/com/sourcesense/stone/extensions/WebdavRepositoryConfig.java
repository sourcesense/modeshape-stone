package com.sourcesense.stone.extensions;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.jcr2spi.config.CacheBehaviour;
import org.apache.jackrabbit.jcr2spi.config.RepositoryConfig;
import org.apache.jackrabbit.spi.IdFactory;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.QValueFactory;
import org.apache.jackrabbit.spi.RepositoryService;
import org.apache.jackrabbit.spi.commons.identifier.IdFactoryImpl;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.name.PathFactoryImpl;
import org.apache.jackrabbit.spi.commons.value.QValueFactoryImpl;
import org.apache.jackrabbit.spi2dav.RepositoryServiceImpl;

public class WebdavRepositoryConfig implements RepositoryConfig {

    private RepositoryService webdavRepositoryService;

    public WebdavRepositoryConfig( String url ) throws RepositoryException {
        IdFactory idFactory = IdFactoryImpl.getInstance();
        NameFactory nFactory = NameFactoryImpl.getInstance();
        PathFactory pFactory = PathFactoryImpl.getInstance();
        QValueFactory vFactory = QValueFactoryImpl.getInstance();
        webdavRepositoryService = new RepositoryServiceImpl(url, idFactory, nFactory, pFactory, vFactory);
    }

    @Override
    public CacheBehaviour getCacheBehaviour() {
        return null;
    }

    @Override
    public int getItemCacheSize() {
        return 0;
    }

    @Override
    public int getPollTimeout() {
        return 0;
    }

    @Override
    public RepositoryService getRepositoryService() throws RepositoryException {
        return webdavRepositoryService;
    }

}
