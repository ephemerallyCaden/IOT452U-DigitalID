package com.digitalid.domain.service;

import com.digitalid.domain.exception.ValidationException;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.CertificationType;
import com.digitalid.domain.model.Region;

import java.time.LocalDate;

public class CertificationValidationService {

    public void validateCertification(Certification cert) {
        if (cert.getIssueDate() == null) {
            throw new ValidationException("Issue date is required");
        }
        if (cert.getExpirationDate() != null && !cert.getIssueDate().isBefore(cert.getExpirationDate())) {
            throw new ValidationException("Issue date must be before expiration date");
        }
        if (cert.getIssueDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Issue date cannot be in the future");
        }
    }

    public boolean isCertRelevantForRegion(CertificationType type, Region targetRegion) {
        if (targetRegion == null) {
            return true; // global authority — all certs are relevant
        }
        Region certRegion = type.getHomeRegion();
        if (certRegion == targetRegion) {
            return true;
        }
        // EU-wide certs are valid in EU member states
        return certRegion == Region.EU_GENERAL && isEuCountry(targetRegion);
    }

    public void validateRenewal(Certification existing) {
        if (existing.isExpired() && existing.getExpirationDate() != null
                && existing.getExpirationDate().plusYears(1).isBefore(LocalDate.now())) {
            throw new ValidationException(
                    "Cannot renew certification that has been expired for more than one year");
        }
    }

    private boolean isEuCountry(Region region) {
        return region == Region.GERMANY || region == Region.FRANCE
                || region == Region.ITALY || region == Region.SPAIN;
    }
}
