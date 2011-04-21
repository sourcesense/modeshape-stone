package com.sourcesense.stone.extensions.it;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.jcr2spi.RepositoryImpl;
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

public class SPI2DAVJackrabbit {

    public static void main( String[] args ) {
        SPI2DAVJackrabbit spi2davJackrabbit = new SPI2DAVJackrabbit();
        try {
            spi2davJackrabbit.connect();
        } catch (LoginException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void connect() throws LoginException, RepositoryException {
        String url = "http://localhost:8080/server";

        final IdFactory idFactory = IdFactoryImpl.getInstance();
        final NameFactory nFactory = NameFactoryImpl.getInstance();
        final PathFactory pFactory = PathFactoryImpl.getInstance();
        final QValueFactory vFactory = QValueFactoryImpl.getInstance();
        final RepositoryServiceImpl webdavRepoService = new RepositoryServiceImpl(url, idFactory, nFactory, pFactory, vFactory);

        RepositoryConfig config = new RepositoryConfig() {
            public RepositoryService getRepositoryService() {
                return webdavRepoService;
            }

            public CacheBehaviour getCacheBehaviour() {
                return CacheBehaviour.INVALIDATE;
            }

            public int getItemCacheSize() {
                return 10000;
            }

            public int getPollTimeout() {
                return 0;
            }
        };

        Repository repository = RepositoryImpl.create(config);
        Session session = repository.login();
        
        Node rootNode = session.getRootNode();
        rootNode.addNode("system");
        session.save();
        
        session.logout();
        
        
        session = repository.login();
        Node node = session.getNode(session.getRootNode().getPath()+"myNode");
        System.out.println("myNode: " + node.toString());
        session.logout();
    }
}
