package com.sourcesense.stone.jcr.modeshape.server;

import static org.junit.Assert.assertNotNull;

import javax.jcr.Repository;

import com.sourcesense.stone.jcr.base.util.RepositoryAccessor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

@RunWith(JUnit4TestRunner.class)
public class JNDIRMITest extends AbstractTestCase {

	@Test
	public void connectionRetrieved() throws Exception {

		RepositoryAccessor  repositoryAccessor = new RepositoryAccessor();
		Repository repository = repositoryAccessor.getRepositoryFromURL("jndi://modeshape:java.naming.factory.initial=org.modeshape.common.naming.DummyInitialContextFactory,java.naming.provider.url=localhost");
		assertNotNull(repository);
	}
}
