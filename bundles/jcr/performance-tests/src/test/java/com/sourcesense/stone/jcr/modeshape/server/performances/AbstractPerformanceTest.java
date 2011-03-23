package com.sourcesense.stone.jcr.modeshape.server.performances;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Credentials;
import javax.jcr.Repository;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.junit.After;
import org.junit.Test;
import org.modeshape.jcr.JcrConfiguration;
import org.modeshape.jcr.JcrEngine;
import org.modeshape.jcr.api.SecurityContextCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sourcesense.stone.jcr.modeshape.server.security.CustomSecurityContext;

public abstract class AbstractPerformanceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int warmup = 10;

    private final int runtime = 50;

    private final Credentials credentials = new SecurityContextCredentials(new CustomSecurityContext());

    private final String name;

    private final JcrEngine engine;

    public AbstractPerformanceTest(String name) {
        this.name = name;

        JcrConfiguration configuration = new JcrConfiguration();
        try {
            configuration.loadFrom(this.getClass().getResource(String.format("modeshape-repository-%s.xml", this.name)));

            this.engine = configuration.build();
            this.engine.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void tearDown() {
        this.engine.shutdown();
    }

    @Test
    public void testPerformance() throws Exception {
        runTest(new ReadPropertyTest());
        runTest(new SetPropertyTest());
        runTest(new SmallFileReadTest());
        runTest(new SmallFileWriteTest());
        runTest(new BigFileReadTest());
        runTest(new BigFileWriteTest());
        runTest(new ConcurrentReadTest());
        runTest(new ConcurrentReadWriteTest());
        runTest(new SimpleSearchTest());
        runTest(new SQL2SearchTest());
        runTest(new DescendantSearchTest());
        runTest(new SQL2DescendantSearchTest());
        runTest(new TwoWayJoinTest());
        runTest(new ThreeWayJoinTest());
        runTest(new CreateManyChildNodesTest());
        runTest(new UpdateManyChildNodesTest());
        runTest(new TransientManyChildNodesTest());
        runTest(new PathBasedQueryTest());
    }

    private void runTest(AbstractTest test) {
        this.logger.info("Executing {} ...", test.getClass().getSimpleName());

        // Create the repository directory
        File dir = new File("/tmp/stone/repository", name + "-" + test);
        dir.mkdirs();

        try {
            // Create the repository
            Repository repository = this.engine.getRepository("test");
            // Run the test
            DescriptiveStatistics statistics = runTest(test, repository);
            if (statistics.getN() > 0) {
                writeReport(test.toString(), name, statistics);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println(
                    "Unable to run " + test + ": " + t.getMessage());
        } finally {
            FileUtils.deleteQuietly(dir);
        }
    }

    private DescriptiveStatistics runTest(AbstractTest test, Repository repository) throws Exception {
        DescriptiveStatistics statistics = new DescriptiveStatistics();

        test.setUp(repository, this.credentials);
        try {
            // Run a few iterations to warm up the system
            long warmupEnd = System.currentTimeMillis() + warmup * 1000;
            while (System.currentTimeMillis() < warmupEnd) {
                test.execute();
            }

            // Run test iterations, and capture the execution times
            long runtimeEnd = System.currentTimeMillis() + runtime * 1000;
            while (System.currentTimeMillis() < runtimeEnd) {
                statistics.addValue(test.execute());
            }
        } finally {
            test.tearDown();
        }

        return statistics;
    }

    private void writeReport(
            String test, String name, DescriptiveStatistics statistics)
            throws IOException {
        File parent = new File("/tmp/stone/reports");
        //if (!parent.exists()) {
            parent.mkdirs();
        //}

        File report = new File(parent, test + ".txt");

        boolean needsPrefix = !report.exists();
        PrintWriter writer = new PrintWriter(
                new FileWriterWithEncoding(report, "UTF-8", true));
        try {
            if (needsPrefix) {
                writer.format(
                        "# %-34.34s     min     10%%     50%%     90%%     max%n",
                        test);
            }

            writer.format(
                    "%-36.36s  %6.0f  %6.0f  %6.0f  %6.0f  %6.0f%n",
                    name,
                    statistics.getMin(),
                    statistics.getPercentile(10.0),
                    statistics.getPercentile(50.0),
                    statistics.getPercentile(90.0),
                    statistics.getMax());
        } finally {
            writer.close();
        }
    }

}
