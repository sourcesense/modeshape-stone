package com.sourcesense.stone.jcr.modeshape.server.performances;

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

class BigFileWriteTest extends AbstractTest {

    private static final int FILE_SIZE = 100;

    private Session session;

    private Node file;

    public void beforeSuite() throws RepositoryException {
        session = loginWriter();
    }

    @SuppressWarnings("deprecation")
    public void runTest() throws RepositoryException {
        file = session.getRootNode().addNode(
                "BigFileWriteTest", "nt:file");
        Node content = file.addNode("jcr:content", "nt:resource");
        content.setProperty("jcr:mimeType", "application/octet-stream");
        content.setProperty("jcr:lastModified", Calendar.getInstance());
        content.setProperty(
                "jcr:data", new TestInputStream(FILE_SIZE * 1024 * 1024));
        session.save();
    }

    public void afterTest() throws RepositoryException {
        file.remove();
        session.save();
    }

}
