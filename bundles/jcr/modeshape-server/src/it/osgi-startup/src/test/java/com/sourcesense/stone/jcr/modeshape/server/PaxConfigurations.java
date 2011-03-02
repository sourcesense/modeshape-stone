package com.sourcesense.stone.jcr.modeshape.server;

import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.Option;
/*if[DEBUG]
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
end[DEBUG]*/

public class PaxConfigurations {

    static final String OSGI_VERSION = "4.1.0";
    static final String SLF4J_VERSION = "1.5.0";
    static final String SLING_VERSION = "2.1.0";
    static final String JACKRABBIT_VERSION = "2.0.0";
    
    static final String JACKRABBIT_GROUP = "org.apache.jackrabbit";
    static final String SLING_GROUP = "org.apache.sling";
    static final String OSGI_GROUP = "org.osgi";
    static final String SLF4J_GROUP = "org.slf4j";

    public static Option[] modeShapeConfiguration() {
        return options(felix(),
                       /*if[DEBUG]
                       vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"),
                       end[DEBUG]*/
                       mavenBundle("javax.jcr","jcr","2.0"),
                       
                       jackrabbit(),
                       sling(),
                       
                       osgi(),
                       slf4j(),
                       stone(),
                       
                       systemProperty("sling.home").value("/tmp/sling"));
    }

      static Option stone() {
        return composite(mavenBundle("com.sourcesense.stone","com.sourcesense.stone.jcr.modeshape.server","1.0.0-SNAPSHOT"),
                         mavenBundle("com.sourcesense.stone","com.sourcesense.stone.external.dependencies","1.0.0-SNAPSHOT"));
    }

    static Option osgi() {
        return composite(mavenBundle(OSGI_GROUP,"org.osgi.core",OSGI_VERSION),
                         mavenBundle(OSGI_GROUP,"org.osgi.compendium",OSGI_VERSION));
    }

    static Option jackrabbit() {
        return composite(mavenBundle(JACKRABBIT_GROUP,"jackrabbit-api",JACKRABBIT_VERSION),
                  mavenBundle(JACKRABBIT_GROUP,"jackrabbit-jcr-commons",JACKRABBIT_VERSION),
                  mavenBundle(JACKRABBIT_GROUP,"jackrabbit-jcr-rmi",JACKRABBIT_VERSION));
    }

    static Option slf4j() {
        return composite(mavenBundle(SLF4J_GROUP,"slf4j-api",SLF4J_VERSION),
                         mavenBundle(SLF4J_GROUP,"slf4j-simple",SLF4J_VERSION).noStart());
    }

     static Option sling() {
        return composite(mavenBundle(SLING_GROUP,"org.apache.sling.jcr.api",SLING_VERSION),
                         mavenBundle(SLING_GROUP,"org.apache.sling.jcr.base",SLING_VERSION));
    }

}
