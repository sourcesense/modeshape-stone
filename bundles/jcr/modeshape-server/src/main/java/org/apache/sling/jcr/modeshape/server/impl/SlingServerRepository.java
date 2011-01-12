/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.jcr.modeshape.server.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.jcr.Credentials;
import javax.jcr.Repository;

import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.base.AbstractSlingRepository;
import org.osgi.framework.Bundle;

/**
 * The <code>SlingServerRepository</code> TODO
 *
 * @scr.component label="%repository.name" description="%repository.description"
 *          name="org.apache.sling.jcr.modeshape.server.SlingServerRepository"
 *          configurationFactory="true" policy="require"
 *
 * @scr.property name="service.vendor" value="The Apache Software Foundation"
 * @scr.property name="service.description"
 *      value="Factory for embedded Modeshape Repository Instances"
 */
public class SlingServerRepository extends AbstractSlingRepository
        implements Repository, SlingRepository {

    /**
     * The name of the configuration property defining the URL to the
     * repository configuration file (value is
     * "config").
     * <p>
     * If the configuration file is located in the local file system, the
     * "file:" scheme must still be specified.
     * <p>
     * This parameter is mandatory for this activator to start the repository.
     *
     * @scr.property value=""
     */
    public static final String REPOSITORY_CONFIG_URL = "config";

    /**
     * The name of the configuration property defining the file system
     * directory where the repository files are located (value is
     * "home").
     * <p>
     * This parameter is mandatory for this activator to start the repository.
     *
     * @scr.property value=""
     */
    public static final String REPOSITORY_HOME_DIR = "home";

    /**
     * @scr.property value=""
     */
    public static final String REPOSITORY_REGISTRATION_NAME = "name";

    //---------- Repository Management ----------------------------------------

    @Override
    protected Repository acquireRepository() {
        Repository repository = super.acquireRepository();
        if (repository != null) {
            return repository;
        }

//        @SuppressWarnings("unchecked")
//        Dictionary<String, Object> environment = this.getComponentContext().getProperties();
//        String configURLObj = (String) environment.get(REPOSITORY_CONFIG_URL);
//        String home = (String) environment.get(REPOSITORY_HOME_DIR);
//
//        // somewhat dirty hack to have the derby.log file in a sensible
//        // location, but don't overwrite anything already set
//        if (System.getProperty("derby.stream.error.file") == null) {
//            String derbyLog = home + "/derby.log";
//            System.setProperty("derby.stream.error.file", derbyLog);
//        }
//
//        InputStream ins = null;
//        try {
//
//            // check whether the URL is a file path
//            File configFile = new File(configURLObj);
//            if (configFile.canRead()) {
//
//                ins = new FileInputStream(configFile);
//                log(LogService.LOG_INFO, "Using configuration file " + configFile.getAbsolutePath());
//
//            } else {
//
//                try {
//
//                    URL configURL = new URL(configURLObj);
//                    ins = configURL.openStream();
//                    log(LogService.LOG_INFO, "Using configuration URL " + configURL);
//
//                } catch (MalformedURLException mue) {
//
//                    log(LogService.LOG_INFO, "Configuration File "
//                        + configFile.getAbsolutePath()
//                        + " has been lost, trying to recreate");
//
//                    final Bundle bundle = getComponentContext().getBundleContext().getBundle();
//                    SlingServerRepository.copyFile(bundle, "repository.xml", configFile);
//
//                    ins = new FileInputStream(configFile);
//                    log(LogService.LOG_INFO, "Using configuration file " + configFile.getAbsolutePath());
//                }
//            }
//
//            RepositoryConfig crc = RepositoryConfig.create(ins, home);
//            return RepositoryImpl.create(crc);
//
//        } catch (IOException ioe) {
//
//            log(LogService.LOG_ERROR,
//                "acquireRepository: IO problem starting repository from "
//                    + configURLObj + " in " + home, ioe);
//
//        } catch (RepositoryException re) {
//
//            log(LogService.LOG_ERROR,
//                "acquireRepository: Repository problem starting repository from "
//                    + configURLObj + " in " + home, re);
//        } finally {
//            if (ins != null) {
//                try {
//                    ins.close();
//                } catch (IOException ioe) {
//                    // ignore
//                }
//            }
//        }

        // got no repository ....
        return null;
    }

    @Override
    protected void disposeRepository(Repository repository) {
        super.disposeRepository(repository);

//        if (repository instanceof RepositoryImpl) {
//
//            try {
//                ((RepositoryImpl) repository).shutdown();
//            } catch (Throwable t) {
//                log(LogService.LOG_ERROR,
//                    "deactivate: Unexpected problem shutting down repository",
//                    t);
//            }
//
//        } else {
//            log(LogService.LOG_INFO,
//                "Repository is not a RepositoryImpl, nothing to do");
//        }
    }




    //---------- Helper -------------------------------------------------------

    public static void copyFile(Bundle bundle, String entryPath, File destFile) throws FileNotFoundException, IOException {
        if (destFile.canRead()) {
            // nothing to do, file exists
            return;
        }

        // copy from property
        URL entryURL = bundle.getEntry(entryPath);
        if (entryURL == null) {
            throw new FileNotFoundException(entryPath);
        }

        // check for a file property
        InputStream source = entryURL.openStream();
        copyStream(source, destFile);
    }

    public static void copyStream(InputStream source, File destFile) throws FileNotFoundException, IOException {
         OutputStream dest = null;

        try {

            // ensure path to parent folder of licFile
            destFile.getParentFile().mkdirs();

            dest = new FileOutputStream(destFile);
            byte[] buf = new byte[2048];
            int rd;
            while ( (rd = source.read(buf)) >= 0) {
                dest.write(buf, 0, rd);
            }


        } finally {
            if (dest != null) {
                try {
                    dest.close();
                } catch (IOException ignore) {
                }
            }

            // licSource is not null
            try {
                source.close();
            } catch (IOException ignore) {
            }
        }
    }

    /**
     * {@inheritDoc}
     * @see org.apache.sling.jcr.base.AbstractSlingRepository#getAdministrativeCredentials(java.lang.String)
     */
    @Override
    protected Credentials getAdministrativeCredentials(String adminUser) {
        return null; // new AdministrativeCredentials(adminUser);
    }

    /**
     * {@inheritDoc}
     * @see org.apache.sling.jcr.base.AbstractSlingRepository#getAnonCredentials(java.lang.String)
     */
    @Override
    protected Credentials getAnonCredentials(String anonUser) {
        return null; // new AnonCredentials(anonUser);
    }
}
