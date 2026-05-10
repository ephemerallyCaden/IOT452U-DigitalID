package com.digitalid.application.usecase;

import java.time.LocalDate;

import com.digitalid.application.request.ChangeStatusRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.application.service.WorkerLifecycleNotifier;
import com.digitalid.domain.exception.InvalidOperationException;
import com.digitalid.domain.model.*;
import com.digitalid.domain.service.WorkerValidationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ChangeStatusUseCaseTest {

    private ChangeStatusUseCase useCase;
    private CreateWorkerUseCaseTest.FakeWorkerRepository workerRepo;
    private CreateWorkerUseCaseTest.FakeAuditLogRepository auditRepo;

    @BeforeEach
    void setUp() {
        workerRepo = new CreateWorkerUseCaseTest.FakeWorkerRepository();
        auditRepo = new CreateWorkerUseCaseTest.FakeAuditLogRepository();
        AuditService auditService = new AuditService(auditRepo);
        WorkerValidationService validator = new WorkerValidationService();
        WorkerLifecycleNotifier notifier = new WorkerLifecycleNotifier();
        OrganisationContext context = makeContext();
        useCase = new ChangeStatusUseCase(context, validator, workerRepo, auditService, notifier);

        // Seed a worker
        Worker worker = new Worker("WK-US-1", "Test Worker", LocalDate.of(2000, 1, 1),
                "test@email.com", Region.UNITED_STATES);
        workerRepo.save(worker);
    }

    @Test
    void suspendsActiveWorker() {
        ChangeStatusRequest request = new ChangeStatusRequest("WK-US-1", WorkerStatus.SUSPENDED);

        Worker result = useCase.execute(request);

        assertEquals(WorkerStatus.SUSPENDED, result.getStatus());
    }

    @Test
    void revokesActiveWorker() {
        ChangeStatusRequest request = new ChangeStatusRequest("WK-US-1", WorkerStatus.REVOKED);

        Worker result = useCase.execute(request);

        assertEquals(WorkerStatus.REVOKED, result.getStatus());
    }

    @Test
    void reactivatesSuspendedWorker() {
        // First suspend
        useCase.execute(new ChangeStatusRequest("WK-US-1", WorkerStatus.SUSPENDED));
        // Then reactivate
        Worker result = useCase.execute(new ChangeStatusRequest("WK-US-1", WorkerStatus.ACTIVE));

        assertEquals(WorkerStatus.ACTIVE, result.getStatus());
    }

    @Test
    void rejectsReactivatingRevokedWorker() {
        // First revoke
        useCase.execute(new ChangeStatusRequest("WK-US-1", WorkerStatus.REVOKED));

        // Attempting to reactivate should fail
        ChangeStatusRequest request = new ChangeStatusRequest("WK-US-1", WorkerStatus.ACTIVE);
        assertThrows(InvalidOperationException.class, () -> useCase.execute(request));
    }

    @Test
    void logsStatusChangeToAudit() {
        useCase.execute(new ChangeStatusRequest("WK-US-1", WorkerStatus.SUSPENDED));

        assertEquals(1, auditRepo.entries.size());
        assertEquals("CHANGE_STATUS", auditRepo.entries.get(0).action);
        assertEquals("WK-US-1", auditRepo.entries.get(0).entityId);
    }

    private OrganisationContext makeContext() {
        OrganisationProfile profile = OrganisationProfile.forType(OrganisationType.CENTRAL_AUTHORITY);
        return new OrganisationContext("ORG-TEST", OrganisationType.CENTRAL_AUTHORITY,
                "Test Authority", null, profile.getAllowedTools());
    }
}
