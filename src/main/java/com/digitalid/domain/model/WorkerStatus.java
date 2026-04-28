package com.digitalid.domain.model;

public enum WorkerStatus {

    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    REVOKED("Revoked");

    private final String displayName;

    WorkerStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canTransitionTo(WorkerStatus target) {
        if (this == target) {
            return false;
        }

        switch (this) {
            case ACTIVE:
                return target == SUSPENDED || target == REVOKED;
            case SUSPENDED:
                return target == ACTIVE || target == REVOKED;
            case REVOKED:
                return false;
            default:
                return false;
        }
    }
}
