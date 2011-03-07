package com.sourcesense.stone.jcr.modeshape.server.performances;

import static com.sourcesense.stone.jcr.modeshape.server.IntegrationTestUtil.getSlingRepositoryFromServiceList;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Credentials;
import javax.jcr.Repository;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.junit.Ignore;
import org.junit.Test;
import org.modeshape.jcr.api.SecurityContextCredentials;
import org.ops4j.pax.exam.Inject;
import org.osgi.framework.BundleContext;

import com.sourcesense.stone.jcr.modeshape.server.security.CustomSecurityContext;

abstract class AbstractPerformanceTest {

    private final String name;

    private final int warmup = 10;

    private final int runtime = 50;

    private final Credentials credentials = new SecurityContextCredentials(new CustomSecurityContext());

    public AbstractPerformanceTest(String name) {
        this.name = name;
    }

    @Inject
    private BundleContext bundleContext;

    @Test
    @Ignore
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
        // Create the repository directory
        File dir = new File(new File("target", "repository"), name + "-" + test);
        dir.mkdirs();

        try {
            // Create the repository
            Repository repository = getSlingRepositoryFromServiceList(bundleContext);
            // Run the test
            DescriptiveStatistics statistics = runTest(test, repository);
            if (statistics.getN() > 0) {
                writeReport(test.toString(), name, statistics);
            }
        } catch (Throwable t) {
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
        File report = new File("target", test + ".txt");

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
