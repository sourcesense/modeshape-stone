package com.sourcesense.stone.extensions.webconsolesecurityprovider.internal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.webconsole.WebConsoleSecurityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component( specVersion = "1.1", metatype = true )
@Service( WebConsoleSecurityProvider.class )
public class SlingWebConsoleSecurityProvider implements WebConsoleSecurityProvider {

    private static final String PROPERTY_FOR_AUTHORIZED_USERS = "users";

    private static final String PROPERTY_DEFAULT_AUTHORIZED_USER = "admin";

    private static final String PROPERTY_AUTHORIZED_GROUPS = "groups";

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Reference
    private Repository repository;

    @Property( name = PROPERTY_FOR_AUTHORIZED_USERS, cardinality = 20, value = PROPERTY_DEFAULT_AUTHORIZED_USER )
    private Set<String> users;

    @Property( name = PROPERTY_AUTHORIZED_GROUPS, cardinality = 20 )
    private Set<String> groups;

    @SuppressWarnings( "unused" )
    @Activate
    @Modified
    private void configure( Map<String, Object> config ) {
        this.users = toSet(config.get(PROPERTY_FOR_AUTHORIZED_USERS));
        this.groups = toSet(config.get(PROPERTY_AUTHORIZED_GROUPS));
    }

    @Override
    public Object authenticate( String userName,
                                String password ) {
        final Credentials creds = new SimpleCredentials(userName,
                (password == null) ? new char[0] : password.toCharArray());
            Session session = null;
            ClassLoader ccl = Thread.currentThread().getContextClassLoader(); 
            try {
            	Thread.currentThread().setContextClassLoader(getClass().getClassLoader()); 
                session = repository.login(creds);
                return true;
            } catch (LoginException re) {
                log.info(
                    "authenticate: User "
                        + userName
                        + " failed to authenticate with the repository for Web Console access",
                    re);
            } catch (RepositoryException re) {
                log.info("authenticate: Generic problem trying grant User "
                    + userName + " access to the Web Console", re);
            } finally {
            	Thread.currentThread().setContextClassLoader(ccl); 
                if (session != null) {
                    session.logout();
                }
            }

            // no success (see log)
            return null;
    }

    @Override
    public boolean authorize( Object user,
                              String role ) {
        log.info("********** Attempting to authorize " + user + ", with role " + role);
        return true;
    }

    private Set<String> toSet( final Object configObj ) {
        final HashSet<String> groups = new HashSet<String>();
        if (configObj instanceof String) {
            groups.add((String)configObj);
        } else if (configObj instanceof Collection<?>) {
            for (Object obj : ((Collection<?>)configObj)) {
                if (obj instanceof String) {
                    groups.add((String)obj);
                }
            }
        } else if (configObj instanceof String[]) {
            for (String string : ((String[])configObj)) {
                if (string != null) {
                    groups.add(string);
                }
            }
        }
        return groups;
    }
}
