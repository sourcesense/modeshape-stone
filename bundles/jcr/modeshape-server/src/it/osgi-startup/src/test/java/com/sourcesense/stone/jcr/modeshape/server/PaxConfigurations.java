package com.sourcesense.stone.jcr.modeshape.server;

import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
import org.ops4j.pax.exam.Option;

/*if[DEBUG]
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
end[DEBUG]*/

public class PaxConfigurations {

    private static final String LUCENE_GROUP = "org.apache.lucene";
    private static final String LUCENE_VERSION = "3.0.2";
    private static final String JCIP_VERSION = "1.0";
    private static final String JCIP_GROUP = "net.jcip";
    private static final String JODA_TIME_VERSION = "1.6";
    private static final String JODA_TIME_GROUP = "joda-time";
    static final String GOOGLE_COLLECTIONS_VERSION = "1.0-rc3";
    static final String STONE_VERSION = "1.0.0-SNAPSHOT";
    static final String CONFIGURATION_ADMIN_VERSION = "1.2.8";
    static final String JCR_VERSION = "2.0";
    static final String OSGI_VERSION = "4.1.0";
    static final String SLF4J_VERSION = "1.5.11";
    static final String SLING_VERSION = "2.1.0";
    static final String JACKRABBIT_VERSION = "2.0.0";

    static final String JCR_GROUP = "javax.jcr";
    static final String GOOGLE_COLLECTIONS_GROUP = "com.google.collections";
    static final String STONE_GROUP = "com.sourcesense.stone";
    static final String FELIX_GROUP = "org.apache.felix";
    static final String JACKRABBIT_GROUP = "org.apache.jackrabbit";
    static final String SLING_GROUP = "org.apache.sling";
    static final String OSGI_GROUP = "org.osgi";
    static final String SLF4J_GROUP = "org.slf4j";
    static final String MODESHAPE_VERSION = "2.4.0.Final-stone-SNAPSHOT";
    static final String MODESHAPE_GROUP = "org.modeshape";

    public static Option slingBasicConfiguration() {
        return composite(felix(),
        /*if[DEBUG]
        vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"),
        end[DEBUG]*/
        jcr(), jackrabbit(), sling(), configurationAdmin(), osgi(), slf4j(), systemProperty("sling.home").value("/tmp/sling"));
    }

    public static Option stoneConfiguration() {
        return composite(joda(), jcip(), lucene(), googleCommons(), modeshape(), stone());
    }

    static Option jcr() {
        return mavenBundle(JCR_GROUP, "jcr", JCR_VERSION);
    }

    static Option lucene() {
        return composite(
                         wrappedBundle(mavenBundle(LUCENE_GROUP, "lucene-core", LUCENE_VERSION)),
                         wrappedBundle(mavenBundle(LUCENE_GROUP, "lucene-analyzers", LUCENE_VERSION)),
                         wrappedBundle(mavenBundle(LUCENE_GROUP, "lucene-misc", LUCENE_VERSION)),
                         wrappedBundle(mavenBundle(LUCENE_GROUP, "lucene-regex", LUCENE_VERSION)),
                         wrappedBundle(mavenBundle(LUCENE_GROUP, "lucene-snowball", LUCENE_VERSION)));
    }

    static Option joda() {
        return composite(mavenBundle(JODA_TIME_GROUP, "joda-time", JODA_TIME_VERSION));
    }

    static Option jcip() {
        return composite(wrappedBundle(mavenBundle(JCIP_GROUP, "jcip-annotations", JCIP_VERSION)));
    }

    static Option modeshape() {
        return composite(mavenBundle(MODESHAPE_GROUP, "modeshape-common", MODESHAPE_VERSION),
                         mavenBundle(MODESHAPE_GROUP, "modeshape-graph", MODESHAPE_VERSION),
                         mavenBundle(MODESHAPE_GROUP, "modeshape-cnd", MODESHAPE_VERSION),
                         mavenBundle(MODESHAPE_GROUP, "modeshape-repository", MODESHAPE_VERSION),
                         mavenBundle(MODESHAPE_GROUP, "modeshape-search-lucene", MODESHAPE_VERSION),
                         mavenBundle(MODESHAPE_GROUP, "modeshape-jcr-api", MODESHAPE_VERSION),
                         mavenBundle(MODESHAPE_GROUP, "modeshape-jcr", MODESHAPE_VERSION));
    }

    static Option configurationAdmin() {
        return composite(mavenBundle(FELIX_GROUP, "org.apache.felix.configadmin", CONFIGURATION_ADMIN_VERSION));
    }

    static Option stone() {
        return composite(mavenBundle(STONE_GROUP, "com.sourcesense.stone.jcr.modeshape.server", STONE_VERSION));
    }

    static Option osgi() {
        return composite(mavenBundle(OSGI_GROUP, "org.osgi.core", OSGI_VERSION),
                         mavenBundle(OSGI_GROUP, "org.osgi.compendium", OSGI_VERSION));
    }

    static Option jackrabbit() {
        return composite(mavenBundle(JACKRABBIT_GROUP, "jackrabbit-api", JACKRABBIT_VERSION),
                         mavenBundle(JACKRABBIT_GROUP, "jackrabbit-jcr-commons", JACKRABBIT_VERSION),
                         mavenBundle(JACKRABBIT_GROUP, "jackrabbit-jcr-rmi", JACKRABBIT_VERSION));
    }

    static Option slf4j() {
        return composite(mavenBundle(SLF4J_GROUP, "slf4j-api", SLF4J_VERSION),
                         mavenBundle(SLF4J_GROUP, "slf4j-simple", SLF4J_VERSION).noStart());
    }

    static Option sling() {
        return composite(mavenBundle(SLING_GROUP, "org.apache.sling.jcr.api", SLING_VERSION),
                         mavenBundle(SLING_GROUP, "org.apache.sling.jcr.base", SLING_VERSION));
    }

    static Option googleCommons() {
        return wrappedBundle(mavenBundle(GOOGLE_COLLECTIONS_GROUP, "google-collections", GOOGLE_COLLECTIONS_VERSION));
    }

}
