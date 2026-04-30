package com.digitalid.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrganisationContextTest {

    @Test
    void hasToolAccessReturnsTrueForAllowedTool() {
        OrganisationContext ctx = new OrganisationContext("ORG-001",
                OrganisationType.FAST_FOOD, "Mega Slice Pizza",
                Set.of(ToolType.VIEW_WORKER_ID, ToolType.VERIFY_BASIC));
        assertTrue(ctx.hasToolAccess(ToolType.VIEW_WORKER_ID));
        assertTrue(ctx.hasToolAccess(ToolType.VERIFY_BASIC));
    }

    @Test
    void hasToolAccessReturnsFalseForUnauthorisedTool() {
        OrganisationContext ctx = new OrganisationContext("ORG-001",
                OrganisationType.FAST_FOOD, "Mega Slice Pizza",
                Set.of(ToolType.VIEW_WORKER_ID, ToolType.VERIFY_BASIC));
        assertFalse(ctx.hasToolAccess(ToolType.CREATE_WORKER_ID));
    }

    @Test
    void allowedToolsSetIsImmutable() {
        Set<ToolType> tools = new java.util.HashSet<>(Set.of(ToolType.VIEW_WORKER_ID));
        OrganisationContext ctx = new OrganisationContext("ORG-001",
                OrganisationType.COFFEE_SHOP, "Daily Grind", tools);
        tools.add(ToolType.DELETE_WORKER_ID);
        assertFalse(ctx.hasToolAccess(ToolType.DELETE_WORKER_ID));
    }
}
