package com.digitalid.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CertificationTypeTest {

    @Test
    void ukFoodSafetyCertsHaveCorrectCategory() {
        assertEquals(CertificationCategory.FOOD_SAFETY, CertificationType.UK_LEVEL_2_FOOD_SAFETY.getCategory());
        assertEquals(CertificationCategory.FOOD_SAFETY, CertificationType.UK_LEVEL_3_FOOD_SAFETY.getCategory());
    }

    @Test
    void ukNonFoodSafetyCertsHaveCorrectCategories() {
        assertEquals(CertificationCategory.PERMIT, CertificationType.UK_STREET_TRADING_LICENCE.getCategory());
        assertEquals(CertificationCategory.BACKGROUND_CHECK, CertificationType.UK_DBS_CHECK.getCategory());
        assertEquals(CertificationCategory.LICENCE, CertificationType.UK_DRIVER_LICENCE.getCategory());
        assertEquals(CertificationCategory.TRAINING, CertificationType.UK_ALLERGEN_TRAINING.getCategory());
    }

    @Test
    void lifetimeCertsReturnMinusOne() {
        assertTrue(CertificationType.GERMANY_GESUNDHEITSZEUGNIS.isLifetime());
        assertTrue(CertificationType.UK_DRIVER_LICENCE.isLifetime());
    }

    @Test
    void certTypesLinkedToCorrectRegions() {
        assertEquals(Region.UNITED_KINGDOM, CertificationType.UK_LEVEL_2_FOOD_SAFETY.getHomeRegion());
        assertEquals(Region.UNITED_KINGDOM, CertificationType.UK_DBS_CHECK.getHomeRegion());
        assertEquals(Region.FRANCE, CertificationType.FRANCE_FORMATION_HACCP.getHomeRegion());
    }
}
