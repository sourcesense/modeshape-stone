package com.sourcesense.stone.extensions;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

public class CredentialsFactory {

    public Credentials createCredentials( String username,
                                          String password ) {
        return new SimpleCredentials(username, password.toCharArray());
    }

}
