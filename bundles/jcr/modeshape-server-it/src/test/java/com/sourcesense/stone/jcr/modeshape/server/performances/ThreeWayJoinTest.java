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
 * Performance test for a three-way join that selects 50 triples from
 * a set of 125k nodes. The query is constructed in a way that should
 * allow a smart implementation to perform the join quite efficiently.
 */
class ThreeWayJoinTest extends AbstractTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int NODE_COUNT = 30;

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
            Node foo = root.addNode("node" + i, "nt:unstructured");
            foo.setProperty("foo", i);

            logger.info("Added node " + foo);

            for (int j = 0; j < NODE_COUNT; j++) {
                Node bar = foo.addNode("node" + j, "nt:unstructured");
                bar.setProperty("bar", j);

                logger.info("Added child " + bar);

                for (int k = 0; k < NODE_COUNT; k++) {
                    Node baz = bar.addNode("node" + k, "nt:unstructured");
                    baz.setProperty("baz", k);

                    logger.info("Added nephew " + baz);
                }
            }
            session.save();
        }

        logger.info("Exiting from beforeSuite");
    }

    public void runTest() throws Exception {

        logger.info("I'm in runTest");

        int x = random.nextInt(NODE_COUNT);

        logger.info("Node count calculated is " + x);

        String query =
            "SELECT a.foo AS a, b.bar AS b, c.baz AS c"
            + " FROM [nt:unstructured] AS a"
            + " INNER JOIN [nt:unstructured] AS b ON a.foo = b.bar"
            + " INNER JOIN [nt:unstructured] AS c ON b.bar = c.baz"
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
            long c = row.getValue("c").getLong();

            logger.info("a, b, c = " + a + ", " + b + ", " + c);

            if (a != x || b != x || c != x) {
                String errorMsg = "Invalid test result: " + x + " -> " + a + ", " + b + ", " + c;
                logger.error(errorMsg);
                throw new Exception(
                        "Invalid test result: "
                        + x + " -> " + a + ", " + b + ", " + c);
            }
            count++;
        }
        if (count != NODE_COUNT * NODE_COUNT * NODE_COUNT) {
            String errorMsg = "Invalid test result count: " + count;
            throw new Exception(errorMsg);
        }
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
