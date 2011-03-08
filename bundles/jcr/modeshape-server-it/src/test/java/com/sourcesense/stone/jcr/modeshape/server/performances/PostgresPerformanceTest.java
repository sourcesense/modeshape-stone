package com.sourcesense.stone.jcr.modeshape.server.performances;

import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.googleCommons;
import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.slingBasicConfiguration;
import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.stonePostgresConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

@RunWith(JUnit4TestRunner.class)
public final class PostgresPerformanceTest extends AbstractPerformanceTest {

    @Configuration
    public static Option[] configuration() {
        return options(slingBasicConfiguration(),
                googleCommons(),
                stonePostgresConfiguration());
    }

    public PostgresPerformanceTest() {
        super("PostgreSQL");
    }

}
