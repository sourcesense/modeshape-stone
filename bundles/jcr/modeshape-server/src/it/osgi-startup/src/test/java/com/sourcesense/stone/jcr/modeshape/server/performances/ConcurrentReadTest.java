package com.sourcesense.stone.jcr.modeshape.server.performances;

import java.util.Random;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Test case that traverses 10k unstructured nodes (100x100) while
 * 50 concurrent readers randomly access nodes from within this tree.
 */
class ConcurrentReadTest extends AbstractTest {

    protected static final int NODE_COUNT = 100;

    private static final int READER_COUNT = getScale(20);

    private Session session;

    protected Node root;

    public void beforeSuite() throws Exception {
        session = loginWriter();
        root = session.getRootNode().addNode("testroot", "nt:unstructured");
        for (int i = 0; i < NODE_COUNT; i++) {
            Node node = root.addNode("node" + i, "nt:unstructured");
            for (int j = 0; j < NODE_COUNT; j++) {
                node.addNode("node" + j, "nt:unstructured");
            }
            session.save();
        }

        for (int i = 0; i < READER_COUNT; i++) {
            addBackgroundJob(new Reader());
        }
    }

    private class Reader implements Runnable {

        private final Session session = loginReader();

        private final Random random = new Random();

        public void run() {
            try {
                int i = random.nextInt(NODE_COUNT);
                int j = random.nextInt(NODE_COUNT);
                session.getRootNode().getNode(
                        "testroot/node" + i + "/node" + j);
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void runTest() throws Exception {
        Reader reader = new Reader();
        for (int i = 0; i < 1000; i++) {
            reader.run();
        }
    }

    public void afterSuite() throws Exception {
        for (int i = 0; i < NODE_COUNT; i++) {
            root.getNode("node" + i).remove();
            session.save();
        }

        root.remove();
        session.save();
    }

}
