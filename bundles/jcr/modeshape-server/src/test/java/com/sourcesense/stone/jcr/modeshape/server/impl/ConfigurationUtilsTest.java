package com.sourcesense.stone.jcr.modeshape.server.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.File;
import org.junit.Test;
import org.osgi.framework.BundleContext;

public class ConfigurationUtilsTest {

    @Test
    public void shouldReturnHomeDirSetUsingPropertySlingRepositoryHome() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.getProperty(ConfigurationUtils.SLING_REPOSITORY_HOME)).thenReturn("aPath");
        when(bundleContext.getProperty(ConfigurationUtils.SLING_HOME)).thenReturn(null);
        
        ConfigurationUtils configurationUtil = new ConfigurationUtils(bundleContext);

        File homeDir = configurationUtil.getHomeDir();
        
        assertEquals("aPath/modeshape",homeDir.getPath());
    }

    @Test
    public void shouldReturnHomeDirSetUsingPropertySlingHome() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.getProperty(ConfigurationUtils.SLING_REPOSITORY_HOME)).thenReturn(null);
        when(bundleContext.getProperty(ConfigurationUtils.SLING_HOME)).thenReturn("aPath");
        
        ConfigurationUtils configurationUtil = new ConfigurationUtils(bundleContext);
        
        File homeDir = configurationUtil.getHomeDir();
        
        assertEquals("aPath/modeshape",homeDir.getPath());
    }

    @Test
    public void shouldReturnRepositoryNameWhenNoPropertySet() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.getProperty(ConfigurationUtils.SLING_REPOSITORY_HOME)).thenReturn(null);
        when(bundleContext.getProperty(ConfigurationUtils.SLING_HOME)).thenReturn(null);
        
        ConfigurationUtils configurationUtil = new ConfigurationUtils(bundleContext);
        
        File homeDir = configurationUtil.getHomeDir();
        
        assertEquals("modeshape",homeDir.getPath());
    }
    
}
