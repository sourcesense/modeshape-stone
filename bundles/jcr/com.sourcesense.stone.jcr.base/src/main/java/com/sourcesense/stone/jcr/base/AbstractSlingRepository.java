/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.sourcesense.stone.jcr.base;

import java.util.Dictionary;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.Workspace;

import org.apache.sling.jcr.api.NamespaceMapper;
import org.apache.sling.jcr.api.SlingRepository;
import com.sourcesense.stone.jcr.base.internal.loader.Loader;
import com.sourcesense.stone.jcr.base.util.RepositoryAccessor;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The <code>AbstractSlingRepository</code> is an abstract implementation of
 * the {@link SlingRepository} interface which provides default support for
 * attached repositories as well as ensuring live repository connection,
 * reconnecting if needed. Implementations of the <code>SlingRepository</code>
 * interface may wish to extend this class to benefit from a default
 * implementation.
 *
 * @scr.component metatype="no"
 */
public abstract class AbstractSlingRepository implements SlingRepository,
        Runnable {

    /** @scr.property value="" */
    public static final String PROPERTY_DEFAULT_WORKSPACE = "defaultWorkspace";

    /** @scr.property valueRef="DEFAULT_ANONYMOUS_USER" */
    public static final String PROPERTY_ANONYMOUS_USER = "anonymous.name";

    /** @scr.property valueRef="DEFAULT_ANONYMOUS_PASS" */
    public static final String PROPERTY_ANONYMOUS_PASS = "anonymous.password";

    /** @scr.property valueRef="DEFAULT_ADMIN_USER" */
    public static final String PROPERTY_ADMIN_USER = "admin.name";

    /** @scr.property valueRef="DEFAULT_ADMIN_PASS" */
    public static final String PROPERTY_ADMIN_PASS = "admin.password";

    /** @scr.property valueRef="DEFAULT_POLL_ACTIVE" */
    public static final String PROPERTY_POLL_ACTIVE = "poll.active";

    /** @scr.property valueRef="DEFAULT_POLL_INACTIVE" */
    public static final String PROPERTY_POLL_INACTIVE = "poll.inactive";

    public static final String DEFAULT_ANONYMOUS_USER = "anonymous";

    public static final String DEFAULT_ANONYMOUS_PASS = "anonymous";

    public static final String DEFAULT_ADMIN_USER = "admin";

    public static final String DEFAULT_ADMIN_PASS = "admin";

    /**
     * The default value for the number of seconds to wait between two
     * consecutive checks while the repository is active (value is 10).
     */
    public static final int DEFAULT_POLL_ACTIVE = 10;

    /**
     * The default value for the number of seconds to wait between two
     * consecutive checks while the repository is not active (value is 10).
     */
    public static final int DEFAULT_POLL_INACTIVE = 10;

    /** The minimum number of seconds allowed for any of the two poll times */
    public static final int MIN_POLL = 2;

    /** @scr.reference bind="bindLog" unbind="unbindLog" */
    private LogService log;

    private ComponentContext componentContext;

    private Repository repository;

    private ServiceRegistration repositoryService;

    private String defaultWorkspace;

    private String anonUser;

    private char[] anonPass;

    private String adminUser;

    private char[] adminPass;

    /** Namespace handler. */
    private Loader namespaceHandler;

    /** Session proxy handler. */
    private SessionProxyHandler sessionProxyHandler;

    // the poll interval used while the repository is not active
    private long pollTimeInActiveSeconds;

    // the poll interval used while the repository is active
    private long pollTimeActiveSeconds;

    // whether the repository checker task should be active. this field
    // is managed by the startRepositoryPinger and stopRepositoryPinger methods
    private boolean running;

    // the background thread constantly checking the repository
    private Thread repositoryPinger;

    private ServiceTracker namespaceMapperTracker;

    protected AbstractSlingRepository() {
    }

    /**
     * Returns the default workspace, which may be <code>null</code> meaning
     * to use the repository provided default workspace. Declared final to make
     * sure the SLING-256 rule is enforced.
     */
    public final String getDefaultWorkspace() {
        return defaultWorkspace;
    }

    private void setDefaultWorkspace(String defaultWorkspace) {
        // normalize the default workspace name: trim leading and trailing
        // blanks and set to null in case the trimmed name is empty
        if (defaultWorkspace != null) {
            defaultWorkspace = defaultWorkspace.trim();
            if (defaultWorkspace.length() == 0) {
                defaultWorkspace = null;
            }
        }

        log(LogService.LOG_DEBUG,
            "setDefaultWorkspace: Setting the default workspace to "
                + defaultWorkspace);
        this.defaultWorkspace = defaultWorkspace;
    }

    /**
     * Logs in as an anonymous user. This implementation simply returns the
     * result of calling {@link #login(Credentials, String)}
     */
    public Session login() throws LoginException, RepositoryException {
        return this.login(null, null);
    }

    public Session loginAdministrative(String workspace)
            throws RepositoryException {
        Credentials sc = getAdministrativeCredentials(this.adminUser);
        return this.login(sc, workspace);
    }

    public Session login(Credentials credentials) throws LoginException,
            RepositoryException {
        return this.login(credentials, null);
    }

    public Session login(String workspace) throws LoginException,
            NoSuchWorkspaceException, RepositoryException {
        return this.login(null, workspace);
    }

    public Session login(Credentials credentials, String workspace)
            throws LoginException, NoSuchWorkspaceException,
            RepositoryException {

        // if already stopped, don't retrieve a session
        if (this.componentContext == null || this.getRepository() == null) {
            throw new RepositoryException("Sling Repository not ready");
        }

        if (credentials == null) {
            credentials = getAnonCredentials(this.anonUser);
        }

        // check the workspace
        if (workspace == null) {
            workspace = this.getDefaultWorkspace();
        }

        try {
            log(LogService.LOG_DEBUG, "login: Logging in to workspace '"
                + workspace + "'");
            Session session = getRepository().login(credentials, workspace);

            // if the defualt workspace is null, acquire a session from the pool
            // and use the workspace used as the new default workspace
            if (workspace == null) {
                String defaultWorkspace = session.getWorkspace().getName();
                log(LogService.LOG_DEBUG, "login: Using " + defaultWorkspace
                    + " as the default workspace instead of 'null'");
                setDefaultWorkspace(defaultWorkspace);
            }

            defineNamespacePrefixes(session);

            // to support namespace prefixes if session.impersonate is called
            // we have to use a proxy
            if ( this.sessionProxyHandler != null ) {
                return this.sessionProxyHandler.createProxy(session);
            }
            return session;

        } catch (NoSuchWorkspaceException nswe) {
            // if the desired workspace is the default workspace, try to create
            // (but not if using the repository-supplied default workspace)
            if (workspace != null
                && workspace.equals(this.getDefaultWorkspace())
                && this.createWorkspace(workspace)) {
                return this.getRepository().login(credentials, workspace);
            }

            // otherwise (any workspace) or if workspace creation fails
            // just forward the original exception
            throw nswe;

        } catch (RuntimeException re) {
            // SLING-702: Jackrabbit throws IllegalStateException if the
            // repository has already been shut down ...
            throw new RepositoryException(re.getMessage(), re);
        }
    }

    /**
     * @param anonUser the user name of the anon user.
     * @return a Credentials implementation that represents the anon user.
     */
    protected Credentials getAnonCredentials(String anonUser) {
        // NB: this method is overridden in the Jackrabbit Service bundle to avoid using the anon password. SLING-1282
        return new SimpleCredentials(anonUser, anonPass);
    }

    /**
     * @param adminUser the name of the administrative user.
     * @return a Credentials implementation that represents the administrative user.
     */
    protected Credentials getAdministrativeCredentials(String adminUser){
        // NB: this method is overridden in the Jackrabbit Service bundle to avoid using the admin password. SLING-1282
        return new SimpleCredentials(adminUser, adminPass);
    }



    /*
     * (non-Javadoc)
     *
     * @see javax.jcr.Repository#getDescriptor(java.lang.String)
     */
    public String getDescriptor(String name) {
        Repository repo = getRepository();
        if (repo != null) {
            return repo.getDescriptor(name);
        }

        log(LogService.LOG_ERROR, "getDescriptor: Repository not available");
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.jcr.Repository#getDescriptorKeys()
     */
    public String[] getDescriptorKeys() {
        Repository repo = getRepository();
        if (repo != null) {
            return repo.getDescriptorKeys();
        }

        log(LogService.LOG_ERROR, "getDescriptorKeys: Repository not available");
        return new String[0];
    }

    /**
     * {@inheritDoc}
     */
    public Value getDescriptorValue(String key) {
        Repository repo = getRepository();
        if (repo != null) {
            return repo.getDescriptorValue(key);
        }

        log(LogService.LOG_ERROR, "getDescriptorValue: Repository not available");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Value[] getDescriptorValues(String key) {
        Repository repo = getRepository();
        if (repo != null) {
            return repo.getDescriptorValues(key);
        }

        log(LogService.LOG_ERROR, "getDescriptorValues: Repository not available");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleValueDescriptor(String key) {
        Repository repo = getRepository();
        if (repo != null) {
            return repo.isSingleValueDescriptor(key);
        }

        log(LogService.LOG_ERROR, "isSingleValueDescriptor: Repository not available");
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isStandardDescriptor(String key) {
        Repository repo = getRepository();
        if (repo != null) {
            return repo.isStandardDescriptor(key);
        }

        log(LogService.LOG_ERROR, "isStandardDescriptor: Repository not available");
        return false;
    }

    // ---------- logging ------------------------------------------------------

    protected void log(int level, String message) {
        this.log(level, message, null);
    }

    protected void log(int level, String message, Throwable t) {
        LogService log = this.log;
        if (log != null) {
            if (componentContext != null) {
                log.log(componentContext.getServiceReference(), level, message,
                    t);
            } else {
                log.log(level, message, t);
            }
        }
    }

    // ---------- Repository Access -------------------------------------------

    /**
     * Returns a new instance of the {@link RepositoryAccessor} class to access
     * a repository over RMI or through JNDI.
     * <p>
     * Extensions of this method may return an extension of the
     * {@link RepositoryAccessor} class if the provide extended functionality.
     */
    protected RepositoryAccessor getRepositoryAccessor() {
        return new RepositoryAccessor();
    }

    /**
     * Acquires the repository by calling the
     * {@link com.sourcesense.stone.jcr.base.util.RepositoryAccessor#getRepositoryFromURL(String)}
     * with the value of the
     * {@link com.sourcesense.stone.jcr.base.util.RepositoryAccessor#REPOSITORY_URL_OVERRIDE_PROPERTY}
     * framework or configuration property. If the property exists and a
     * repository can be accessed using this property, that repository is
     * returned. Otherwise <code>null</code> is returned.
     * <p>
     * Extensions of this class may overwrite this method with implementation
     * specific acquisition semantics and may call this base class method or not
     * as the implementation sees fit.
     * <p>
     * This method does not throw any <code>Throwable</code> but instead just
     * returns <code>null</code> if not repository is available. Any problems
     * trying to acquire the repository must be caught and logged as
     * appropriate.
     *
     * @return The acquired JCR <code>Repository</code> or <code>null</code>
     *         if not repository can be acquired.
     */
    protected Repository acquireRepository() {
        // if the environment provides a repository override URL, other settings
        // are ignored
        String overrideUrl = (String) componentContext.getProperties().get(
            RepositoryAccessor.REPOSITORY_URL_OVERRIDE_PROPERTY);
        if (overrideUrl == null) {
            overrideUrl = componentContext.getBundleContext().getProperty(
                RepositoryAccessor.REPOSITORY_URL_OVERRIDE_PROPERTY);
        }

        if (overrideUrl != null && overrideUrl.length() > 0) {
            log(LogService.LOG_INFO,
                "acquireRepository: Will not use embedded repository due to property "
                    + RepositoryAccessor.REPOSITORY_URL_OVERRIDE_PROPERTY + "="
                    + overrideUrl + ", acquiring repository using that URL");
            return getRepositoryAccessor().getRepositoryFromURL(overrideUrl);
        }

        log(LogService.LOG_DEBUG,
            "acquireRepository: No existing repository to access");
        return null;
    }

    /**
     * This method is called after a repository has been acquired by
     * {@link #acquireRepository()} but before the repository is registered as a
     * service.
     * <p>
     * Implementations may overwrite this method but MUST call this base class
     * implementation first.
     *
     * @param repository The JCR <code>Repository</code> to setup.
     */
    protected void setupRepository(Repository repository) {
        BundleContext bundleContext = componentContext.getBundleContext();
        this.namespaceHandler = new Loader(this, bundleContext);
        this.sessionProxyHandler = new SessionProxyHandler(this);
    }

    /**
     * Registers this component as an OSGi service with type
     * <code>javax.jcr.Repository</code> and
     * <code>org.apache.sling.jcr.api.SlingRepository</code> using the
     * component properties as service registration properties.
     * <p>
     * This method may be overwritten to register the component with different
     * types.
     *
     * @return The OSGi <code>ServiceRegistration</code> object representing
     *         the registered service.
     */
    protected ServiceRegistration registerService() {
        @SuppressWarnings("unchecked")
        Dictionary<String, Object> props = componentContext.getProperties();
        String[] interfaces = new String[] { SlingRepository.class.getName(),
            Repository.class.getName() };

        return componentContext.getBundleContext().registerService(interfaces,
            this, props);
    }

    /**
     * Returns the repository underlying this instance or <code>null</code> if
     * no repository is currently being available.
     */
    protected Repository getRepository() {
        return repository;
    }

    /**
     * Checks that the given <code>repository</code> is still available. This
     * implementation tries to get the <code>Repository.SPEC_NAME_DESC</code>
     * descriptor from the repository and returns <code>true</code> if the
     * returned value is not <code>null</code>.
     * <p>
     * Extensions of this class may overwrite this method to implement different
     * access checks. The contract of this method must be obeyed, though in a
     * sense, the <code>true</code> must only be returned if
     * <code>repository</code> is actually usable.
     *
     * @param repository The JCR <code>Repository</code> to check for
     *            availability.
     * @return <code>true</code> if <code>repository</code> is not
     *         <code>null</code> and accessible.
     */
    protected boolean pingRepository(Repository repository) {
        if (repository != null) {
            try {
                return repository.getDescriptor(Repository.SPEC_NAME_DESC) != null;
            } catch (Throwable t) {
                log(LogService.LOG_DEBUG, "pingRepository: Repository "
                    + repository + " does not seem to be available any more", t);
            }
        }

        // fall back to unavailable
        return false;
    }

    /** Ping our current repository and check that admin login (required by Sling) works. */
    protected boolean pingAndCheck() {
        if(repository == null) {
            throw new IllegalStateException("Repository is null");
        }

        boolean result = false;

        if(pingRepository(repository)) {
            try {
                final Session s = loginAdministrative(getDefaultWorkspace());
                s.logout();
                result = true;
            } catch(RepositoryException re) {
                log.log(LogService.LOG_INFO, "pingAndCheck; loginAdministrative failed", re);
            }
        }

        return result;
    }

    /**
     * Unregisters the service represented by the
     * <code>serviceRegistration</code>.
     * <p>
     * This method may be overwritten by extensions of this class as long as it
     * is made sure, the given service registration is unregistered.
     */
    protected void unregisterService(ServiceRegistration serviceRegistration) {
        serviceRegistration.unregister();
    }

    /**
     * Performs any cleanups before the repository is actually disposed off by
     * the {@link #disposeRepository(Repository)} method.
     * <p>
     * This method is meant for cleanup tasks before the repository is actually
     * disposed off. Extensions of this class may overwrite but must call this
     * base class implementation.
     *
     * @param repository
     */
    protected void tearDown(Repository repository) {
        if (this.namespaceHandler != null) {
            this.namespaceHandler.dispose();
            this.namespaceHandler = null;
        }
        this.sessionProxyHandler = null;
    }

    /**
     * Disposes off the given <code>repository</code>. This base class
     * implementation does nothing. Extensions should overwrite if any special
     * disposal operation is required.
     *
     * @param repository
     */
    protected void disposeRepository(Repository repository) {
        // nothing to do here ...
    }

    // --------- SCR integration -----------------------------------------------

    protected ComponentContext getComponentContext() {
        return this.componentContext;
    }

    /**
     * This method must be called if overwritten by implementations !!
     *
     * @throws nothing, but allow derived classes to throw any Exception
     */
    protected void activate(ComponentContext componentContext) throws Exception {
        this.namespaceMapperTracker = new ServiceTracker(componentContext.getBundleContext(), NamespaceMapper.class.getName(), null);
        this.namespaceMapperTracker.open();

        this.componentContext = componentContext;

        @SuppressWarnings("unchecked")
        Dictionary<String, Object> properties = componentContext.getProperties();

        setDefaultWorkspace(this.getProperty(properties,
            PROPERTY_DEFAULT_WORKSPACE, null));
        this.anonUser = this.getProperty(properties, PROPERTY_ANONYMOUS_USER,
            DEFAULT_ANONYMOUS_USER);
        this.anonPass = this.getProperty(properties, PROPERTY_ANONYMOUS_PASS,
            DEFAULT_ANONYMOUS_PASS).toCharArray();

        this.adminUser = this.getProperty(properties, PROPERTY_ADMIN_USER,
            DEFAULT_ADMIN_USER);
        this.adminPass = this.getProperty(properties, PROPERTY_ADMIN_PASS,
            DEFAULT_ADMIN_PASS).toCharArray();

        setPollTimeActive(getIntProperty(properties, PROPERTY_POLL_ACTIVE));
        setPollTimeInActive(getIntProperty(properties, PROPERTY_POLL_INACTIVE));

        // immediately try to start the repository while activating
        // this component instance
        try {
            if (startRepository()) {
                log(LogService.LOG_INFO, "Repository started successfully");
            } else {
                log(LogService.LOG_WARNING,
                    "Repository startup failed, will try later");
            }
        } catch (Throwable t) {
            log(LogService.LOG_WARNING,
                "activate: Unexpected problem starting repository", t);
        }

        // launch the background repository checker now
        startRepositoryPinger();
    }

    /**
     * This method must be called if overwritten by implementations !!
     *
     * @param componentContext
     */
    protected void deactivate(ComponentContext componentContext) {
        this.namespaceMapperTracker.close();

        // stop the background thread
        stopRepositoryPinger();

        // ensure the repository is really disposed off
        if (repository != null || repositoryService != null) {
            log(LogService.LOG_INFO,
                "deactivate: Repository still running, forcing shutdown");

            try {
                stopRepository();
            } catch (Throwable t) {
                log(LogService.LOG_WARNING,
                    "deactivate: Unexpected problem stopping repository", t);
            }
        }

        this.componentContext = null;
    }

    protected void bindLog(LogService log) {
        this.log = log;
    }

    protected void unbindLog(LogService log) {
        if (this.log == log) {
            this.log = null;
        }
    }

    // ---------- internal -----------------------------------------------------

    private String getProperty(Dictionary<String, Object> properties,
            String name, String defaultValue) {
        Object prop = properties.get(name);
        return (prop instanceof String) ? (String) prop : defaultValue;
    }

    private int getIntProperty(Dictionary<String, Object> properties,
            String name) {
        Object prop = properties.get(name);
        if (prop instanceof Number) {
            return ((Number) prop).intValue();
        } else if (prop != null) {
            try {
                return Integer.decode(String.valueOf(prop)).intValue();
            } catch (NumberFormatException nfe) {
                // don't really care
            }
        }

        return -1;
    }

    private boolean createWorkspace(String workspace) {
        this.log(LogService.LOG_INFO, "createWorkspace: Requested workspace "
            + workspace + " does not exist, trying to create");

        Session tmpSession = null;
        try {
            Credentials sc = getAdministrativeCredentials(this.adminUser);
            tmpSession = this.getRepository().login(sc);
            Workspace defaultWs = tmpSession.getWorkspace();
            defaultWs.createWorkspace(workspace);
            return true;
        } catch (Throwable t) {
            this.log(LogService.LOG_ERROR,
                "createWorkspace: Cannot create requested workspace "
                    + workspace, t);
        } finally {
            if (tmpSession != null) {
                tmpSession.logout();
            }
        }

        // fall back to failure
        return false;
    }

    void defineNamespacePrefixes(final Session session) throws RepositoryException {
        if (this.namespaceHandler != null) {
            // apply namespace mapping
            this.namespaceHandler.defineNamespacePrefixes(session);
        }

        // call namespace mappers
        Object[] nsMappers = namespaceMapperTracker.getServices();
        if (nsMappers != null) {
            for (int i = 0; i < nsMappers.length; i++) {
                ((NamespaceMapper) nsMappers[i]).defineNamespacePrefixes(session);
            }
        }
    }

    // ---------- Background operation checking repository availability --------

    private void setPollTimeActive(int seconds) {
        if (seconds < MIN_POLL) {
            seconds = DEFAULT_POLL_ACTIVE;
        }
        pollTimeActiveSeconds = seconds;
    }

    private void setPollTimeInActive(int seconds) {
        if (seconds < MIN_POLL) {
            seconds = DEFAULT_POLL_INACTIVE;
        }
        pollTimeInActiveSeconds = seconds;
    }

    private void startRepositoryPinger() {
        if (repositoryPinger == null) {
            // make sure the ping will be running
            running = true;

            // create and start the thread
            repositoryPinger = new Thread(this, "Repository Pinger");
            repositoryPinger.start();
        }
    }

    private void stopRepositoryPinger() {

        // make sure the thread is terminating
        running = false;

        // nothing to do if the thread is not running at all
        Thread rpThread = repositoryPinger;
        if (rpThread == null) {
            return;
        }

        // clear the repositoryPinger thread field
        repositoryPinger = null;

        // notify the thread for it to be able to shut down
        synchronized (rpThread) {
            rpThread.notifyAll();
        }

        // wait at most 10 seconds for the thread to terminate
        try {
            rpThread.join(10000L);
        } catch (InterruptedException ie) {
            // don't care here
        }

        // consider it an error if the thread is still running !!
        if (rpThread.isAlive()) {
            log(LogService.LOG_ERROR,
                "stopRepositoryPinger: Timed waiting for thread " + rpThread
                    + " to terminate");
        }

    }

    private boolean startRepository() {
        try {
            log(LogService.LOG_DEBUG,
                "startRepository: calling acquireRepository()");
            Repository newRepo = acquireRepository();
            if (newRepo != null) {

                // ensure we really have the repository
                log(LogService.LOG_DEBUG,
                    "startRepository: got a Repository, calling pingRepository()");
                if (pingRepository(newRepo)) {
                    repository = newRepo;

                    if(pingAndCheck()) {
                        log(LogService.LOG_DEBUG,
                            "startRepository: pingRepository() and pingAndCheck() successful, calling setupRepository()");
                        setupRepository(newRepo);

                        log(LogService.LOG_DEBUG,
                            "startRepository: calling registerService()");
                        repositoryService = registerService();

                        log(LogService.LOG_DEBUG,
                            "registerService() successful, registration="
                                + repositoryService);

                        return true;
                    }

                    // ping succeeded but pingAndCheck fail, we have to drop
                    // the repository in this situation and restart from
                    // scratch later
                    log(
                        LogService.LOG_DEBUG,
                        "pingRepository() successful but pingAndCheck() fails, calling disposeRepository()");

                    // drop reference
                    repository = null;

                } else {

                    // otherwise let go of the repository and fail startup
                    log(LogService.LOG_DEBUG,
                        "startRepository: pingRepository() failed, calling disposeRepository()");

                }

                // ping or pingAndCheck failed: dispose off repository
                disposeRepository(newRepo);
            }
        } catch (Throwable t) {
            // consider an uncaught problem an error
            log(
                LogService.LOG_ERROR,
                "startRepository: Uncaught Throwable trying to access Repository, calling stopRepository()",
                t);

            // repository might be partially started, stop anything left
            stopRepository();
        }

        return false;
    }

    private void stopRepository() {
        if (repositoryService != null) {
            try {
                log(LogService.LOG_DEBUG,
                    "Unregistering SlingRepository service, registration="
                        + repositoryService);
                unregisterService(repositoryService);
            } catch (Throwable t) {
                log(
                    LogService.LOG_INFO,
                    "stopRepository: Uncaught problem unregistering the repository service",
                    t);
            }
            repositoryService = null;
        }

        if (repository != null) {
            Repository oldRepo = repository;
            repository = null;

            try {
                tearDown(oldRepo);
            } catch (Throwable t) {
                log(
                    LogService.LOG_INFO,
                    "stopRepository: Uncaught problem tearing down the repository",
                    t);
            }

            try {
                disposeRepository(oldRepo);
            } catch (Throwable t) {
                log(
                    LogService.LOG_INFO,
                    "stopRepository: Uncaught problem disposing the repository",
                    t);
            }
        }
    }

    public void run() {
        // start polling with a small value to be faster at system startup
        // we'll increase the polling time after each try
        long pollTimeMsec = 100L;
        final long MSEC = 1000L;
        final int pollTimeFactor = 2;
        Object waitLock = repositoryPinger;

        try {

            while (running) {

                // wait first before starting to check
                synchronized (waitLock) {
                    try {
                        // no debug logging, see SLING-505
                        // log(LogService.LOG_DEBUG, "Waiting " + pollTime + " seconds before checking repository");
                        waitLock.wait(pollTimeMsec);
                    } catch (InterruptedException ie) {
                        // don't care, go ahead
                    }
                }

                long newPollTime = pollTimeMsec;
                if (running) {

                    Repository repo = repository;
                    boolean ok = false;
                    if (repo == null) {
                        // No Repository yet, try to start
                        if (startRepository()) {
                            log(LogService.LOG_INFO, "Repository started successfully");
                            ok = true;
                            newPollTime = pollTimeActiveSeconds * MSEC;
                        } else {
                            // ramp up poll time, up to the max of our configured times
                            newPollTime = Math.min(pollTimeMsec * pollTimeFactor, Math.max(pollTimeInActiveSeconds, pollTimeActiveSeconds) * MSEC);
                        }

                    } else if (pingAndCheck()) {
                        ok = true;
                        newPollTime = pollTimeActiveSeconds * MSEC;

                    } else {
                        // Repository disappeared
                        log(LogService.LOG_INFO,
                            "run: Repository not accessible anymore, unregistering service");
                        stopRepository();
                        newPollTime = pollTimeInActiveSeconds * MSEC;
                    }

                    if(newPollTime != pollTimeMsec) {
                        pollTimeMsec = newPollTime;
                        log(LogService.LOG_DEBUG,
                                "Repository Pinger interval set to " + pollTimeMsec + " msec, repository is "
                                + (ok ? "available" : "NOT available")
                                );
                    }
                }
            }

            // thread is terminating due to "running" being set to false
            log(LogService.LOG_INFO, "Repository Pinger stopping on request");

        } catch (Throwable t) {
            // try to log the cause for thread termination
            log(LogService.LOG_ERROR, "Repository Pinger caught unexpected issue", t);

        } finally {

            // whatever goes on, make sure the repository is disposed of
            // at the end of the thread....
            log(LogService.LOG_INFO, "Stopping repository on shutdown");
            stopRepository();
        }
    }

}
