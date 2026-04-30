package com.digitalid.domain.model;

public enum CertificationCategory {

    FOOD_SAFETY("Food Safety"),
    PERMIT("Permit"),
    BACKGROUND_CHECK("Background Check"),
    LICENCE("Licence"),
    TRAINING("Training");

    private final String displayName;

    CertificationCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
