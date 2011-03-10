package com.sourcesense.stone.jcr.modeshape.server.performances;

import java.util.Random;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test case that traverses 10k unstructured nodes (100x100) while
 * 50 concurrent readers randomly access nodes from within this tree.
 */
class ConcurrentReadTest extends AbstractTest {

    protected static final int NODE_COUNT = 100;

    private static final int READER_COUNT = getScale(20);

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Session session;

    protected Node root;

    public void beforeSuite() throws Exception {
        this.logger.info("Initializing storage with {} nodes");

        session = loginWriter();
        root = session.getRootNode().addNode("testroot", "nt:unstructured");
        for (int i = 0; i < NODE_COUNT; i++) {
            Node node = root.addNode("node" + i, "nt:unstructured");
            for (int j = 0; j < NODE_COUNT; j++) {
                node.addNode("node" + j, "nt:unstructured");
            }
            session.save();
        }

        this.logger.info("Done! Initializing {} readers", READER_COUNT);

        for (int i = 0; i < READER_COUNT; i++) {
            this.logger.info("Adding {} reader");
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

                logger.info("[{}] looking for node testroot/node{}/node{}",
                        new Object[] {
                            Thread.currentThread().getName(),
                            i,
                            j
                        }
                );

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
