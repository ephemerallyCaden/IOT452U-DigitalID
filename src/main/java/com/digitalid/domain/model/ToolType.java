package com.digitalid.domain.model;

public enum ToolType {

    // Core (all organisations)
    VIEW_WORKER(ToolCategory.CORE, "View Worker"),
    VERIFY_BASIC(ToolCategory.CORE, "Basic Verification"),
    VERIFY_WORK_AUTHORISATION(ToolCategory.CORE, "Verify Work Authorisation"),

    // Identity Management (central authority)
    CREATE_WORKER(ToolCategory.IDENTITY_MANAGEMENT, "Create Worker"),
    UPDATE_WORKER(ToolCategory.IDENTITY_MANAGEMENT, "Update Worker"),
    CHANGE_STATUS(ToolCategory.IDENTITY_MANAGEMENT, "Change Worker Status"),
    DELETE_WORKER(ToolCategory.IDENTITY_MANAGEMENT, "Delete Worker"),

    // Certification Management (central authority)
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
    CHECK_REGIONAL_COMPLIANCE(ToolCategory.REPORTING, "Check Regional Compliance"),
    VIEW_ORGANISATION_ACTIVITY(ToolCategory.REPORTING, "View Organisation Activity"),

    // Search
    SEARCH_WORKERS(ToolCategory.SEARCH, "Search Workers"),

    // Batch Operations
    BULK_STATUS_UPDATE(ToolCategory.BATCH, "Bulk Status Update"),
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
