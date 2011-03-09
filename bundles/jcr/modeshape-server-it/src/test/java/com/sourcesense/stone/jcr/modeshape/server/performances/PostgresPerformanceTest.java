package com.sourcesense.stone.jcr.modeshape.server.performances;

import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.debug;
import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.slingBasicConfiguration;
import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.stonePostgresConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

@RunWith(JUnit4TestRunner.class)
public final class PostgresPerformanceTest extends AbstractPerformanceTest {

    @Configuration
    public static Option[] configuration() {
        try {
            FileUtils.deleteDirectory(new File("/tmp/sling"));
        } catch (IOException e) {
            // do it quietly
        }
        return options(debug(),
                slingBasicConfiguration(),
                stonePostgresConfiguration(),
                vmOption("-Duser.language=en -Duser.country=US -Xms2048m -Xmx2048m -XX:PermSize=128m -XX:-UseGCOverheadLimit"));
    }

    public PostgresPerformanceTest() {
        super("PostgreSQL");
    }

}
