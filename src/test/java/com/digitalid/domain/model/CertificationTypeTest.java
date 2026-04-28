package com.digitalid.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CertificationTypeTest {

    @Test
    void hasAtLeastTwentyCertificationTypes() {
        assertTrue(CertificationType.values().length >= 20,
                "Expected at least 20 cert types, got " + CertificationType.values().length);
    }

    @Test
    void usServSafeManagerHasFiveYearValidity() {
        CertificationType cert = CertificationType.US_SERVSAFE_MANAGER;
        assertEquals(60, cert.getValidityMonths());
        assertEquals(Region.UNITED_STATES, cert.getHomeRegion());
        assertEquals("National Restaurant Association", cert.getIssuingAuthority());
    }

    @Test
    void lifetimeCertsReturnMinusOne() {
        assertTrue(CertificationType.GERMANY_GESUNDHEITSZEUGNIS.isLifetime());
        assertTrue(CertificationType.JAPAN_FOOD_SANITATION_MANAGER.isLifetime());
        assertEquals(-1, CertificationType.HONG_KONG_BASIC_FOOD_HYGIENE.getValidityMonths());
    }

    @Test
    void nonLifetimeCertsHavePositiveMonths() {
        assertFalse(CertificationType.US_FOOD_HANDLER.isLifetime());
        assertTrue(CertificationType.US_FOOD_HANDLER.getValidityMonths() > 0);
    }

    @Test
    void southKoreaRequiresAnnualRenewal() {
        assertEquals(12, CertificationType.SOUTH_KOREA_FOOD_HYGIENE.getValidityMonths());
    }

    @Test
    void certTypesAreLinkedToCorrectRegions() {
        assertEquals(Region.UNITED_KINGDOM, CertificationType.UK_LEVEL_2_FOOD_SAFETY.getHomeRegion());
        assertEquals(Region.FRANCE, CertificationType.FRANCE_FORMATION_HACCP.getHomeRegion());
        assertEquals(Region.SINGAPORE, CertificationType.SINGAPORE_WSQ_FOOD_SAFETY.getHomeRegion());
    }
}
