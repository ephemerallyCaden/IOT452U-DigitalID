package com.digitalid.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CertificationTypeTest {

    @Test
    void allExistingCertsAreFoodSafety() {
        for (CertificationType type : CertificationType.values()) {
            assertEquals(CertificationCategory.FOOD_SAFETY, type.getCategory());
        }
    }

    @Test
    void lifetimeCertsReturnMinusOne() {
        assertTrue(CertificationType.GERMANY_GESUNDHEITSZEUGNIS.isLifetime());
        assertTrue(CertificationType.JAPAN_FOOD_SANITATION_MANAGER.isLifetime());
    }

    @Test
    void certTypesLinkedToCorrectRegions() {
        assertEquals(Region.UNITED_KINGDOM, CertificationType.UK_LEVEL_2_FOOD_SAFETY.getHomeRegion());
        assertEquals(Region.FRANCE, CertificationType.FRANCE_FORMATION_HACCP.getHomeRegion());
    }
}
