package com.digitalid.application.usecase;

import java.time.LocalDate;

import com.digitalid.application.request.ViewWorkerRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.exception.WorkerNotFoundException;
import com.digitalid.domain.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ViewWorkerUseCaseTest {

    private ViewWorkerUseCase useCase;
    private CreateWorkerUseCaseTest.FakeWorkerRepository workerRepo;
    private CreateWorkerUseCaseTest.FakeAuditLogRepository auditRepo;

    @BeforeEach
    void setUp() {
        workerRepo = new CreateWorkerUseCaseTest.FakeWorkerRepository();
        auditRepo = new CreateWorkerUseCaseTest.FakeAuditLogRepository();
        AuditService auditService = new AuditService(auditRepo);
        OrganisationContext context = makeContext();
        useCase = new ViewWorkerUseCase(context, workerRepo, auditService);

        Worker worker = new Worker("WK-UK-1", "Viewable Worker", LocalDate.of(1995, 8, 20),
                "view@email.com", Region.UNITED_KINGDOM);
        workerRepo.save(worker);
    }

    @Test
    void returnsWorkerById() {
        ViewWorkerRequest request = new ViewWorkerRequest("WK-UK-1");

        Worker result = useCase.execute(request);

        assertEquals("WK-UK-1", result.getWorkerId());
        assertEquals("Viewable Worker", result.getFullName());
        assertEquals(Region.UNITED_KINGDOM, result.getRegion());
    }

    @Test
    void throwsWhenWorkerNotFound() {
        ViewWorkerRequest request = new ViewWorkerRequest("WK-NONEXISTENT");

        assertThrows(WorkerNotFoundException.class, () -> useCase.execute(request));
    }

    @Test
    void logsViewToAudit() {
        useCase.execute(new ViewWorkerRequest("WK-UK-1"));

        assertEquals(1, auditRepo.entries.size());
        assertEquals("VIEW_WORKER", auditRepo.entries.get(0).action);
        assertEquals("WK-UK-1", auditRepo.entries.get(0).entityId);
    }

    private OrganisationContext makeContext() {
        OrganisationProfile profile = OrganisationProfile.forType(OrganisationType.FINE_DINING);
        return new OrganisationContext("ORG-RESTAURANT", OrganisationType.FINE_DINING,
                "Test Restaurant", profile.getAllowedTools());
    }
}
