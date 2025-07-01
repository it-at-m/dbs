package de.muenchen.dbs.personalization.security;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Each possible authority in this project is represented by a constant in this class.
 * The constants are used within the {@link org.springframework.stereotype.Controller} or
 * {@link org.springframework.stereotype.Service} classes in the method security annotations
 * (e.g. {@link PreAuthorize}).
 */
@SuppressWarnings("PMD.DataClass")
public final class Authorities {
    public static final String CHECKLIST_GET = "hasAnyRole('reader', 'writer')";
    public static final String CHECKLIST_GET_ALL = "hasAnyRole('reader', 'writer')";
    public static final String CHECKLIST_CREATE = "hasAnyRole('writer')";
    public static final String CHECKLIST_UPDATE = "hasAnyRole('writer')";
    public static final String CHECKLIST_DELETE = "hasAnyRole('writer')";

    public static final String THEENTITY_GET = "hasAnyRole('reader', 'writer')";
    public static final String THEENTITY_GET_ALL = "hasAnyRole('reader', 'writer')";
    public static final String THEENTITY_CREATE = "hasAnyRole('writer')";
    public static final String THEENTITY_UPDATE = "hasAnyRole('writer')";
    public static final String THEENTITY_DELETE = "hasAnyRole('writer')";

    // Permissions based auth
    // public static final String CHECKLIST_GET = "hasAuthority('DBS_CHECKLIST_READ')";
    // public static final String CHECKLIST_GET_ALL = "hasAuthority('DBS_CHECKLIST_READ')";
    // public static final String CHECKLIST_CREATE = "hasAuthority('DBS_CHECKLIST_WRITE')";
    // public static final String CHECKLIST_UPDATE = "hasAuthority('DBS_CHECKLIST_WRITE')";
    // public static final String CHECKLIST_DELETE = "hasAuthority('DBS_CHECKLIST_DELETE')";

    private Authorities() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
