package com.sourcesense.stone.jcr.modeshape.server.performances;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

/**
 * <code>PathBasedQueryTest</code> implements a performance test executing a
 * query that has a path constraint with low selectivity, whereas the predicate
 * is very selective.
 */
class PathBasedQueryTest extends AbstractTest {

    private Session session;

    private Node root;

    @Override
    protected void beforeSuite() throws Exception {
        session = getRepository().login(getCredentials());
        root = session.getRootNode().addNode(
                getClass().getSimpleName(), "nt:unstructured");
        int count = 0;
        for (int i = 0; i < 5; i++) {
            Node n = root.addNode("node-" + i);
            for (int j = 0; j < 100; j++) {
                n.addNode("node-" + j).setProperty("count", count++);
            }
        }
        session.save();
    }

    @Override
    protected void runTest() throws Exception {
        QueryManager qm = session.getWorkspace().getQueryManager();
        @SuppressWarnings("deprecation")
        Query q = qm.createQuery("/jcr:root" + root.getPath() + "/*/*[@count = 250]",
                Query.XPATH);
        for (int i = 0; i < 10; i++) {
            q.execute().getNodes().nextNode();
        }
    }

    @Override
    protected void afterSuite() throws Exception {
        root.remove();
        session.save();
        session.logout();
    }
}
