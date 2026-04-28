package com.digitalid.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrganisationTypeTest {

    @Test
    void shouldHaveSevenOrganisationTypes() {
        assertEquals(7, OrganisationType.values().length);
    }

    @Test
    void centralAuthorityDetails() {
        OrganisationType ca = OrganisationType.CENTRAL_AUTHORITY;
        assertEquals("Food Service Certification Board", ca.getDisplayName());
        assertFalse(ca.getDescription().isEmpty());
    }

    @Test
    void fineDiningDetails() {
        assertEquals("Le Gourmet", OrganisationType.FINE_DINING.getDisplayName());
    }
}
