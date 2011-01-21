package com.sourcesense.stone.jcr.modeshape.server.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;

public class ActivatorTest {

    private static final String CONFIG_ADMIN_NAME = ConfigurationAdmin.class.getName();

    @Test
    public void shouldVerifyConfigurationWhenConfigurationAdminServiceReferenceIsFound() throws Exception {

        ServiceReference configurationAdminServiceReference = mock(ServiceReference.class);

        BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.getServiceReference(CONFIG_ADMIN_NAME)).thenReturn(configurationAdminServiceReference);

        final ActivatorHelper activatorHelper = mock(ActivatorHelper.class);

        Activator activator = new Activator() {

            @Override
            protected ActivatorHelper getActivatorHelper() {
                return activatorHelper;
            }
        };

        activator.start(bundleContext);

        verify(activatorHelper).verifyConfiguration(configurationAdminServiceReference);
    }

    @Test
    public void shouldRegisterItselfAsListenerWhenNoConfigurationAdminServiceReferenceIsFound() throws Exception {

        BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.getServiceReference(CONFIG_ADMIN_NAME)).thenReturn(null);

        Activator activator = new Activator();

        activator.start(bundleContext);

        verify(bundleContext).addServiceListener(activator, "(" + Constants.OBJECTCLASS + "=" + CONFIG_ADMIN_NAME + ")");
    }

    @Test
    public void shouldOpenAccessManagerFactoryTrackerOnlyTheFirstTime() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);

        final AccessManagerFactoryTracker accessManagerFactoryTracker = mock(AccessManagerFactoryTracker.class);
        Activator activator = new Activator() {
            @Override
            protected AccessManagerFactoryTracker getAccessManagerFactoryTracker() {
                return null;
            }

            @Override
            protected AccessManagerFactoryTracker createAccessManagerFactoryTracker( BundleContext bundleContext ) {
                return accessManagerFactoryTracker;
            }
        };

        activator.start(bundleContext);

        verify(accessManagerFactoryTracker).open();
    }

    @Test
    public void shouldNotOpenAccessManagerFactoryTrackerWhenAlreadyOpened() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);

        final AccessManagerFactoryTracker accessManagerFactoryTracker = mock(AccessManagerFactoryTracker.class);
        Activator activator = new Activator() {
            @Override
            protected AccessManagerFactoryTracker getAccessManagerFactoryTracker() {
                return mock(AccessManagerFactoryTracker.class);
            }

            @Override
            protected AccessManagerFactoryTracker createAccessManagerFactoryTracker( BundleContext bundleContext ) {
                return accessManagerFactoryTracker;
            }
        };

        activator.start(bundleContext);

        verify(accessManagerFactoryTracker, never()).open();
    }
}
