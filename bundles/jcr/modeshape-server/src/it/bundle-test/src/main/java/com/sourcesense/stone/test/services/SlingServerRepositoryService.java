package com.sourcesense.stone.test.services;

import com.sourcesense.stone.jcr.modeshape.server.impl.SlingServerRepository;


/**
 * @scr.component
 * @scr.service
 * 
 * @scr.property name="service.vendor" value="Sourcesense"
 * @scr.property name="service.description" value="Sling Server Repository Service Test"
 * @scr.reference name="slingServerRepository"
 *                interface="com.sourcesense.stone.jcr.modeshape.server.impl.SlingServerRepository"
 */
public class SlingServerRepositoryService implements SimpleService {

	private SlingServerRepository slingServerRepository;
	
	public SlingServerRepositoryService() {
		
	}

	protected void bindSlingServerRepository(
			SlingServerRepository slingServerRepository) {
		this.slingServerRepository = slingServerRepository;
	}

	protected void unbindSlingServerRepository(
			SlingServerRepository slingServerRepository) {
		this.slingServerRepository = null;
	}

	public SlingServerRepository getRepository() {
		return slingServerRepository;
	}

}
