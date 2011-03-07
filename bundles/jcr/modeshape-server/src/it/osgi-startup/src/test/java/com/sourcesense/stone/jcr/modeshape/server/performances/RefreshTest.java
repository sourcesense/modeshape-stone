package com.sourcesense.stone.jcr.modeshape.server.performances;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

class RefreshTest extends AbstractTest {

    private Session session;

    public void beforeSuite() throws RepositoryException {
        session = getRepository().login();
    }

    public void runTest() throws Exception {
        for (int i = 0; i < 1000000; i++) {
            session.refresh(false);
        }
    }

    public void afterSuite() throws RepositoryException {
        session.logout();
    }

}
