package com.sourcesense.stone.jcr.modeshape.server.performances;

import java.util.Random;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * A {@link ConcurrentReadTest} with a single writer thread that continuously
 * updates the nodes being accessed by the readers.
 */
class ConcurrentReadWriteTest extends ConcurrentReadTest {

    public void beforeSuite() throws Exception {
        super.beforeSuite();

        addBackgroundJob(new Writer());
    }

    private class Writer implements Runnable {

        private final Session session = loginWriter();

        private final Random random = new Random();

        private long count = 0;

        public void run() {
            try {
                int i = random.nextInt(NODE_COUNT);
                int j = random.nextInt(NODE_COUNT);
                Node node = session.getRootNode().getNode(
                        "testroot/node" + i + "/node" + j);
                node.setProperty("count", count++);
                session.save();
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
