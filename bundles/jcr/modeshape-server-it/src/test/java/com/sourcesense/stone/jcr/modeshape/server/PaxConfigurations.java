package com.sourcesense.stone.jcr.modeshape.server;

import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.when;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;

public class PaxConfigurations {


    /**
     * this class cannot be instantiated
     */
    private PaxConfigurations() {
        // do nothing
    }

    static final String LUCENE_VERSION = "3.0.2";
    static final String JCIP_VERSION = "1.0.0";
    static final String JODA_TIME_VERSION = "1.6";
    static final String GOOGLE_COLLECTIONS_VERSION = "1.0-rc3";
    static final String STONE_VERSION = "1.0.0-SNAPSHOT";
    static final String CONFIGURATION_ADMIN_VERSION = "1.2.8";
    static final String JCR_VERSION = "2.0";
    static final String OSGI_VERSION = "4.2.0";
    static final String SLF4J_VERSION = "1.5.11";
    static final String SLING_VERSION = "2.1.0";
    static final String JACKRABBIT_VERSION = "2.0.0";
    static final String MODESHAPE_VERSION = "2.4.0.Final-stone-SNAPSHOT";
//    private static final String FELIX_WEB_VERSION = "2.0.5-SNAPSHOT";
    private static final String FELIX_WEB_VERSION = "2.2.0";

    static final String LUCENE_GROUP = "org.apache.lucene";
    static final String JCR_GROUP = "javax.jcr";
    static final String APACHE_COMMONS_GROUP = "org.apache.commons";
    static final String GOOGLE_COLLECTIONS_GROUP = "com.google.collections";
    static final String JCIP_GROUP = "net.jcip";
    static final String STONE_GROUP = "com.sourcesense.stone";
    static final String JODA_TIME_GROUP = "joda-time";
    static final String FELIX_GROUP = "org.apache.felix";
    static final String JACKRABBIT_GROUP = "org.apache.jackrabbit";
    static final String SLING_GROUP = "org.apache.sling";
    static final String OSGI_GROUP = "org.osgi";
    static final String SLF4J_GROUP = "org.slf4j";
    static final String MODESHAPE_GROUP = "org.modeshape";

    public static Option slingBasicConfiguration() {
        return composite(felix(),
                         mavenBundle(FELIX_GROUP, "org.apache.felix.scr", "1.6.0"),
                         slingCommonsLog(),
                         jcr(),
                         jackrabbit(),
                         sling(),
                         configurationAdmin(),
                         osgi(),
                         slf4j(),
                         systemProperty("sling.home").value("/tmp/sling"));
    }

    public static Option slingFullConfiguration() {
        return composite(slingBasicConfiguration(),
                         felixWeb(),
                         stoneJAASLoginResistration(),
                         systemProperty("org.osgi.service.http.port").value("9191"));
    }

    public static Option stoneInMemoryConfiguration() {
        return composite(stoneDependencies(), stone_in_memory());
    }

    public static Option stoneH2Configuration() {
        // return composite(stoneDependencies(), stone_h2());
        return composite(externalDependencies(), stone_h2());
    }

    public static Option stonePostgresConfiguration() {
        // return composite(stoneDependencies(), stone_postgres());
        return composite(externalDependencies(), stone_postgres());
    }

    public static Option jcr() {
        return mavenBundle(JCR_GROUP, "jcr", JCR_VERSION);
    }

    public static Option lucene() {
        return composite(wrappedBundle(mavenBundle(LUCENE_GROUP, "lucene-core", LUCENE_VERSION)),
                         wrappedBundle(mavenBundle(LUCENE_GROUP, "lucene-analyzers", LUCENE_VERSION)),
                         wrappedBundle(mavenBundle(LUCENE_GROUP, "lucene-misc", LUCENE_VERSION)),
                         wrappedBundle(mavenBundle(LUCENE_GROUP, "lucene-regex", LUCENE_VERSION)),
                         wrappedBundle(mavenBundle(LUCENE_GROUP, "lucene-snowball", LUCENE_VERSION)));
    }

    public static Option joda() {
        return composite(mavenBundle(JODA_TIME_GROUP, "joda-time", JODA_TIME_VERSION));
    }

    public static Option jcip() {
        return composite(mavenBundle(JCIP_GROUP, "com.springsource.net.jcip.annotations", JCIP_VERSION));
    }

    public static Option modeshape() {
        return composite(mavenBundle(MODESHAPE_GROUP, "modeshape-common", MODESHAPE_VERSION),
                         mavenBundle(MODESHAPE_GROUP, "modeshape-graph", MODESHAPE_VERSION),
                         mavenBundle(MODESHAPE_GROUP, "modeshape-cnd", MODESHAPE_VERSION),
                         mavenBundle(MODESHAPE_GROUP, "modeshape-repository", MODESHAPE_VERSION),
                         mavenBundle(MODESHAPE_GROUP, "modeshape-search-lucene", MODESHAPE_VERSION),
                         mavenBundle(MODESHAPE_GROUP, "modeshape-jcr-api", MODESHAPE_VERSION),
                         mavenBundle(MODESHAPE_GROUP, "modeshape-jcr", MODESHAPE_VERSION));
    }

    public static Option modeshapeWeb() {
        return composite(mavenBundle(MODESHAPE_GROUP, "modeshape-web-jcr", MODESHAPE_VERSION));
    }

    public static Option configurationAdmin() {
        return composite(mavenBundle(FELIX_GROUP, "org.apache.felix.configadmin", CONFIGURATION_ADMIN_VERSION));
    }

    public static Option stone( String classifier ) {
        return composite(mavenBundle(createStoneServerArtifactReference(classifier)));
    }

    public static Option stone_in_memory() {
        return composite(commonsIO(), commonsMath(), commonsCollections(), stone("in-memory"));
    }

    public static Option stone_h2() {
        return composite(commonsIO(), commonsMath(), commonsCollections(), stone("h2"));
    }

    public static Option stone_postgres() {
        return composite(commonsIO(), commonsMath(), commonsCollections(), stone("postgres"));
    }

    public static Option osgi() {
        return composite(mavenBundle(OSGI_GROUP, "org.osgi.core", OSGI_VERSION),
                         mavenBundle(OSGI_GROUP, "org.osgi.compendium", OSGI_VERSION));
    }

    public static Option jackrabbit() {
        return composite(mavenBundle(JACKRABBIT_GROUP, "jackrabbit-api", JACKRABBIT_VERSION),
                         mavenBundle(JACKRABBIT_GROUP, "jackrabbit-jcr-commons", JACKRABBIT_VERSION),
                         mavenBundle(JACKRABBIT_GROUP, "jackrabbit-jcr-rmi", JACKRABBIT_VERSION));
    }

    public static Option slf4j() {
        return composite(mavenBundle(SLF4J_GROUP, "slf4j-api", SLF4J_VERSION),
                         mavenBundle(SLF4J_GROUP, "slf4j-simple", SLF4J_VERSION).noStart());
    }

    public static Option sling() {
        return composite(mavenBundle(STONE_GROUP, "com.sourcesense.stone.jcr.base", STONE_VERSION),
                         mavenBundle(SLING_GROUP, "org.apache.sling.jcr.api", SLING_VERSION));
    }

    public static Option felixWeb() {
        return composite(mavenBundle(FELIX_GROUP, "org.apache.felix.http.jetty", FELIX_WEB_VERSION));
    }

    public static Option stoneWebConsoleSecurityProvider() {
        return composite(mavenBundle(STONE_GROUP, "com.sourcesense.stone.extensions.webconsolesecurityprovider", STONE_VERSION));
    }

    public static Option stoneJAASLoginResistration() {
        return composite(mavenBundle(STONE_GROUP, "com.sourcesense.stone.jaas.jaas-login-registration", STONE_VERSION));
    }

    public static Option googleCommons() {
        return wrappedBundle(mavenBundle(GOOGLE_COLLECTIONS_GROUP, "google-collections", GOOGLE_COLLECTIONS_VERSION));
    }

    public static Option commonsCollections() {
        return wrappedBundle(mavenBundle("commons-collections", "commons-collections", "3.2.1"));
    }

    public static Option debug() {
        boolean debugIsEnabled = Boolean.getBoolean("debug");
        return composite(when(debugIsEnabled).useOptions(vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")));
    }

    static Option slingCommonsLog() {
        return mavenBundle(SLING_GROUP, "org.apache.sling.commons.log", SLING_VERSION);
    }

    static Option stoneDependencies() {
        return composite(joda(), jcip(), googleCommons(), lucene(), modeshape());
    }

    static Option externalDependencies() {
        return mavenBundle(STONE_GROUP, "com.sourcesense.stone.external.dependencies", STONE_VERSION);
    }

    static Option h2() {
        return composite(mavenBundle("com.h2database", "h2", "1.3.152"));
    }

    static Option hibernate() {
        return composite(wrappedBundle(mavenBundle("org.hibernate", "hibernate-core", "3.6.1.Final")),
                         wrappedBundle(mavenBundle("org.hibernate", "hibernate-entitymanager", "3.6.1.Final")),
                         wrappedBundle(mavenBundle("org.hibernate", "hibernate-annotations", "3.5.2-Final")),
                         wrappedBundle(mavenBundle("org.hibernate", "hibernate-c3p0", "3.5.2-Final")),
                         wrappedBundle(mavenBundle("org.hibernate.javax.persistence", "hibernate-jpa-2.0-api", "1.0.0.Final")));
    }

    static Option modeshapeJPA() {
        return composite(mavenBundle(MODESHAPE_GROUP, "modeshape-connector-store-jpa", MODESHAPE_VERSION));
    }

    static Option commonsMath() {
        return composite(mavenBundle(APACHE_COMMONS_GROUP, "commons-math", "2.1"));
    }

    static Option commonsIO() {
        return composite(mavenBundle("commons-io", "commons-io", "1.4"));
    }

    private static MavenArtifactUrlReference createStoneServerArtifactReference( String classifier ) {
        MavenArtifactUrlReference mavenArtifactUrlReference = new MavenArtifactUrlReference();
        mavenArtifactUrlReference.groupId(STONE_GROUP);
        mavenArtifactUrlReference.artifactId("com.sourcesense.stone.jcr.modeshape.server");
        mavenArtifactUrlReference.version(STONE_VERSION);
        mavenArtifactUrlReference.classifier(classifier);
        return mavenArtifactUrlReference;
    }

}
