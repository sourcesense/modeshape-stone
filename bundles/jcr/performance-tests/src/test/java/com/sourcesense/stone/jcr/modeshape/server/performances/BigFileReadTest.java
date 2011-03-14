package com.sourcesense.stone.jcr.modeshape.server.performances;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;

class BigFileReadTest extends AbstractTest {

    private static final int FILE_COUNT = 10;

    private static final int FILE_SIZE = 100;

    private Session session;

    private Node root;

    private int i = 0;

    @SuppressWarnings("deprecation")
    public void beforeSuite() throws RepositoryException {
        session = getRepository().login(getCredentials());

        root = session.getRootNode().addNode(
                "BigFileReadTest", "nt:folder");
        for (int i = 0; i < FILE_COUNT; i++) {
            Node file = root.addNode("file" + i, "nt:file");
            Node content = file.addNode("jcr:content", "nt:resource");
            content.setProperty("jcr:mimeType", "application/octet-stream");
            content.setProperty("jcr:lastModified", Calendar.getInstance());
            content.setProperty(
                    "jcr:data", new TestInputStream(FILE_SIZE * 1024 * 1024));
        }
        session.save();
    }

    public void runTest() throws Exception {
        Node file = root.getNode("file" + (i++ % FILE_COUNT));
        Node content = file.getNode("jcr:content");
        @SuppressWarnings("deprecation")
        InputStream stream = content.getProperty("jcr:data").getStream();
        try {
            IOUtils.copy(stream, new NullOutputStream());
        } finally {
            stream.close();
        }
    }

    public void afterSuite() throws RepositoryException {
        root.remove();
        session.save();
        session.logout();
    }

}
