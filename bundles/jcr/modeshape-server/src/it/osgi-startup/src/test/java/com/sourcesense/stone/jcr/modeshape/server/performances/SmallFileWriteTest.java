package com.sourcesense.stone.jcr.modeshape.server.performances;

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class SmallFileWriteTest extends AbstractTest {

    private static final int FILE_COUNT = 100;

    private static final int FILE_SIZE = 10;

    private Session session;

    private Node root;

    public void beforeSuite() throws RepositoryException {
        session = loginWriter();
    }

    public void beforeTest() throws RepositoryException {
        root = session.getRootNode().addNode("SmallFileWriteTest", "nt:folder");
        session.save();
    }

    @SuppressWarnings("deprecation")
    public void runTest() throws Exception {
        for (int i = 0; i < FILE_COUNT; i++) {
            Node file = root.addNode("file" + i, "nt:file");
            Node content = file.addNode("jcr:content", "nt:resource");
            content.setProperty("jcr:mimeType", "application/octet-stream");
            content.setProperty("jcr:lastModified", Calendar.getInstance());
            content.setProperty(
                    "jcr:data", new TestInputStream(FILE_SIZE * 1024));
        }
    }

    public void afterTest() throws RepositoryException {
        root.remove();
        session.save();
    }

}
