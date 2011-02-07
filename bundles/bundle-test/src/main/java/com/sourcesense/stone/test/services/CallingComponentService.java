package com.sourcesense.stone.test.services;

import org.apache.sling.jcr.api.SlingRepository;

/**
 * @scr.component immediate="true" label="%resource.resolver.name"
 *                description="%resource.resolver.description" specVersion="1.1"
 * @scr.service interface="com.sourcesense.stone.test.services.SimpleService"
 * 
 * @scr.property name="service.vendor" value="Sourcesense"
 */
public class CallingComponentService implements SimpleService {
	
    /**
     * The JCR Repository we access to resolve resources
     *
     * @scr.reference
     */
	private SlingRepository slingRepository;
	
	public CallingComponentService() {
		
	}
	
	public SlingRepository getComponent() {
		return slingRepository;
	}

}
