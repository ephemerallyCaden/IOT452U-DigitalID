package com.digitalid.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CertificationTest {

    private Certification makeCert(LocalDate issue, LocalDate expiry) {
        return new Certification("CERT-001", "WK-US-2024-001",
                CertificationType.US_FOOD_HANDLER, "State Health Dept",
                "FH-12345", issue, expiry);
    }

    @Test
    void newCertificationIsActiveByDefault() {
        Certification cert = makeCert(LocalDate.of(2024, 1, 1), LocalDate.of(2027, 1, 1));
        assertEquals("ACTIVE", cert.getStatus());
        assertTrue(cert.isValid());
    }

    @Test
    void expiredCertIsNotValid() {
        Certification cert = makeCert(LocalDate.of(2020, 1, 1), LocalDate.of(2022, 1, 1));
        assertTrue(cert.isExpired());
        assertFalse(cert.isValid());
    }

    @Test
    void futureCertIsNotExpired() {
        Certification cert = makeCert(LocalDate.of(2024, 1, 1), LocalDate.of(2030, 6, 15));
        assertFalse(cert.isExpired());
    }

    @Test
    void issueDateMustBeBeforeExpiration() {
        assertThrows(IllegalArgumentException.class, () ->
                makeCert(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 1, 1)));
    }

    @Test
    void needsRenewalWhenWithinThreshold() {
        // cert expiring in 20 days
        LocalDate soon = LocalDate.now().plusDays(20);
        Certification cert = makeCert(LocalDate.of(2024, 1, 1), soon);
        assertTrue(cert.needsRenewal(30));
        assertFalse(cert.needsRenewal(10));
    }

    @Test
    void lifetimeCertNeverExpires() {
        Certification cert = new Certification("CERT-002", "WK-JP-2024-001",
                CertificationType.JAPAN_FOOD_SANITATION_MANAGER, "Public Health Center",
                "JP-999", LocalDate.of(2020, 3, 1), null);
        assertFalse(cert.isExpired());
        assertFalse(cert.needsRenewal(90));
    }

    @Test
    void suspendedCertIsNotValid() {
        Certification cert = makeCert(LocalDate.of(2024, 1, 1), LocalDate.of(2030, 1, 1));
        cert.suspend();
        assertEquals("SUSPENDED", cert.getStatus());
        assertFalse(cert.isValid());
    }

    @Test
    void cannotReactivateExpiredCert() {
        Certification cert = makeCert(LocalDate.of(2024, 1, 1), LocalDate.of(2030, 1, 1));
        cert.markExpired();
        assertThrows(IllegalStateException.class, cert::reactivate);
    }
}
