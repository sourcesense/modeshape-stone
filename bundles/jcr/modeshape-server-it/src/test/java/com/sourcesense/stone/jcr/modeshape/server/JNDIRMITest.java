package com.sourcesense.stone.jcr.modeshape.server;
import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.slingBasicConfiguration;
import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.stoneInMemoryConfiguration;
import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.debug;
import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.options;
import javax.jcr.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import com.sourcesense.stone.jcr.base.util.RepositoryAccessor;

@RunWith(JUnit4TestRunner.class)
public class JNDIRMITest {

    @Inject
    BundleContext bundleContext;
    
    @Configuration
    public Option[] configuration() {
        return options(debug(), slingBasicConfiguration(), stoneInMemoryConfiguration());
    }
    
	@Test
	public void connectionRetrieved() throws Exception {

		RepositoryAccessor  repositoryAccessor = new RepositoryAccessor();
		Repository repository = repositoryAccessor.getRepositoryFromURL("jndi://modeshape:java.naming.factory.initial=org.modeshape.common.naming.DummyInitialContextFactory,java.naming.provider.url=localhost");
		assertNotNull(repository);
	}
}
