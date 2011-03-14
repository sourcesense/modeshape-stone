package com.sourcesense.stone.jcr.modeshape.server.performances;

import java.util.Random;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performance test for a two-way join that selects 300 pairs from
 * a set of 90k nodes. The query is constructed in a way that should
 * allow a smart implementation to perform the join quite efficiently.
 */
class TwoWayJoinTest extends AbstractTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int NODE_COUNT = 300;

    private final Random random = new Random();

    private Session session;

    private Node root;

    public void beforeSuite() throws RepositoryException {
        logger.info("I'm in beforeSuite");
        String joins = getRepository().getDescriptor("query.joins");

        logger.info("Found descriptor for joins: " + joins);

        if (joins == null || joins.equals("query.joins.none")) {
            String errorMsg = "Join queries not supported by this repository";
            logger.error(errorMsg);
            throw new RepositoryException(errorMsg);
        }

        session = loginWriter();
        root = session.getRootNode().addNode("testroot", "nt:unstructured");

        for (int i = 0; i < NODE_COUNT; i++) {
            Node node = root.addNode("node" + i, "nt:unstructured");
            node.setProperty("foo", i);

            logger.info("Added node " + node);

            for (int j = 0; j < NODE_COUNT; j++) {
                Node child = node.addNode("node" + j, "nt:unstructured");
                child.setProperty("bar", j);

                logger.info("Added child " + child + " to node " + node);
            }
            session.save();

            logger.info("Session saved");
        }

        logger.info("Exiting from beforeSuite");
    }

    public void runTest() throws Exception {

        logger.info("I'm in runTest");

        int x = random.nextInt(NODE_COUNT);

        logger.info("Node count calculated is " + x);

        String query =
            "SELECT a.foo AS a, b.bar AS b"
            + " FROM [nt:unstructured] AS a"
            + " INNER JOIN [nt:unstructured] AS b ON a.foo = b.bar"
            + " WHERE a.foo = " + x;

        logger.info("Executing query " + query);

        QueryManager manager = session.getWorkspace().getQueryManager();
        RowIterator iterator =
            manager.createQuery(query, "JCR-SQL2").execute().getRows();
        int count = 0;
        while (iterator.hasNext()) {
            Row row = iterator.nextRow();
            long a = row.getValue("a").getLong();
            long b = row.getValue("b").getLong();

            logger.info("a, b = " + a + ", " + b);

            if (a != x || a != b) {
                String errorMsg = "Invalid test result: " + x + " -> " + a + ", " + b;
                logger.error(errorMsg);
                throw new Exception(errorMsg);
            }
            count++;
        }
        if (count != NODE_COUNT) {
            String errorMsg = "Invalid test result count: " + count + " !=" + NODE_COUNT;
            logger.error(errorMsg);
            throw new Exception(errorMsg);
        }

        logger.info("Exiting from runTest");
    }

    public void afterSuite() throws RepositoryException {

        logger.info("I'm in afterSuite");

        for (int i = 0; i < NODE_COUNT; i++) {
            root.getNode("node" + i).remove();
            session.save();

            logger.info("Removed node " + (i+1));
        }

        root.remove();
        session.save();
        logger.info("Removed root node");
        logger.info("Exiting from afterSuite");
    }

}
