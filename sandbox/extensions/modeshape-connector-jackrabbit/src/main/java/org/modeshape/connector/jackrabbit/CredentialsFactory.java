package org.modeshape.connector.jackrabbit;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CredentialsFactory {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public Credentials createCredentials( String username,
                                          String password ) {
        logger.info("Creating credentials with username: " + username + ", password: " + password);
        return new SimpleCredentials(username, password.toCharArray());
    }

}
