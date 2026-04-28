package com.digitalid.domain.model;

public enum ToolCategory {

    CORE("Core"),
    IDENTITY_MANAGEMENT("Identity Management"),
    CERTIFICATION_MANAGEMENT("Certification Management"),
    ENHANCED_VERIFICATION("Enhanced Verification"),
    REPORTING("Reporting & Analytics"),
    SEARCH("Search & Query"),
    BATCH("Batch Operations"),
    NOTIFICATION("Notifications");

    private final String displayName;

    ToolCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
