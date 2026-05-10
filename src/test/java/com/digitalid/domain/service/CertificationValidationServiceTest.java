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
    void certIsRelevantForMatchingRegion() {
        assertTrue(service.isCertRelevantForRegion(CertificationType.UK_LEVEL_2_FOOD_SAFETY, Region.UNITED_KINGDOM));
        assertFalse(service.isCertRelevantForRegion(CertificationType.UK_LEVEL_2_FOOD_SAFETY, Region.UNITED_STATES));
    }

    @Test
    void euCertIsRelevantInEuMemberStates() {
        assertTrue(service.isCertRelevantForRegion(CertificationType.EU_HACCP, Region.GERMANY));
        assertTrue(service.isCertRelevantForRegion(CertificationType.EU_HACCP, Region.FRANCE));
        assertFalse(service.isCertRelevantForRegion(CertificationType.EU_HACCP, Region.SINGAPORE));
    }

    @Test
    void allCertsRelevantForNullRegion() {
        // Central Authority (null region) sees all certs as relevant
        assertTrue(service.isCertRelevantForRegion(CertificationType.UK_LEVEL_2_FOOD_SAFETY, null));
        assertTrue(service.isCertRelevantForRegion(CertificationType.US_FOOD_HANDLER, null));
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
