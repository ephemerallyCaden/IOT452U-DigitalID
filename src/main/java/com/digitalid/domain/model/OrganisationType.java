package com.digitalid.domain.model;

public enum OrganisationType {

    CENTRAL_AUTHORITY("Food Service Certification Board", "Issues and manages digital worker IDs and certifications"),
    FINANCIAL_SERVICE("Bread Vault Financial", "Verifies worker IDs for payroll and tax compliance"),
    FAST_FOOD("Kentucky Fried Celery", "Fast food chain"),
    FINE_DINING("Le Gourmet", "Upscale restaurant requiring detailed verification"),
    DELIVERY_SERVICE("Deliverallaby", "Food delivery platform with driver-specific requirements"),
    COFFEE_SHOP("Heytea", "Coffee shop with part-time staff"),
    STREET_VENDOR("Ling Lings Steam Kitchen", "Mobile food vendor credential management");

    private final String displayName;
    private final String description;

    OrganisationType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
