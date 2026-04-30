package com.digitalid.domain.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Certification {

    private final String id;
    private final String workerId;
    private final CertificationType type;
    private final String issuingAuthority;
    private final String certificationNumber;
    private final LocalDate issueDate;
    private final LocalDate expirationDate;
    private String status; // "ACTIVE", "SUSPENDED", "EXPIRED"

    public Certification(String id, String workerId, CertificationType type,
                         String issuingAuthority, String certificationNumber,
                         LocalDate issueDate, LocalDate expirationDate) {
        if (expirationDate != null && !issueDate.isBefore(expirationDate)) {
            throw new IllegalArgumentException("Issue date must be before expiration date");
        }
        this.id = id;
        this.workerId = workerId;
        this.type = type;
        this.issuingAuthority = issuingAuthority;
        this.certificationNumber = certificationNumber;
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
        this.status = "ACTIVE";
    }

    public boolean isExpired() {
        if (expirationDate == null) {
            return false; // lifetime cert
        }
        return LocalDate.now().isAfter(expirationDate);
    }

    public boolean isValid() {
        return "ACTIVE".equals(status) && !isExpired();
    }

    public long daysUntilExpiration() {
        if (expirationDate == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);
    }

    public boolean needsRenewal(int daysThreshold) {
        if (expirationDate == null) {
            return false;
        }
        return daysUntilExpiration() <= daysThreshold;
    }

    public void suspend() {
        this.status = "SUSPENDED";
    }

    public void markExpired() {
        this.status = "EXPIRED";
    }

    public void reactivate() {
        if ("EXPIRED".equals(status)) {
            throw new IllegalStateException("Cannot reactivate an expired certification");
        }
        this.status = "ACTIVE";
    }

    // Getters

    public String getId() { return id; }
    public String getWorkerId() { return workerId; }
    public CertificationType getType() { return type; }
    public String getIssuingAuthority() { return issuingAuthority; }
    public String getCertificationNumber() { return certificationNumber; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public String getStatus() { return status; }
}
