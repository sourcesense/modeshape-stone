package com.sourcesense.stone.jcr.modeshape.server.performances;

import java.util.LinkedList;
import java.util.List;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Abstract base class for individual performance benchmarks.
 */
abstract class AbstractTest {

    private Repository repository;

    private Credentials credentials;

    private List<Session> sessions;

    private List<Thread> threads;

    private volatile boolean running;

    protected static int getScale(int def) {
        int scale = Integer.getInteger("scale", 0);
        if (scale == 0) {
            scale = def;
        }
        return scale;
    }

    /**
     * Prepares this performance benchmark.
     *
     * @param repository the repository to use
     * @param credentials credentials of a user with write access
     * @throws Exception if the benchmark can not be prepared
     */
    public void setUp(Repository repository, Credentials credentials)
            throws Exception {
        this.repository = repository;
        this.credentials = credentials;
        this.sessions = new LinkedList<Session>();
        this.threads = new LinkedList<Thread>();

        this.running = true;

        beforeSuite();
    }

    /**
     * Executes a single iteration of this test.
     *
     * @return number of milliseconds spent in this iteration
     * @throws Exception if an error occurs
     */
    public long execute() throws Exception {
        beforeTest();
        try {
            long start = System.currentTimeMillis();
            runTest();
            return System.currentTimeMillis() - start;
        } finally {
            afterTest();
        }
    }
    /**
     * Cleans up after this performance benchmark.
     *
     * @throws Exception if the benchmark can not be cleaned up
     */
    public void tearDown() throws Exception {
        this.running = false;
        for (Thread thread : threads) {
            thread.join();
        }

        afterSuite();

        for (Session session : sessions) {
            if (session.isLive()) {
                session.logout();
            }
        }

        this.threads = null;
        this.sessions = null;
        this.credentials = null;
        this.repository = null;
    }

    /**
     * Run before any iterations of this test get executed. Subclasses can
     * override this method to set up static test content.
     *
     * @throws Exception if an error occurs
     */
    protected void beforeSuite() throws Exception {
    }

    protected void beforeTest() throws Exception {
    }

    protected abstract void runTest() throws Exception;

    protected void afterTest() throws Exception {
    }

    /**
     * Run after all iterations of this test have been executed. Subclasses can
     * override this method to clean up static test content.
     *
     * @throws Exception if an error occurs
     */
    protected void afterSuite() throws Exception {
    }

    protected Repository getRepository() {
        return repository;
    }

    protected Credentials getCredentials() {
        return credentials;
    }

    /**
     * Returns a new reader session that will be automatically closed once
     * all the iterations of this test have been executed.
     *
     * @return reader session
     */
    protected Session loginReader() {
        try {
            Session session = repository.login();
            sessions.add(session);
            return session;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a new writer session that will be automatically closed once
     * all the iterations of this test have been executed.
     *
     * @return writer session
     */
    protected Session loginWriter() {
        try {
            Session session = repository.login(credentials);
            sessions.add(session);
            return session;
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a background thread that repeatedly executes the given job
     * until all the iterations of this test have been executed.
     *
     * @param job background job
     */
    protected void addBackgroundJob(final Runnable job) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (running) {
                    job.run();
                }
            }
        };
        thread.start();
        threads.add(thread);
    }

    public String toString() {
        String name = getClass().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

}
