package com.sourcesense.stone.jaas.modeshape;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;

public class StoneConfigurationEntry extends AppConfigurationEntry {

    private static final Map<String, Object> IGNORE = new HashMap<String, Object>();

    private static final Map<String, Object> EMPTY = Collections.emptyMap();

    static {
        IGNORE.put("ignore", "true");
    }

    public StoneConfigurationEntry(LoginModuleControlFlag controlFlag,
                                     boolean ignore) {
        super(StoneLoginModule.class.getName(), controlFlag, ignore ? IGNORE : EMPTY);
    }
}
