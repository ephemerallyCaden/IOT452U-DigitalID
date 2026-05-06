package com.digitalid.domain.service;

import com.digitalid.domain.exception.ValidationException;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.CertificationType;
import com.digitalid.domain.model.Region;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CertificationValidationServiceTest {

    private final CertificationValidationService service = new CertificationValidationService();

    @Test
    void rejectsFutureIssueDate() {
        Certification cert = new Certification("C-1", "WK-GB-2024-001",
                CertificationType.UK_LEVEL_2_FOOD_SAFETY, "CIEH", "L2-001",
                LocalDate.now().plusDays(5), LocalDate.now().plusYears(3));
        assertThrows(ValidationException.class, () -> service.validateCertification(cert));
    }

    @Test
    void certMustMatchWorkerRegion() {
        // UK cert for UK worker
        assertDoesNotThrow(() ->
                service.validateCertificationForRegion(CertificationType.UK_LEVEL_2_FOOD_SAFETY, Region.UNITED_KINGDOM));
        // UK cert for US worker
        assertThrows(ValidationException.class, () ->
                service.validateCertificationForRegion(CertificationType.UK_LEVEL_2_FOOD_SAFETY, Region.UNITED_STATES));
    }

    @Test
    void euHaccpAcceptedInEuCountries() {
        assertDoesNotThrow(() ->
                service.validateCertificationForRegion(CertificationType.EU_HACCP, Region.GERMANY));
        // but not outside the EU
        assertThrows(ValidationException.class, () ->
                service.validateCertificationForRegion(CertificationType.EU_HACCP, Region.SINGAPORE));
    }

    @Test
    void renewalRejectsLongExpiredCert() {
        // Cert expired over a year ago
        Certification longExpired = new Certification("C-3", "WK-GB-2024-001",
                CertificationType.UK_LEVEL_2_FOOD_SAFETY, "CIEH", "L2-003",
                LocalDate.of(2020, 1, 1), LocalDate.of(2022, 1, 1));
        assertThrows(ValidationException.class, () -> service.validateRenewal(longExpired));
    }

    @Test
    void renewalAcceptsRecentlyExpiredCert() {
        // Cert expired less than a year ago
        Certification recentlyExpired = new Certification("C-4", "WK-GB-2024-001",
                CertificationType.UK_LEVEL_2_FOOD_SAFETY, "CIEH", "L2-004",
                LocalDate.of(2022, 1, 1), LocalDate.now().minusDays(30));
        assertDoesNotThrow(() -> service.validateRenewal(recentlyExpired));
    }
}
