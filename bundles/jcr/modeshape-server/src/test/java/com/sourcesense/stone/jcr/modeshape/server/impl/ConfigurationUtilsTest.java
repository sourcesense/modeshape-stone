package com.sourcesense.stone.jcr.modeshape.server.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.File;
import org.junit.After;
import org.junit.Test;
import org.osgi.framework.BundleContext;

public class ConfigurationUtilsTest {

    @Test
    public void shouldReturnHomeDirSetUsingPropertySlingRepositoryHome() throws Exception {
        BundleContext bundleContext = programBundleContextToReturn("aPath", null);
        ConfigurationUtils configurationUtil = new ConfigurationUtils(bundleContext);

        File homeDir = configurationUtil.getHomeDir();

        assertEquals("aPath/modeshape", homeDir.getPath());
    }

    @Test
    public void shouldReturnHomeDirSetUsingPropertySlingHome() throws Exception {
        BundleContext bundleContext = programBundleContextToReturn(null, "aPath");
        ConfigurationUtils configurationUtil = new ConfigurationUtils(bundleContext);

        File homeDir = configurationUtil.getHomeDir();

        assertEquals("aPath/modeshape", homeDir.getPath());
    }

    @Test
    public void shouldReturnRepositoryNameWhenNoPropertySet() throws Exception {
        BundleContext bundleContext = programBundleContextToReturn(null, null);
        ConfigurationUtils configurationUtil = new ConfigurationUtils(bundleContext);

        File homeDir = configurationUtil.getHomeDir();

        assertEquals("modeshape", homeDir.getPath());
    }

    private BundleContext programBundleContextToReturn( String slingRepositoryHome,
                                               String slingHome ) {
        BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.getProperty(ConfigurationUtils.SLING_REPOSITORY_HOME)).thenReturn(slingRepositoryHome);
        when(bundleContext.getProperty(ConfigurationUtils.SLING_HOME)).thenReturn(slingHome);
        return bundleContext;
    }

    @After
    public void tearDown() {
        deleteResource("aPath/modeshape");
        deleteResource("aPath");
    }

    private void deleteResource( String path ) {
        File aPathDir = new File(path);
        aPathDir.delete();
    }
}
