package com.digitalid.domain.model;

public enum WorkAuthorisationStatus {

    VERIFIED("Verified"),
    PENDING("Pending"),
    REVERIFICATION_NEEDED("Reverification Needed");

    private final String displayName;

    WorkAuthorisationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canTransitionTo(WorkAuthorisationStatus target) {
        if (this == target) {
            return false;
        }

        switch (this) {
            case PENDING:
                return target == VERIFIED;
            case VERIFIED:
                return target == REVERIFICATION_NEEDED;
            case REVERIFICATION_NEEDED:
                return target == VERIFIED;
            default:
                return false;
        }
    }
}
