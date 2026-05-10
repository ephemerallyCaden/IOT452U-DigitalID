package com.digitalid.integration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import com.digitalid.application.registry.UseCaseRegistry;
import com.digitalid.domain.exception.UnauthorisedAccessException;
import com.digitalid.domain.model.*;
import com.digitalid.infrastructure.config.DependencyInjection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Integration test verifying that organisations cannot access tools outside their profile.
 * Uses the real DI wiring to confirm access control works end-to-end.
 */
class AccessControlIntegrationTest {

    private Path tempDir;
    private DependencyInjection di;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("digitalid-access-test-");
        di = new DependencyInjection(tempDir.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try { Files.deleteIfExists(path); } catch (IOException ignored) {}
                });
    }

    @Test
    void fineDiningCannotCreateWorkers() {
        OrganisationContext context = di.createContext("ORG-FD", OrganisationType.FINE_DINING, "Le Gourmet", Region.FRANCE);
        UseCaseRegistry registry = di.buildRegistry(context);

        assertThrows(UnauthorisedAccessException.class,
                () -> registry.getUseCase(ToolType.CREATE_WORKER, context));
    }

    @Test
    void fineDiningCannotDeleteWorkers() {
        OrganisationContext context = di.createContext("ORG-FD", OrganisationType.FINE_DINING, "Le Gourmet", Region.FRANCE);
        UseCaseRegistry registry = di.buildRegistry(context);

        assertThrows(UnauthorisedAccessException.class,
                () -> registry.getUseCase(ToolType.DELETE_WORKER, context));
    }

    @Test
    void deliveryServiceCannotAccessCertHistory() {
        OrganisationContext context = di.createContext("ORG-DS", OrganisationType.DELIVERY_SERVICE, "QuickBite", Region.UNITED_KINGDOM);
        UseCaseRegistry registry = di.buildRegistry(context);

        assertThrows(UnauthorisedAccessException.class,
                () -> registry.getUseCase(ToolType.VERIFY_WITH_CERT_HISTORY, context));
    }

    @Test
    void streetVendorCannotAccessBulkOperations() {
        OrganisationContext context = di.createContext("ORG-SV", OrganisationType.STREET_VENDOR, "Urban Eats", Region.UNITED_STATES);
        UseCaseRegistry registry = di.buildRegistry(context);

        assertThrows(UnauthorisedAccessException.class,
                () -> registry.getUseCase(ToolType.BULK_STATUS_UPDATE, context));
    }

    @Test
    void centralAuthorityCanAccessEverything() {
        OrganisationContext context = di.createContext("ORG-CA", OrganisationType.CENTRAL_AUTHORITY, "Central Authority", null);
        UseCaseRegistry registry = di.buildRegistry(context);

        // Should not throw for any tool
        assertDoesNotThrow(() -> registry.getUseCase(ToolType.CREATE_WORKER, context));
        assertDoesNotThrow(() -> registry.getUseCase(ToolType.DELETE_WORKER, context));
        assertDoesNotThrow(() -> registry.getUseCase(ToolType.VERIFY_BASIC, context));
        assertDoesNotThrow(() -> registry.getUseCase(ToolType.BULK_STATUS_UPDATE, context));
        assertDoesNotThrow(() -> registry.getUseCase(ToolType.VIEW_AUDIT_LOG, context));
    }

    @Test
    void fineDiningCanAccessAllowedVerificationTools() {
        OrganisationContext context = di.createContext("ORG-FD", OrganisationType.FINE_DINING, "Le Gourmet", Region.FRANCE);
        UseCaseRegistry registry = di.buildRegistry(context);

        // Fine dining has: VIEW_WORKER, VERIFY_BASIC, VERIFY_WITH_CERT_HISTORY, VERIFY_WITH_ATTRIBUTES
        assertDoesNotThrow(() -> registry.getUseCase(ToolType.VIEW_WORKER, context));
        assertDoesNotThrow(() -> registry.getUseCase(ToolType.VERIFY_BASIC, context));
        assertDoesNotThrow(() -> registry.getUseCase(ToolType.VERIFY_WITH_CERT_HISTORY, context));
        assertDoesNotThrow(() -> registry.getUseCase(ToolType.VERIFY_WITH_ATTRIBUTES, context));
    }
}
