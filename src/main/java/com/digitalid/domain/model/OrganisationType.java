package com.digitalid.domain.model;

public enum OrganisationType {

    CENTRAL_AUTHORITY("Central Authority Service", "Issues and manages digital worker IDs and certifications"),
    FINANCIAL_SERVICE("Financial Service", "Verifies worker IDs for payroll and tax compliance"),
    FAST_FOOD("Fast Food Service", "Fast food chain requiring basic verification"),
    FINE_DINING("Fine Dining Service", "Upscale restaurant requiring detailed verification"),
    DELIVERY_SERVICE("Delivery Service", "Food delivery platform with driver-specific requirements"),
    COFFEE_SHOP("Coffee Shop Service", "Coffee shop with part-time staff"),
    STREET_VENDOR("Street Vendor Service", "Mobile food vendor credential management");

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
