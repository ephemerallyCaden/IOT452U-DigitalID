package com.digitalid.application.usecase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.digitalid.application.port.out.AuditLogRepository;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.CreateWorkerRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.application.service.WorkerLifecycleNotifier;
import com.digitalid.domain.exception.ValidationException;
import com.digitalid.domain.model.*;
import com.digitalid.domain.service.WorkerValidationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class CreateWorkerUseCaseTest {

    private CreateWorkerUseCase useCase;
    private FakeWorkerRepository workerRepo;
    private FakeAuditLogRepository auditRepo;

    @BeforeEach
    void setUp() {
        workerRepo = new FakeWorkerRepository();
        auditRepo = new FakeAuditLogRepository();
        AuditService auditService = new AuditService(auditRepo);
        WorkerValidationService validator = new WorkerValidationService();
        WorkerLifecycleNotifier notifier = new WorkerLifecycleNotifier();
        OrganisationContext context = makeContext();
        useCase = new CreateWorkerUseCase(context, validator, workerRepo, auditService, notifier);
    }

    @Test
    void createsWorkerWithCorrectId() {
        CreateWorkerRequest request = new CreateWorkerRequest(
                "John Smith", LocalDate.of(2000, 1, 15), "john@email.com", Region.UNITED_STATES);

        Worker result = useCase.execute(request);

        assertEquals("WK-US-1", result.getWorkerId());
        assertEquals("John Smith", result.getFullName());
        assertEquals(Region.UNITED_STATES, result.getRegion());
        assertEquals(WorkerStatus.ACTIVE, result.getStatus());
    }

    @Test
    void savesWorkerToRepository() {
        CreateWorkerRequest request = new CreateWorkerRequest(
                "Jane Doe", LocalDate.of(1995, 6, 20), "jane@email.com", Region.UNITED_KINGDOM);

        useCase.execute(request);

        assertEquals(1, workerRepo.savedWorkers.size());
        assertEquals("Jane Doe", workerRepo.savedWorkers.get(0).getFullName());
    }

    @Test
    void logsCreationToAuditTrail() {
        CreateWorkerRequest request = new CreateWorkerRequest(
                "Alice Brown", LocalDate.of(1998, 3, 10), "alice@email.com", Region.GERMANY);

        Worker worker = useCase.execute(request);

        assertEquals(1, auditRepo.entries.size());
        assertEquals("CREATE_WORKER", auditRepo.entries.get(0).action);
        assertEquals(worker.getWorkerId(), auditRepo.entries.get(0).entityId);
    }

    @Test
    void incrementsSequenceNumber() {
        useCase.execute(new CreateWorkerRequest(
                "Worker One", LocalDate.of(2000, 1, 1), "one@email.com", Region.JAPAN));
        useCase.execute(new CreateWorkerRequest(
                "Worker Two", LocalDate.of(2000, 2, 2), "two@email.com", Region.JAPAN));

        assertEquals("WK-JP-1", workerRepo.savedWorkers.get(0).getWorkerId());
        assertEquals("WK-JP-2", workerRepo.savedWorkers.get(1).getWorkerId());
    }

    @Test
    void rejectsBlankName() {
        CreateWorkerRequest request = new CreateWorkerRequest(
                "", LocalDate.of(2000, 1, 1), "test@email.com", Region.FRANCE);

        assertThrows(ValidationException.class, () -> useCase.execute(request));
    }

    @Test
    void rejectsFutureDateOfBirth() {
        CreateWorkerRequest request = new CreateWorkerRequest(
                "Future Person", LocalDate.now().plusDays(1), "future@email.com", Region.SINGAPORE);

        assertThrows(ValidationException.class, () -> useCase.execute(request));
    }

    private OrganisationContext makeContext() {
        OrganisationProfile profile = OrganisationProfile.forType(OrganisationType.CENTRAL_AUTHORITY);
        return new OrganisationContext("ORG-TEST", OrganisationType.CENTRAL_AUTHORITY,
                "Test Authority", null, profile.getAllowedTools());
    }

    // --- Fakes ---

    static class FakeWorkerRepository implements WorkerRepository {
        final List<Worker> savedWorkers = new ArrayList<>();
        private int sequence = 0;

        @Override
        public int nextNum() { return ++sequence; }

        @Override
        public void save(Worker worker) { savedWorkers.add(worker); }

        @Override
        public List<Worker> listAll() { return savedWorkers; }

        @Override
        public void delete(String workerId) {
            savedWorkers.removeIf(w -> w.getWorkerId().equals(workerId));
        }

        @Override
        public Worker findById(String workerId) {
            return savedWorkers.stream()
                    .filter(w -> w.getWorkerId().equals(workerId))
                    .findFirst()
                    .orElseThrow(() -> new com.digitalid.domain.exception.WorkerNotFoundException(workerId));
        }

        @Override
        public List<Worker> findByRegion(Region region) {
            return savedWorkers.stream()
                    .filter(w -> w.getRegion() == region)
                    .collect(java.util.stream.Collectors.toList());
        }

        @Override
        public List<Worker> findByStatus(WorkerStatus status) {
            return savedWorkers.stream()
                    .filter(w -> w.getStatus() == status)
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    static class FakeAuditLogRepository implements AuditLogRepository {
        final List<Entry> entries = new ArrayList<>();

        @Override
        public void save(AuditLogEntry auditEntry) {
            entries.add(new Entry(auditEntry.getAction(), auditEntry.getEntityId(), auditEntry.getEntityType()));
        }

        @Override
        public List<AuditLogEntry> findByEntityId(String entityId) { return List.of(); }

        @Override
        public List<AuditLogEntry> findByOrganisationId(String organisationId) { return List.of(); }

        @Override
        public List<AuditLogEntry> findAll() { return List.of(); }

        static class Entry {
            final String action;
            final String entityId;
            final String entityType;
            Entry(String action, String entityId, String entityType) {
                this.action = action;
                this.entityId = entityId;
                this.entityType = entityType;
            }
        }
    }
}
