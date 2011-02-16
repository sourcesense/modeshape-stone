package com.sourcesense.stone.jcr.modeshape.server.impl;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods collection for {@link BundleContext}.
 *
 * @version $Id$
 */
public class ConfigurationUtils {

    static final String SLING_HOME = "sling.home";

    static final String SLING_REPOSITORY_HOME = "sling.repository.home";

    private static final String SLING_REPOSITORY_NAME = "sling.repository.name";

    private static final String MODESHAPE_REPOSITORY_XML = "modeshape-repository.xml";

    private static final Logger log = LoggerFactory.getLogger(ConfigurationUtils.class);

    private final BundleContext bundleContext;

    /**
     * Copy a stream from the give source to the destination file.
     *
     * @param source
     * @param destFile
     * @throws Exception
     */
    static void copyStream(InputStream source, File destFile ) throws Exception {
        OutputStream dest = null;

        try {
            dest = writeFile(source, destFile);
        } finally {
            closeQuietly(source);
            closeQuietly(dest);
        }
    }

    /**
     * Close the give {@link Closeable} swallowing any exception, if occurs.
     *
     * @param closeable
     */
    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // do nothing, close quietly
            }
        }
    }

    /**
     * 
     *
     * @param source
     * @param destFile
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static OutputStream writeFile( InputStream source,
                                           File destFile ) throws FileNotFoundException, IOException {
        OutputStream dest;
        destFile.getParentFile().mkdirs();

        dest = new FileOutputStream(destFile);
        byte[] buf = new byte[2048];
        int rd;
        while ((rd = source.read(buf)) >= 0) {
            dest.write(buf, 0, rd);
        }
        return dest;
    }

    /**
     * 
     *
     * @param bundle
     * @param entryPath
     * @param destFile
     * @throws Exception
     */
    static void copyFile( Bundle bundle,
                          String entryPath,
                          File destFile ) throws Exception {
        if (destFile.canRead()) {
            return;
        }

        URL entryURL = bundle.getEntry(entryPath);
        if (entryURL == null) {
            throw new FileNotFoundException(entryPath);
        }

        InputStream source = entryURL.openStream();
        copyStream(source, destFile);
    }

    /**
     * 
     *
     * @param bundleContext
     */
    ConfigurationUtils( BundleContext bundleContext ) {
        this.bundleContext = bundleContext;
    }

    /**
     * 
     *
     * @return
     */
    File getHomeDir() {
        File homeDir;

        String repoHomePath = bundleContext.getProperty(SLING_REPOSITORY_HOME);
        String slingHomePath = bundleContext.getProperty(SLING_HOME);

        String repositoryName = getRepositoryName();
        if (repoHomePath != null) {
            homeDir = new File(repoHomePath, repositoryName);
        } else if (slingHomePath != null) {
            homeDir = new File(slingHomePath, repositoryName);
        } else {
            homeDir = new File(repositoryName);
        }

        log.info("Creating default config for Modeshape in " + homeDir);
        if (!homeDir.isDirectory()) {
            if (!homeDir.mkdirs()) {
                log.info("verifyConfiguration: Cannot create Modeshape home " + homeDir
                         + ", failed creating default configuration");
                return null;
            }
        }

        return homeDir;
    }

    /**
     * 
     *
     * @return
     */
    String getRepositoryName() {
        String repoName = bundleContext.getProperty(SLING_REPOSITORY_NAME);
        if (repoName != null) {
            return repoName;
        }
        return "modeshape";
    }

    /**
     * 
     *
     * @param homeDir
     * @return
     * @throws Exception
     */
    String getConfigFileUrl( File homeDir ) throws Exception {

        String repoConfigFileUrl = bundleContext.getProperty("sling.repository.config.file.url");
        if (repoConfigFileUrl != null) {
            URL configFileUrl = null;
            try {
                configFileUrl = new URL(repoConfigFileUrl);
                return repoConfigFileUrl;
            } catch (MalformedURLException e) {
                configFileUrl = new URL("file:///" + repoConfigFileUrl);
                File configFile = new File(configFileUrl.getFile());
                if (configFile.canRead()) {
                    return configFileUrl.toString();
                }
            }
        }

        File configFile = new File(homeDir, MODESHAPE_REPOSITORY_XML);
        boolean copied = false;

        try {
            URL contextConfigURL = new URL("context:" + MODESHAPE_REPOSITORY_XML);
            InputStream contextConfigStream = contextConfigURL.openStream();
            if (contextConfigStream != null) {
                ConfigurationUtils.copyStream(contextConfigStream, configFile);
                copied = true;
            }
        } catch (Exception e) {
        }

        if (!copied) {
            ConfigurationUtils.copyFile(bundleContext.getBundle(), MODESHAPE_REPOSITORY_XML, configFile);
        }
        return configFile.toURI().toURL().toString();
    }

}
