package com.sourcesense.stone.jcr.modeshape.server.performances;

import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.slingBasicConfiguration;
import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.stonePostgresConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.File;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

@RunWith(JUnit4TestRunner.class)
public final class PostgresPerformanceTest extends AbstractPerformanceTest {

    @Configuration
    public static Option[] configuration() {
        new File("/tmp/sling").delete();
        return options(slingBasicConfiguration(),
                stonePostgresConfiguration());
    }

    public PostgresPerformanceTest() {
        super("PostgreSQL");
    }

}
