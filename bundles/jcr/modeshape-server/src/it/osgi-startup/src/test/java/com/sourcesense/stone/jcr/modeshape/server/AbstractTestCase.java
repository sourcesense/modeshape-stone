package com.sourcesense.stone.jcr.modeshape.server;

import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.mavenConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

/*if[DEBUG]
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
end[DEBUG]*/

import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.osgi.framework.BundleContext;

public abstract class AbstractTestCase {

    @Configuration
    public static Option[] configuration() {
        return options(felix(),
                       mavenConfiguration(),
                       systemProperty( "sling.repository.home" ).value( "/tmp" )
                       /*if[DEBUG]
                       ,vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
                       end[DEBUG]*/
                       );
    }
    
    @Inject
    protected BundleContext bundleContext;
}
