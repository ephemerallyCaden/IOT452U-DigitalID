package com.digitalid.application.registry;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.domain.exception.UnauthorisedAccessException;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.OrganisationType;
import com.digitalid.domain.model.OrganisationProfile;
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
        registry.register(ToolType.VIEW_WORKER_ID, dummyUseCase);

        OrganisationContext context = makeContext(OrganisationType.FINE_DINING);
        UseCase<?, ?> retrieved = registry.getUseCase(ToolType.VIEW_WORKER_ID, context);

        assertSame(dummyUseCase, retrieved);
    }

    @Test
    void throwsWhenOrganisationLacksAccess() {
        UseCase<String, String> dummyUseCase = request -> "done";
        registry.register(ToolType.CREATE_WORKER_ID, dummyUseCase);

        // Fine dining doesn't have CREATE_WORKER_ID access
        OrganisationContext context = makeContext(OrganisationType.FINE_DINING);

        assertThrows(UnauthorisedAccessException.class, () ->
                registry.getUseCase(ToolType.CREATE_WORKER_ID, context));
    }

    @Test
    void centralAuthorityCanAccessAllTools() {
        UseCase<String, String> dummyUseCase = request -> "done";
        registry.register(ToolType.CREATE_WORKER_ID, dummyUseCase);
        registry.register(ToolType.BULK_STATUS_UPDATE, dummyUseCase);

        OrganisationContext context = makeContext(OrganisationType.CENTRAL_AUTHORITY);

        assertDoesNotThrow(() -> registry.getUseCase(ToolType.CREATE_WORKER_ID, context));
        assertDoesNotThrow(() -> registry.getUseCase(ToolType.BULK_STATUS_UPDATE, context));
    }

    @Test
    void throwsWhenUseCaseNotRegistered() {
        OrganisationContext context = makeContext(OrganisationType.CENTRAL_AUTHORITY);

        assertThrows(IllegalStateException.class, () ->
                registry.getUseCase(ToolType.VIEW_WORKER_ID, context));
    }

    @Test
    void tracksRegisteredCount() {
        assertEquals(0, registry.size());

        registry.register(ToolType.VIEW_WORKER_ID, request -> "a");
        registry.register(ToolType.VERIFY_BASIC, request -> "b");

        assertEquals(2, registry.size());
        assertTrue(registry.isRegistered(ToolType.VIEW_WORKER_ID));
        assertFalse(registry.isRegistered(ToolType.DELETE_WORKER_ID));
    }

    private OrganisationContext makeContext(OrganisationType type) {
        OrganisationProfile profile = OrganisationProfile.forType(type);
        return new OrganisationContext("ORG-001", type, "Test Org", profile.getAllowedTools());
    }
}
