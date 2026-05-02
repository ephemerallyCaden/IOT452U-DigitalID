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
        assertEquals("Central Authority Service", ca.getDisplayName());
        assertFalse(ca.getDescription().isEmpty());
    }

    @Test
    void fineDiningDetails() {
        assertEquals("Fine Dining Service", OrganisationType.FINE_DINING.getDisplayName());
    }
}
