package com.digitalid.integration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.registry.UseCaseRegistry;
import com.digitalid.application.request.*;
import com.digitalid.domain.exception.InvalidOperationException;
import com.digitalid.domain.model.*;
import com.digitalid.infrastructure.config.ApplicationFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * End-to-end integration test that wires up the full system with real JSON repositories.
 * Tests a complete worker lifecycle: create -> view -> update -> suspend -> reactivate -> revoke.
 */
class WorkerLifecycleIntegrationTest {

    private Path tempDir;
    private ApplicationFactory di;
    private UseCaseRegistry registry;
    private OrganisationContext context;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("digitalid-test-");
        di = new ApplicationFactory(tempDir.toString());
        context = di.createContext("ORG-INTEG", OrganisationType.CENTRAL_AUTHORITY, "Integration Test Authority", null);
        registry = di.buildRegistry(context);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up temp directory
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try { Files.deleteIfExists(path); } catch (IOException ignored) {}
                });
    }

    @Test
    @SuppressWarnings("unchecked")
    void fullWorkerLifecycle() {
        // 1. Create worker
        CreateWorkerRequest createReq = new CreateWorkerRequest(
                "Integration Test Worker", LocalDate.of(1999, 7, 22),
                "integration@test.com", Region.UNITED_KINGDOM);
        UseCase<CreateWorkerRequest, Worker> createUC =
                (UseCase<CreateWorkerRequest, Worker>) registry.getUseCase(ToolType.CREATE_WORKER, context);
        Worker created = createUC.execute(createReq);

        assertNotNull(created.getWorkerId());
        assertEquals("Integration Test Worker", created.getFullName());
        assertEquals(WorkerStatus.ACTIVE, created.getStatus());

        String workerId = created.getWorkerId();

        // 2. View worker - confirm persisted
        UseCase<ViewWorkerRequest, Worker> viewUC =
                (UseCase<ViewWorkerRequest, Worker>) registry.getUseCase(ToolType.VIEW_WORKER, context);
        Worker viewed = viewUC.execute(new ViewWorkerRequest(workerId));
        assertEquals(workerId, viewed.getWorkerId());
        assertEquals("integration@test.com", viewed.getEmail());

        // 3. Update worker email
        UpdateWorkerRequest updateReq = new UpdateWorkerRequest(workerId, "updated@test.com", null);
        UseCase<UpdateWorkerRequest, Worker> updateUC =
                (UseCase<UpdateWorkerRequest, Worker>) registry.getUseCase(ToolType.UPDATE_WORKER, context);
        Worker updated = updateUC.execute(updateReq);
        assertEquals("updated@test.com", updated.getEmail());

        // 4. Suspend worker
        ChangeStatusRequest suspendReq = new ChangeStatusRequest(workerId, WorkerStatus.SUSPENDED);
        UseCase<ChangeStatusRequest, Worker> statusUC =
                (UseCase<ChangeStatusRequest, Worker>) registry.getUseCase(ToolType.CHANGE_STATUS, context);
        Worker suspended = statusUC.execute(suspendReq);
        assertEquals(WorkerStatus.SUSPENDED, suspended.getStatus());

        // 5. Verify suspended worker is invalid
        VerifyWorkerRequest verifyReq = new VerifyWorkerRequest(workerId, ToolType.VERIFY_BASIC);
        UseCase<VerifyWorkerRequest, VerificationResult> verifyUC =
                (UseCase<VerifyWorkerRequest, VerificationResult>) registry.getUseCase(ToolType.VERIFY_BASIC, context);
        VerificationResult result = verifyUC.execute(verifyReq);
        assertFalse(result.isValid());

        // 6. Reactivate worker
        Worker reactivated = statusUC.execute(new ChangeStatusRequest(workerId, WorkerStatus.ACTIVE));
        assertEquals(WorkerStatus.ACTIVE, reactivated.getStatus());

        // 7. Revoke worker
        Worker revoked = statusUC.execute(new ChangeStatusRequest(workerId, WorkerStatus.REVOKED));
        assertEquals(WorkerStatus.REVOKED, revoked.getStatus());

        // 8. Cannot reactivate a revoked worker
        assertThrows(InvalidOperationException.class,
                () -> statusUC.execute(new ChangeStatusRequest(workerId, WorkerStatus.ACTIVE)));

        // 9. Audit log should have recorded all actions
        UseCase<AuditLogRequest, List> auditUC =
                (UseCase<AuditLogRequest, List>) registry.getUseCase(ToolType.VIEW_AUDIT_LOG, context);
        List<String> auditEntries = auditUC.execute(new AuditLogRequest(workerId, null));
        assertTrue(auditEntries.size() >= 6, "Expected at least 6 audit entries for the lifecycle");
    }

    @Test
    @SuppressWarnings("unchecked")
    void certificationWorkflow() {
        // Create a worker first
        CreateWorkerRequest createReq = new CreateWorkerRequest(
                "Cert Test Worker", LocalDate.of(2001, 3, 14),
                "cert@test.com", Region.UNITED_STATES);
        UseCase<CreateWorkerRequest, Worker> createUC =
                (UseCase<CreateWorkerRequest, Worker>) registry.getUseCase(ToolType.CREATE_WORKER, context);
        Worker worker = createUC.execute(createReq);

        // Add a certification
        AddCertificationRequest addCertReq = new AddCertificationRequest(
                worker.getWorkerId(), CertificationType.US_FOOD_HANDLER,
                "State Health Department", "FH-INTEG-001",
                LocalDate.of(2025, 1, 1), LocalDate.of(2028, 1, 1));
        UseCase<AddCertificationRequest, Certification> addCertUC =
                (UseCase<AddCertificationRequest, Certification>) registry.getUseCase(ToolType.ADD_CERTIFICATION, context);
        Certification cert = addCertUC.execute(addCertReq);

        assertNotNull(cert.getId());
        assertEquals(CertificationType.US_FOOD_HANDLER, cert.getType());
        assertEquals(worker.getWorkerId(), cert.getWorkerId());

        // Verify with cert history shows the certification
        VerifyWorkerRequest verifyReq = new VerifyWorkerRequest(worker.getWorkerId(), ToolType.VERIFY_WITH_CERT_HISTORY);
        UseCase<VerifyWorkerRequest, VerificationResult> verifyUC =
                (UseCase<VerifyWorkerRequest, VerificationResult>) registry.getUseCase(ToolType.VERIFY_WITH_CERT_HISTORY, context);
        VerificationResult result = verifyUC.execute(verifyReq);

        assertTrue(result.isValid());
        assertNotNull(result.getCertifications());
        assertFalse(result.getCertifications().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchAndFilterWorkers() {
        UseCase<CreateWorkerRequest, Worker> createUC =
                (UseCase<CreateWorkerRequest, Worker>) registry.getUseCase(ToolType.CREATE_WORKER, context);

        // Create workers in different regions
        createUC.execute(new CreateWorkerRequest("US Worker One", LocalDate.of(2000, 1, 1),
                "us1@test.com", Region.UNITED_STATES));
        createUC.execute(new CreateWorkerRequest("US Worker Two", LocalDate.of(2000, 2, 2),
                "us2@test.com", Region.UNITED_STATES));
        createUC.execute(new CreateWorkerRequest("UK Worker One", LocalDate.of(2000, 3, 3),
                "uk1@test.com", Region.UNITED_KINGDOM));

        // Search by region
        UseCase<SearchWorkersRequest, List> searchUC =
                (UseCase<SearchWorkersRequest, List>) registry.getUseCase(ToolType.SEARCH_WORKERS, context);
        List<Worker> usWorkers = searchUC.execute(new SearchWorkersRequest(null, Region.UNITED_STATES, null));

        // DataSeeder adds 10 workers, some of which are US-based, plus our 2
        assertTrue(usWorkers.size() >= 2);

        // Search by name
        List<Worker> namedWorkers = searchUC.execute(new SearchWorkersRequest("UK Worker", null, null));
        assertEquals(1, namedWorkers.size());
        assertEquals("UK Worker One", namedWorkers.get(0).getFullName());
    }
}
