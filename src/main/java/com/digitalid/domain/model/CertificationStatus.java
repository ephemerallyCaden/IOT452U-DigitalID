package com.digitalid.domain.model;

public enum CertificationStatus {

    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    EXPIRED("Expired");

    private final String displayName;

    CertificationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canTransitionTo(CertificationStatus target) {
        if (this == target) {
            return false;
        }

        switch (this) {
            case ACTIVE:
                return target == SUSPENDED || target == EXPIRED;
            case SUSPENDED:
                return target == ACTIVE || target == EXPIRED;
            case EXPIRED:
                return false;
            default:
                return false;
        }
    }
}
