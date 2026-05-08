package com.digitalid.application.usecase;

import java.time.LocalDate;

import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.request.DeleteWorkerRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.application.service.WorkerLifecycleNotifier;
import com.digitalid.domain.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class DeleteWorkerUseCaseTest {

    private DeleteWorkerUseCase useCase;
    private CreateWorkerUseCaseTest.FakeWorkerRepository workerRepo;
    private CertificationRepository certRepo;
    private CreateWorkerUseCaseTest.FakeAuditLogRepository auditRepo;

    @BeforeEach
    void setUp() {
        workerRepo = new CreateWorkerUseCaseTest.FakeWorkerRepository();
        certRepo = new AddCertificationUseCaseTest.FakeCertificationRepository();
        auditRepo = new CreateWorkerUseCaseTest.FakeAuditLogRepository();
        AuditService auditService = new AuditService(auditRepo);
        WorkerLifecycleNotifier notifier = new WorkerLifecycleNotifier();
        OrganisationContext context = makeContext();
        useCase = new DeleteWorkerUseCase(context, workerRepo, certRepo, auditService, notifier);

        Worker worker = new Worker("WK-UK-1", "Delete Me", LocalDate.of(1999, 5, 5),
                "delete@email.com", Region.UNITED_KINGDOM);
        workerRepo.save(worker);
    }

    @Test
    void deletesExistingWorker() {
        useCase.execute(new DeleteWorkerRequest("WK-UK-1"));

        assertEquals(0, workerRepo.listAll().size());
    }

    @Test
    void logsDeleteToAudit() {
        useCase.execute(new DeleteWorkerRequest("WK-UK-1"));

        assertEquals(1, auditRepo.entries.size());
        assertEquals("DELETE_WORKER", auditRepo.entries.get(0).action);
        assertEquals("WK-UK-1", auditRepo.entries.get(0).entityId);
    }

    @Test
    void deleteIsIdempotentForNonexistentWorker() {
        // Deleting a non-existent worker does not throw - matches repo behaviour
        assertDoesNotThrow(() -> useCase.execute(new DeleteWorkerRequest("WK-NONEXISTENT")));
    }

    private OrganisationContext makeContext() {
        OrganisationProfile profile = OrganisationProfile.forType(OrganisationType.CENTRAL_AUTHORITY);
        return new OrganisationContext("ORG-TEST", OrganisationType.CENTRAL_AUTHORITY,
                "Test Authority", profile.getAllowedTools());
    }
}
