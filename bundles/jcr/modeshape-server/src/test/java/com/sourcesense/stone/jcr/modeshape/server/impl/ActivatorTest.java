package com.sourcesense.stone.jcr.modeshape.server.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;

import java.lang.reflect.Field;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;

public class ActivatorTest {

    private static final String CONFIG_ADMIN_NAME = ConfigurationAdmin.class.getName();

    @Test
    public void shouldVerifyConfigurationOnStartWhenConfigurationAdminServiceReferenceIsFound() throws Exception {

        ServiceReference configurationAdminServiceReference = mock(ServiceReference.class);

        BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.getServiceReference(CONFIG_ADMIN_NAME)).thenReturn(configurationAdminServiceReference);

        final AccessManagerFactoryTracker accessManagerFactoryTracker = mock(AccessManagerFactoryTracker.class);
        final ActivatorHelper activatorHelper = mock(ActivatorHelper.class);
        when(activatorHelper.createAccessManagerFactoryTracker()).thenReturn(accessManagerFactoryTracker);

        Activator activator = new Activator() {

            @Override
            protected ActivatorHelper getActivatorHelper() {
                return activatorHelper;
            }

            @Override
            protected AccessManagerFactoryTracker getAccessManagerFactoryTracker() {
                return accessManagerFactoryTracker;
            }
        };

        activator.start(bundleContext);

        verify(activatorHelper).verifyConfiguration(configurationAdminServiceReference);
    }

    @Test
    public void shouldRegisterItselfAsListenerOnStartWhenNoConfigurationAdminServiceReferenceIsFound() throws Exception {

        BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.getServiceReference(CONFIG_ADMIN_NAME)).thenReturn(null);

        Activator activator = new Activator() {
            @Override
            protected AccessManagerFactoryTracker getAccessManagerFactoryTracker() {
                return mock(AccessManagerFactoryTracker.class);
            }
        };

        activator.start(bundleContext);

        verify(bundleContext).addServiceListener(activator, "(" + Constants.OBJECTCLASS + "=" + CONFIG_ADMIN_NAME + ")");
    }

    @Test
    public void shouldCreateAccessManagerFactoryTrackerOnStartOnlyTheFirstTime() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);

        final AccessManagerFactoryTracker accessManagerFactoryTracker = mock(AccessManagerFactoryTracker.class);
        final ActivatorHelper activatorHelper = mock(ActivatorHelper.class);
        when(activatorHelper.createAccessManagerFactoryTracker()).thenReturn(accessManagerFactoryTracker);

        Activator activator = new Activator() {
            @Override
            protected AccessManagerFactoryTracker getAccessManagerFactoryTracker() {
                return null;
            }

            @Override
            protected ActivatorHelper getActivatorHelper() {
                return activatorHelper;
            }
        };

        activator.start(bundleContext);

        verify(activatorHelper).createAccessManagerFactoryTracker();
    }

    @Test
    public void shouldCloseAccessManagerFactoryTrackerOnStopOnlyWhenOpened() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);

        final AccessManagerFactoryTracker accessManagerFactoryTracker = mock(AccessManagerFactoryTracker.class);

        Activator activator = new Activator() {
            @Override
            protected AccessManagerFactoryTracker getAccessManagerFactoryTracker() {
                return accessManagerFactoryTracker;
            }
        };

        activator.stop(bundleContext);

        verify(accessManagerFactoryTracker).close();
    }

    @Test
    public void shouldNotCloseAccessManagerFactoryTrackerOnStopBecauseNotOpened() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);

        final AccessManagerFactoryTracker accessManagerFactoryTracker = mock(AccessManagerFactoryTracker.class);

        Activator activator = new Activator() {
            @Override
            protected AccessManagerFactoryTracker getAccessManagerFactoryTracker() {
                return null;
            }
        };

        activator.stop(bundleContext);

        verify(accessManagerFactoryTracker, never()).close();
    }

    @Test
    public void shouldVerifyConfigurationWhenServiceEventRegisteredIsNotifiedAndRemoveServiceListener() throws Exception {

        final ActivatorHelper activatorHelper = mock(ActivatorHelper.class);
        Activator activator = new Activator() {
            @Override
            protected ActivatorHelper getActivatorHelper() {
                return activatorHelper;
            }
        };

        BundleContext bundleContext = mock(BundleContext.class);
        injectBundleContextInto(activator, bundleContext);

        ServiceEvent serviceEvent = mock(ServiceEvent.class);
        when(serviceEvent.getType()).thenReturn(ServiceEvent.REGISTERED);

        ServiceReference aServiceReference = null; // It does not matter here
        when(serviceEvent.getServiceReference()).thenReturn(aServiceReference );


        activator.serviceChanged(serviceEvent);

        verify(activatorHelper).verifyConfiguration(aServiceReference);
        verify(bundleContext).removeServiceListener(activator);
    }

    @Test
    public void shouldNotVerifyConfigurationAndRemoveServiceListenerWhenServiceEventRegisteredIsNotNotified() throws Exception {

        final ActivatorHelper activatorHelper = mock(ActivatorHelper.class);
        Activator activator = new Activator() {
            @Override
            protected ActivatorHelper getActivatorHelper() {
                return activatorHelper;
            }
        };

        BundleContext bundleContext = mock(BundleContext.class);
        injectBundleContextInto(activator, bundleContext);

        ServiceEvent serviceEvent = mock(ServiceEvent.class);
        when(serviceEvent.getType()).thenReturn(ServiceEvent.MODIFIED);

        activator.serviceChanged(serviceEvent);

        verify(activatorHelper, never()).verifyConfiguration((ServiceReference)anyObject());
        verify(bundleContext, never()).removeServiceListener((ServiceListener)anyObject());
    }

    private void injectBundleContextInto( Activator activator, BundleContext bundleContext ) throws NoSuchFieldException, IllegalAccessException {
        Field activatorBundleContext = Activator.class.getDeclaredField("bundleContext");
        activatorBundleContext.setAccessible(true);
        activatorBundleContext.set(activator, bundleContext);
    }
}
