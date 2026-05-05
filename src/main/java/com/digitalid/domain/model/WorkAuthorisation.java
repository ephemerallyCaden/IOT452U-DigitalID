package com.digitalid.domain.model;

import com.digitalid.domain.exception.InvalidOperationException;
import com.digitalid.domain.exception.ValidationException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class WorkAuthorisation {

    private final String id;
    private final String workerId;
    private final Region region;
    private final LocalDate verificationDate;
    private WorkAuthorisationStatus status;
    private final List<String> documentsPresented;
    private final LocalDate expiryDate; // null if indefinite (e.g. citizen/settled status)
    private final String verifiedBy;

    public WorkAuthorisation(String id, String workerId, Region region,
                             LocalDate verificationDate, List<String> documentsPresented,
                             LocalDate expiryDate, String verifiedBy) {
        if (id == null || id.isBlank()) {
            throw new ValidationException("Work authorisation ID cannot be empty");
        }
        if (workerId == null || workerId.isBlank()) {
            throw new ValidationException("Worker ID cannot be empty");
        }
        if (region == null) {
            throw new ValidationException("Region cannot be null");
        }
        if (verificationDate == null) {
            throw new ValidationException("Verification date cannot be null");
        }
        if (documentsPresented == null || documentsPresented.isEmpty()) {
            throw new ValidationException("At least one document must be presented");
        }
        this.id = id;
        this.workerId = workerId;
        this.region = region;
        this.verificationDate = verificationDate;
        this.status = WorkAuthorisationStatus.VERIFIED;
        this.documentsPresented = new ArrayList<>(documentsPresented);
        this.expiryDate = expiryDate;
        this.verifiedBy = verifiedBy;
    }

    public boolean isExpired() {
        if (expiryDate == null) {
            return false; // indefinite right to work
        }
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isValid() {
        return status == WorkAuthorisationStatus.VERIFIED && !isExpired();
    }

    public boolean needsReverification() {
        if (expiryDate == null) {
            return false;
        }
        return !LocalDate.now().isBefore(expiryDate);
    }

    public long daysUntilExpiry() {
        if (expiryDate == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    public void markForReverification() {
        if (!status.canTransitionTo(WorkAuthorisationStatus.REVERIFICATION_NEEDED)) {
            throw new InvalidOperationException(
                    "Cannot mark for reverification from " + status.getDisplayName() + " state");
        }
        this.status = WorkAuthorisationStatus.REVERIFICATION_NEEDED;
    }

    public void confirm() {
        if (!status.canTransitionTo(WorkAuthorisationStatus.VERIFIED)) {
            throw new InvalidOperationException(
                    "Cannot confirm from " + status.getDisplayName() + " state");
        }
        this.status = WorkAuthorisationStatus.VERIFIED;
    }

    /**
     * Region-specific compliance check. UK requires verification BEFORE employment starts.
     * US (I-9) allows 3 business days after hire.
     */
    public boolean meetsRegionalTimingRequirement(LocalDate hireDate) {
        switch (region) {
            case UNITED_KINGDOM:
                // UK: must be verified on or before the hire date
                return !verificationDate.isAfter(hireDate);
            case UNITED_STATES:
                // US: within 3 business days of hire
                LocalDate deadline = addBusinessDays(hireDate, 3);
                return !verificationDate.isAfter(deadline);
            default:
                // Other regions: verified before or on hire date as default
                return !verificationDate.isAfter(hireDate);
        }
    }

    private LocalDate addBusinessDays(LocalDate start, int days) {
        LocalDate result = start;
        int added = 0;
        while (added < days) {
            result = result.plusDays(1);
            if (result.getDayOfWeek() != DayOfWeek.SATURDAY
                    && result.getDayOfWeek() != DayOfWeek.SUNDAY) {
                added++;
            }
        }
        return result;
    }

    // Getters

    public String getId() { return id; }
    public String getWorkerId() { return workerId; }
    public Region getRegion() { return region; }
    public LocalDate getVerificationDate() { return verificationDate; }
    public WorkAuthorisationStatus getStatus() { return status; }
    public List<String> getDocumentsPresented() { return List.copyOf(documentsPresented); }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getVerifiedBy() { return verifiedBy; }
}
