package com.sourcesense.stone.jcr.modeshape.server;

import java.util.Arrays;
import java.util.List;
import org.osgi.framework.Bundle;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class IntegrationTestUtil {

    static Bundle getBundle( String bundleSymbolicName,
                             Bundle[] bundles ) {
        for (Bundle bundle : bundles) {
            if (bundleSymbolicName.equals(bundle.getSymbolicName())) {
                return bundle;
            }
        }

        return null;
    }

    static List<String> symbolicNamesListFor( Bundle[] bundles ) {
        return Lists.transform(Arrays.asList(bundles), new Function<Bundle, String>() {

            @Override
            public String apply( Bundle bundle ) {
                return bundle.getSymbolicName();
            }
        });
    }
}
