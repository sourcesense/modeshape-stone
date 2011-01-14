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

import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;
import java.util.ServiceLoader;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.base.AbstractSlingRepository;

/**
 * The <code>SlingServerRepository</code> TODO
 * 
 * @scr.component label="%repository.name" description="%repository.description" name=
 *                "org.apache.sling.jcr.modeshape.server.SlingServerRepository" configurationFactory="true" policy="require"
 * @scr.property name="service.vendor" value="JBoss"
 * @scr.property name="service.description" value="Factory for embedded Modeshape Repository Instances"
 */
public class SlingServerRepository extends AbstractSlingRepository implements Repository, SlingRepository {

    /**
     * The name of the configuration property defining the URL to the repository configuration file (value is "config").
     * <p>
     * If the configuration file is located in the local file system, the "file:" scheme must still be specified.
     * <p>
     * This parameter is mandatory for this activator to start the repository.
     * 
     * @scr.property value=""
     */
    public static final String REPOSITORY_CONFIG_URL = "config";

    private static final Repository NO_REPOSITORY = null;

    private RepositoryFactory repositoryFactory;

    @Override
    protected Repository acquireRepository() {
        Repository repository = super.acquireRepository();
        return repository != NO_REPOSITORY ? repository : findSuitableRepository();
    }

    private Repository findSuitableRepository() {
        @SuppressWarnings( "unchecked" )
        Dictionary<String, Object> environment = this.getComponentContext().getProperties();
        String configURL = (String)environment.get(REPOSITORY_CONFIG_URL);

        Map<String, String> parameters = Collections.singletonMap("org.modeshape.jcr.URL", configURL);
        Repository repository = null;

        for (RepositoryFactory factory : ServiceLoader.load(RepositoryFactory.class)) {
            try {
                repository = factory.getRepository(parameters);
                if (repository != null) {
                    repositoryFactory = factory;
                    return repository;
                }
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }

        return NO_REPOSITORY;
    }

    @Override
    protected void disposeRepository( Repository repository ) {
        super.disposeRepository(repository);

        if (repositoryFactory instanceof org.modeshape.jcr.api.RepositoryFactory) {
            ((org.modeshape.jcr.api.RepositoryFactory)repositoryFactory).shutdown();
        }
    }
}
