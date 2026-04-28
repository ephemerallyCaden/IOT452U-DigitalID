package com.digitalid.domain.model;

public enum ToolType {

    // Core
    VIEW_WORKER_ID(ToolCategory.CORE, "View Worker ID"),
    VERIFY_BASIC(ToolCategory.CORE, "Basic Verification"),

    // Identity Management
    CREATE_WORKER_ID(ToolCategory.IDENTITY_MANAGEMENT, "Create Worker ID"),
    UPDATE_WORKER_ID(ToolCategory.IDENTITY_MANAGEMENT, "Update Worker ID"),
    CHANGE_STATUS(ToolCategory.IDENTITY_MANAGEMENT, "Change Worker Status"),
    DELETE_WORKER_ID(ToolCategory.IDENTITY_MANAGEMENT, "Delete Worker ID"),

    // Certification Management
    ADD_CERTIFICATION(ToolCategory.CERTIFICATION_MANAGEMENT, "Add Certification"),
    RENEW_CERTIFICATION(ToolCategory.CERTIFICATION_MANAGEMENT, "Renew Certification"),
    UPDATE_CERTIFICATION_STATUS(ToolCategory.CERTIFICATION_MANAGEMENT, "Update Certification Status"),

    // Enhanced Verification
    VERIFY_WITH_CERT_HISTORY(ToolCategory.ENHANCED_VERIFICATION, "Verify with Certification History"),
    VERIFY_WITH_CONDITIONS(ToolCategory.ENHANCED_VERIFICATION, "Verify with Conditions"),
    VERIFY_WITH_PERMITS(ToolCategory.ENHANCED_VERIFICATION, "Verify with Permits"),
    VERIFY_WITH_ATTRIBUTES(ToolCategory.ENHANCED_VERIFICATION, "Verify with Attributes"),

    // Reporting & Analytics
    VIEW_AUDIT_LOG(ToolCategory.REPORTING, "View Audit Log"),
    GENERATE_COMPLIANCE_REPORT(ToolCategory.REPORTING, "Generate Compliance Report"),
    CHECK_EXPIRING_CERTS(ToolCategory.REPORTING, "Check Expiring Certifications"),
    GENERATE_REGIONAL_REPORT(ToolCategory.REPORTING, "Generate Regional Report"),
    VIEW_ORGANISATION_ACTIVITY(ToolCategory.REPORTING, "View Organisation Activity"),

    // Search & Query
    SEARCH_WORKERS(ToolCategory.SEARCH, "Search Workers"),
    SEARCH_BY_CERTIFICATION(ToolCategory.SEARCH, "Search by Certification"),
    SEARCH_BY_EXPIRATION(ToolCategory.SEARCH, "Search by Expiration"),

    // Batch Operations
    BULK_STATUS_UPDATE(ToolCategory.BATCH, "Bulk Status Update"),
    BULK_CERTIFICATION_CHECK(ToolCategory.BATCH, "Bulk Certification Check"),
    EXPORT_WORKER_DATA(ToolCategory.BATCH, "Export Worker Data"),

    // Notifications
    SEND_RENEWAL_REMINDER(ToolCategory.NOTIFICATION, "Send Renewal Reminder"),
    SEND_STATUS_NOTIFICATION(ToolCategory.NOTIFICATION, "Send Status Notification");

    private final ToolCategory category;
    private final String displayName;

    ToolType(ToolCategory category, String displayName) {
        this.category = category;
        this.displayName = displayName;
    }

    public ToolCategory getCategory() {
        return category;
    }

    public String getDisplayName() {
        return displayName;
    }
}
