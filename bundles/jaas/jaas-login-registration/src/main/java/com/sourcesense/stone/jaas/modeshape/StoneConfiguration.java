package com.sourcesense.stone.jaas.modeshape;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

/**
 * @version $Rev: 56022 $ $Date: 2004-10-30 00:16:18 -0500 (Sat, 30 Oct 2004) $
 */
public class StoneConfiguration extends Configuration {

    @Override
    public void refresh() {
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        return new AppConfigurationEntry[] {
                new StoneConfigurationEntry(AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, false),
                new StoneConfigurationEntry(AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT, true)
        };
    }

}