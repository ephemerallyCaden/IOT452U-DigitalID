package com.digitalid.application.usecase;

import java.time.LocalDate;

import com.digitalid.application.request.VerifyWorkerRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.*;
import com.digitalid.domain.service.VerificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class VerifyBasicUseCaseTest {

    private VerifyBasicUseCase useCase;
    private CreateWorkerUseCaseTest.FakeWorkerRepository workerRepo;
    private CreateWorkerUseCaseTest.FakeAuditLogRepository auditRepo;

    @BeforeEach
    void setUp() {
        workerRepo = new CreateWorkerUseCaseTest.FakeWorkerRepository();
        auditRepo = new CreateWorkerUseCaseTest.FakeAuditLogRepository();
        AuditService auditService = new AuditService(auditRepo);
        VerificationService verificationService = new VerificationService();
        OrganisationContext context = makeContext();
        useCase = new VerifyBasicUseCase(context, verificationService, workerRepo, auditService);
    }

    @Test
    void verifiesActiveWorkerAsValid() {
        Worker worker = new Worker("WK-US-1", "Active Worker", LocalDate.of(2000, 1, 1),
                "active@email.com", Region.UNITED_STATES);
        workerRepo.save(worker);

        VerifyWorkerRequest request = new VerifyWorkerRequest("WK-US-1", ToolType.VERIFY_BASIC);
        VerificationResult result = useCase.execute(request);

        assertTrue(result.isValid());
    }

    @Test
    void verifiesSuspendedWorkerAsInvalid() {
        Worker worker = new Worker("WK-US-2", "Suspended Worker", LocalDate.of(2000, 2, 2),
                "suspended@email.com", Region.UNITED_STATES);
        worker.changeStatus(WorkerStatus.SUSPENDED);
        workerRepo.save(worker);

        VerifyWorkerRequest request = new VerifyWorkerRequest("WK-US-2", ToolType.VERIFY_BASIC);
        VerificationResult result = useCase.execute(request);

        assertFalse(result.isValid());
    }

    @Test
    void verifiesRevokedWorkerAsInvalid() {
        Worker worker = new Worker("WK-UK-1", "Revoked Worker", LocalDate.of(1998, 6, 15),
                "revoked@email.com", Region.UNITED_KINGDOM);
        worker.changeStatus(WorkerStatus.REVOKED);
        workerRepo.save(worker);

        VerifyWorkerRequest request = new VerifyWorkerRequest("WK-UK-1", ToolType.VERIFY_BASIC);
        VerificationResult result = useCase.execute(request);

        assertFalse(result.isValid());
    }

    @Test
    void logsVerificationToAudit() {
        Worker worker = new Worker("WK-DE-1", "German Worker", LocalDate.of(2001, 3, 3),
                "german@email.com", Region.GERMANY);
        workerRepo.save(worker);

        useCase.execute(new VerifyWorkerRequest("WK-DE-1", ToolType.VERIFY_BASIC));

        assertEquals(1, auditRepo.entries.size());
        assertEquals("VERIFY_BASIC", auditRepo.entries.get(0).action);
        assertEquals("WK-DE-1", auditRepo.entries.get(0).entityId);
    }

    private OrganisationContext makeContext() {
        OrganisationProfile profile = OrganisationProfile.forType(OrganisationType.FINE_DINING);
        return new OrganisationContext("ORG-RESTAURANT", OrganisationType.FINE_DINING,
                "Test Restaurant", Region.UNITED_STATES, profile.getAllowedTools());
    }
}
