package com.digitalid.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrganisationProfileTest {

    @Test
    void centralAuthorityHasAllTools() {
        OrganisationProfile profile = OrganisationProfile.forType(OrganisationType.CENTRAL_AUTHORITY);
        assertEquals(ToolType.values().length, profile.getToolCount());
        assertTrue(profile.hasTool(ToolType.CREATE_WORKER));
        assertTrue(profile.hasTool(ToolType.CHECK_REGIONAL_COMPLIANCE));
    }

    @Test
    void allOrgsCanVerifyWorkAuthorisation() {
        for (OrganisationType type : OrganisationType.values()) {
            OrganisationProfile profile = OrganisationProfile.forType(type);
            assertTrue(profile.hasTool(ToolType.VERIFY_WORK_AUTHORISATION),
                    type + " should have VERIFY_WORK_AUTHORISATION");
        }
    }

    @Test
    void fineDiningHasFiveTools() {
        OrganisationProfile profile = OrganisationProfile.forType(OrganisationType.FINE_DINING);
        assertEquals(5, profile.getToolCount());
        assertTrue(profile.hasTool(ToolType.VERIFY_WITH_CERT_HISTORY));
        assertFalse(profile.hasTool(ToolType.CREATE_WORKER));
    }

    @Test
    void fastFoodOnlyGetsCoreTools() {
        OrganisationProfile profile = OrganisationProfile.forType(OrganisationType.FAST_FOOD);
        assertEquals(3, profile.getToolCount());
        assertTrue(profile.hasTool(ToolType.VERIFY_BASIC));
        assertFalse(profile.hasTool(ToolType.VERIFY_WITH_CERT_HISTORY));
    }
}
