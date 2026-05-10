package com.digitalid.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrganisationContextTest {

    @Test
    void hasToolAccessReturnsTrueForAllowedTool() {
        OrganisationContext ctx = new OrganisationContext("ORG-001",
                OrganisationType.FAST_FOOD, "Mega Slice Pizza", Region.UNITED_STATES,
                Set.of(ToolType.VIEW_WORKER, ToolType.VERIFY_BASIC));
        assertTrue(ctx.hasToolAccess(ToolType.VIEW_WORKER));
        assertTrue(ctx.hasToolAccess(ToolType.VERIFY_BASIC));
    }

    @Test
    void hasToolAccessReturnsFalseForUnauthorisedTool() {
        OrganisationContext ctx = new OrganisationContext("ORG-001",
                OrganisationType.FAST_FOOD, "Mega Slice Pizza", Region.UNITED_STATES,
                Set.of(ToolType.VIEW_WORKER, ToolType.VERIFY_BASIC));
        assertFalse(ctx.hasToolAccess(ToolType.CREATE_WORKER));
    }

    @Test
    void allowedToolsSetIsImmutable() {
        Set<ToolType> tools = new java.util.HashSet<>(Set.of(ToolType.VIEW_WORKER));
        OrganisationContext ctx = new OrganisationContext("ORG-001",
                OrganisationType.COFFEE_SHOP, "Daily Grind", Region.UNITED_KINGDOM, tools);
        tools.add(ToolType.DELETE_WORKER);
        assertFalse(ctx.hasToolAccess(ToolType.DELETE_WORKER));
    }
}
