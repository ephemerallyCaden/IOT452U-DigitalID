package com.digitalid.application.registry;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.domain.exception.UnauthorisedAccessException;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.OrganisationType;
import com.digitalid.domain.model.OrganisationProfile;
import com.digitalid.domain.model.Region;
import com.digitalid.domain.model.ToolType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UseCaseRegistryTest {

    private UseCaseRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new UseCaseRegistry();
    }

    @Test
    void registersAndRetrievesUseCase() {
        UseCase<String, String> dummyUseCase = request -> "done";
        registry.register(ToolType.VIEW_WORKER, dummyUseCase);

        OrganisationContext context = makeContext(OrganisationType.FINE_DINING);
        UseCase<?, ?> retrieved = registry.getUseCase(ToolType.VIEW_WORKER, context);

        assertSame(dummyUseCase, retrieved);
    }

    @Test
    void throwsWhenOrganisationLacksAccess() {
        UseCase<String, String> dummyUseCase = request -> "done";
        registry.register(ToolType.CREATE_WORKER, dummyUseCase);

        // Fine dining doesn't have CREATE_WORKER access
        OrganisationContext context = makeContext(OrganisationType.FINE_DINING);

        assertThrows(UnauthorisedAccessException.class, () ->
                registry.getUseCase(ToolType.CREATE_WORKER, context));
    }

    @Test
    void centralAuthorityCanAccessAllTools() {
        UseCase<String, String> dummyUseCase = request -> "done";
        registry.register(ToolType.CREATE_WORKER, dummyUseCase);
        registry.register(ToolType.BULK_STATUS_UPDATE, dummyUseCase);

        OrganisationContext context = makeContext(OrganisationType.CENTRAL_AUTHORITY);

        assertDoesNotThrow(() -> registry.getUseCase(ToolType.CREATE_WORKER, context));
        assertDoesNotThrow(() -> registry.getUseCase(ToolType.BULK_STATUS_UPDATE, context));
    }

    @Test
    void throwsWhenUseCaseNotRegistered() {
        OrganisationContext context = makeContext(OrganisationType.CENTRAL_AUTHORITY);

        assertThrows(IllegalStateException.class, () ->
                registry.getUseCase(ToolType.VIEW_WORKER, context));
    }

    @Test
    void tracksRegisteredCount() {
        assertEquals(0, registry.size());

        registry.register(ToolType.VIEW_WORKER, request -> "a");
        registry.register(ToolType.VERIFY_BASIC, request -> "b");

        assertEquals(2, registry.size());
        assertTrue(registry.isRegistered(ToolType.VIEW_WORKER));
        assertFalse(registry.isRegistered(ToolType.DELETE_WORKER));
    }

    private OrganisationContext makeContext(OrganisationType type) {
        OrganisationProfile profile = OrganisationProfile.forType(type);
        Region region = (type == OrganisationType.CENTRAL_AUTHORITY) ? null : Region.UNITED_KINGDOM;
        return new OrganisationContext("ORG-001", type, "Test Org", region, profile.getAllowedTools());
    }
}
