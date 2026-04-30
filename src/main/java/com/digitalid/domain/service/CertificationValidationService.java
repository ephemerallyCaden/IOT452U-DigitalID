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

    public void validateCertificationForRegion(CertificationType type, Region workerRegion) {
        // cert type should match the worker's region, or be a general EU cert for EU countries
        Region certRegion = type.getHomeRegion();
        if (certRegion == workerRegion) {
            return; // direct match
        }
        if (certRegion == Region.EU_GENERAL && isEuCountry(workerRegion)) {
            return; // EU-wide cert is valid in EU member states
        }
        throw new ValidationException(
                "Certification " + type.getDisplayName() + " is not valid for region " + workerRegion.getDisplayName());
    }

    public void validateRenewal(Certification existing, CertificationType newType) {
        if (existing.getType() != newType) {
            throw new ValidationException("Renewal must be for the same certification type");
        }
    }

    private boolean isEuCountry(Region region) {
        return region == Region.GERMANY || region == Region.FRANCE
                || region == Region.ITALY || region == Region.SPAIN;
    }
}
