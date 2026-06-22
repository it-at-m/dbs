package de.muenchen.dbs.personalization.common;

public class Util {

    public static String sanitizeForLog(final String value) {
        if (value == null) {
            return null;
        }
        return value.replace('\r', ' ').replace('\n', ' ');
    }
}
