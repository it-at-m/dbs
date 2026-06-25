package de.muenchen.dbs.personalization.common;

public final class P13nUtils {

    private P13nUtils() {
    }

    public static String sanitizeForLog(final String value) {
        if (value == null) {
            return null;
        }
        return value.replace('\r', ' ').replace('\n', ' ');
    }
}
