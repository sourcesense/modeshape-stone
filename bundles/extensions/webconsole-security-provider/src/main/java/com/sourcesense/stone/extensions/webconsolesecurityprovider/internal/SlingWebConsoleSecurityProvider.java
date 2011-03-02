package com.sourcesense.stone.extensions.webconsolesecurityprovider.internal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.jcr.Repository;
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
    public Object authenticate( String user,
                                String password ) {
        log.info("********** Attempting to authenticate " + user);
        
        return "adminadmin".equals(user + password) ? true : null;
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
