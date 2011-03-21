package com.sourcesense.stone.extensions;

import java.util.Locale;
import java.util.Set;
import org.modeshape.common.i18n.I18n;

public class JackrabbitConnectorI18n {

    public static I18n propertyIsRequired;

    static {
        try {
            I18n.initialize(JackrabbitConnectorI18n.class);
        } catch (final Exception err) {
            System.err.println(err);
        }
    }

    public static Set<Locale> getLocalizationProblemLocales() {
        return I18n.getLocalizationProblemLocales(JackrabbitConnectorI18n.class);
    }

    public static Set<String> getLocalizationProblems() {
        return I18n.getLocalizationProblems(JackrabbitConnectorI18n.class);
    }

    public static Set<String> getLocalizationProblems( Locale locale ) {
        return I18n.getLocalizationProblems(JackrabbitConnectorI18n.class, locale);
    }
}
