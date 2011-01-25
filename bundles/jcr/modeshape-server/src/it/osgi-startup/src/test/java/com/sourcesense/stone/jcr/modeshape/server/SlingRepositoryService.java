package com.sourcesense.stone.jcr.modeshape.server;

import org.apache.sling.jcr.api.SlingRepository;

/**
 * The <code>SlingServerRepository</code> TODO add policy="require"
 * 
 * @scr.component label="%repository.name" description="%repository.description" name=
 *                "com.sourcesense.stone.jcr.modeshape.server.SlingRepositoryService" policy="require"
 * @scr.service
 */
public class SlingRepositoryService {
    /**
     * The JCR Repository we access to resolve resources
     *
     * @scr.reference
     */
    private SlingRepository repository;

	public SlingRepository getRepository() {
		return repository;
	}

	public void setRepository(SlingRepository repository) {
		this.repository = repository;
	}
    
    
}
