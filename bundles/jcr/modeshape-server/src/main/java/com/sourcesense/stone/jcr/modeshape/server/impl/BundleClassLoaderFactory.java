package com.sourcesense.stone.jcr.modeshape.server.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.modeshape.common.component.ClassLoaderFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO fill me
 *
 * @version $Id$
 */
public class BundleClassLoaderFactory implements ClassLoaderFactory {

    private final BundleContext bundleContext;

    private final Logger log = LoggerFactory.getLogger(BundleClassLoaderFactory.class);

    public BundleClassLoaderFactory( ComponentContext componentContext ) {
        this.bundleContext = componentContext.getBundleContext();
    }

    public BundleClassLoaderFactory( BundleContext bundleContext ) {
        this.bundleContext = bundleContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassLoader getClassLoader( String... classpath ) {
        return new ClassLoader() {
            @Override
            protected Class<?> findClass( String name ) throws ClassNotFoundException {

                Bundle[] bundles = bundleContext.getBundles();
                for (Bundle bundle : bundles) {
                    try {
                        @SuppressWarnings( "rawtypes" )
                        Class loadedClass = bundle.loadClass(name);
                        if (log.isDebugEnabled()) {
                            log.debug("Class {} found in bundle {}", name, bundle.getSymbolicName());
                        }
                        return loadedClass;
                    } catch (ClassNotFoundException e) {
                        if (log.isDebugEnabled()) {
                            log.debug("Bundle {} does not contain class {}", bundle.getSymbolicName(), name);
                        }
                    }
                }
                throw new ClassNotFoundException(String.format("Class with name %s not found in loaded bundles", name));
            }

            @Override
            public Class<?> loadClass( String name ) throws ClassNotFoundException {
                return findClass(name);
            }

            @Override
            public InputStream getResourceAsStream( String name ) {
                URL resource = getResource(name);

                try {
                    return null != resource ? resource.openStream() : super.getResourceAsStream(name);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public URL getResource( String name ) {
                Bundle[] bundles = bundleContext.getBundles();
                for (Bundle bundle : bundles) {
                    URL resource = bundle.getResource(name);
                    if (null == resource) {
                        if (log.isDebugEnabled()) {
                            log.debug("Bundle {} does not contain resource {}", bundle.getSymbolicName(), name);
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Resource {} found in bundle {}", name, bundle.getSymbolicName());
                        }
                        return resource;
                    }
                }
                return null;
            }
        };
    };
}
