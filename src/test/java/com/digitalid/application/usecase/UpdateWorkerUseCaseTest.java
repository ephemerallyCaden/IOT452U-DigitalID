package com.digitalid.application.usecase;

import java.time.LocalDate;

import com.digitalid.application.request.UpdateWorkerRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.exception.InvalidOperationException;
import com.digitalid.domain.model.*;
import com.digitalid.domain.service.WorkerValidationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class UpdateWorkerUseCaseTest {

    private UpdateWorkerUseCase useCase;
    private CreateWorkerUseCaseTest.FakeWorkerRepository workerRepo;
    private CreateWorkerUseCaseTest.FakeAuditLogRepository auditRepo;

    @BeforeEach
    void setUp() {
        workerRepo = new CreateWorkerUseCaseTest.FakeWorkerRepository();
        auditRepo = new CreateWorkerUseCaseTest.FakeAuditLogRepository();
        AuditService auditService = new AuditService(auditRepo);
        WorkerValidationService validator = new WorkerValidationService();
        OrganisationContext context = makeContext();
        useCase = new UpdateWorkerUseCase(context, validator, workerRepo, auditService);

        Worker worker = new Worker("WK-US-1", "Original Name", LocalDate.of(2000, 1, 1),
                "original@email.com", Region.UNITED_STATES);
        workerRepo.save(worker);
    }

    @Test
    void updatesEmail() {
        UpdateWorkerRequest request = new UpdateWorkerRequest("WK-US-1", "new@email.com", null);

        Worker result = useCase.execute(request);

        assertEquals("new@email.com", result.getEmail());
    }

    @Test
    void updatesRegion() {
        UpdateWorkerRequest request = new UpdateWorkerRequest("WK-US-1", null, Region.UNITED_KINGDOM);

        Worker result = useCase.execute(request);

        assertEquals(Region.UNITED_KINGDOM, result.getRegion());
    }

    @Test
    void updatesBothEmailAndRegion() {
        UpdateWorkerRequest request = new UpdateWorkerRequest("WK-US-1", "updated@email.com", Region.GERMANY);

        Worker result = useCase.execute(request);

        assertEquals("updated@email.com", result.getEmail());
        assertEquals(Region.GERMANY, result.getRegion());
    }

    @Test
    void rejectsUpdateOnRevokedWorker() {
        Worker worker = workerRepo.findById("WK-US-1");
        worker.changeStatus(WorkerStatus.REVOKED);

        UpdateWorkerRequest request = new UpdateWorkerRequest("WK-US-1", "fail@email.com", null);
        assertThrows(InvalidOperationException.class, () -> useCase.execute(request));
    }

    @Test
    void logsUpdateToAudit() {
        useCase.execute(new UpdateWorkerRequest("WK-US-1", "audit@email.com", null));

        assertEquals(1, auditRepo.entries.size());
        assertEquals("UPDATE_WORKER", auditRepo.entries.get(0).action);
    }

    private OrganisationContext makeContext() {
        OrganisationProfile profile = OrganisationProfile.forType(OrganisationType.CENTRAL_AUTHORITY);
        return new OrganisationContext("ORG-TEST", OrganisationType.CENTRAL_AUTHORITY,
                "Test Authority", profile.getAllowedTools());
    }
}
