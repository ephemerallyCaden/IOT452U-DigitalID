package com.digitalid.application.usecase;

import java.time.LocalDate;
import java.util.List;

import com.digitalid.application.request.SearchWorkersRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class SearchWorkersUseCaseTest {

    private SearchWorkersUseCase useCase;
    private CreateWorkerUseCaseTest.FakeWorkerRepository workerRepo;

    @BeforeEach
    void setUp() {
        workerRepo = new CreateWorkerUseCaseTest.FakeWorkerRepository();
        CreateWorkerUseCaseTest.FakeAuditLogRepository auditRepo = new CreateWorkerUseCaseTest.FakeAuditLogRepository();
        AuditService auditService = new AuditService(auditRepo);
        OrganisationContext context = makeContext();
        useCase = new SearchWorkersUseCase(context, workerRepo, auditService);

        // Seed workers
        workerRepo.save(new Worker("WK-US-1", "John Smith", LocalDate.of(2000, 1, 1),
                "john@email.com", Region.UNITED_STATES));
        workerRepo.save(new Worker("WK-UK-2", "Jane Smith", LocalDate.of(1998, 5, 10),
                "jane@email.com", Region.UNITED_KINGDOM));
        workerRepo.save(new Worker("WK-US-3", "Alice Johnson", LocalDate.of(1995, 3, 20),
                "alice@email.com", Region.UNITED_STATES));

        // Suspend one worker
        workerRepo.findById("WK-UK-2").changeStatus(WorkerStatus.SUSPENDED);
    }

    @Test
    void returnsAllWorkersWithNoFilters() {
        SearchWorkersRequest request = new SearchWorkersRequest(null, null, null);

        List<Worker> results = useCase.execute(request);

        assertEquals(3, results.size());
    }

    @Test
    void filtersByRegion() {
        SearchWorkersRequest request = new SearchWorkersRequest(null, Region.UNITED_STATES, null);

        List<Worker> results = useCase.execute(request);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(w -> w.getRegion() == Region.UNITED_STATES));
    }

    @Test
    void filtersByStatus() {
        SearchWorkersRequest request = new SearchWorkersRequest(null, null, WorkerStatus.SUSPENDED);

        List<Worker> results = useCase.execute(request);

        assertEquals(1, results.size());
        assertEquals("WK-UK-2", results.get(0).getWorkerId());
    }

    @Test
    void filtersByName() {
        SearchWorkersRequest request = new SearchWorkersRequest("smith", null, null);

        List<Worker> results = useCase.execute(request);

        assertEquals(2, results.size());
    }

    @Test
    void combinesMultipleFilters() {
        SearchWorkersRequest request = new SearchWorkersRequest("smith", Region.UNITED_STATES, null);

        List<Worker> results = useCase.execute(request);

        assertEquals(1, results.size());
        assertEquals("John Smith", results.get(0).getFullName());
    }

    @Test
    void returnsEmptyListWhenNoMatch() {
        SearchWorkersRequest request = new SearchWorkersRequest("zzzzz", null, null);

        List<Worker> results = useCase.execute(request);

        assertTrue(results.isEmpty());
    }

    private OrganisationContext makeContext() {
        OrganisationProfile profile = OrganisationProfile.forType(OrganisationType.CENTRAL_AUTHORITY);
        return new OrganisationContext("ORG-TEST", OrganisationType.CENTRAL_AUTHORITY,
                "Test Authority", profile.getAllowedTools());
    }
}
