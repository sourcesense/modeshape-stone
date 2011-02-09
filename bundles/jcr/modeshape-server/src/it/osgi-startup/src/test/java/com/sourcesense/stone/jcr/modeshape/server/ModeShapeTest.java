package com.sourcesense.stone.jcr.modeshape.server;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.modeshape.graph.mimetype.ExtensionBasedMimeTypeDetector;
import org.modeshape.graph.mimetype.MimeTypeDetector;

public class ModeShapeTest extends AbstractTestCase {

    @Test
    public void shouldBeAbleToCreateExtensionBaseMimeTypeDetectorWithNew() throws Exception {
        MimeTypeDetector extensionBasedMimeTypeDetector = new ExtensionBasedMimeTypeDetector();
        assertNotNull(extensionBasedMimeTypeDetector);
    }
}
