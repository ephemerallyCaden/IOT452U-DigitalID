package com.digitalid.domain.model;

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
    private String status; // "VERIFIED", "PENDING", "REVERIFICATION_NEEDED"
    private final List<String> documentsPresented;
    private final LocalDate expiryDate; // null if indefinite (e.g. citizen/settled status)
    private final String verifiedBy;

    public WorkAuthorisation(String id, String workerId, Region region,
                             LocalDate verificationDate, List<String> documentsPresented,
                             LocalDate expiryDate, String verifiedBy) {
        this.id = id;
        this.workerId = workerId;
        this.region = region;
        this.verificationDate = verificationDate;
        this.status = "VERIFIED";
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
        return "VERIFIED".equals(status) && !isExpired();
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
    public String getStatus() { return status; }
    public List<String> getDocumentsPresented() { return List.copyOf(documentsPresented); }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getVerifiedBy() { return verifiedBy; }

    public void setStatus(String status) { this.status = status; }
}
