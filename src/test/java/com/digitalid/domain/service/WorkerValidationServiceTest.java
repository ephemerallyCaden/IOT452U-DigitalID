package com.digitalid.domain.service;

import com.digitalid.domain.exception.InvalidOperationException;
import com.digitalid.domain.exception.ValidationException;
import com.digitalid.domain.model.Region;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.model.WorkerStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WorkerValidationServiceTest {

    private final WorkerValidationService service = new WorkerValidationService();

    private Worker makeWorker() {
        return new Worker("WK-GB-2024-001", "James Smith",
                LocalDate.of(1990, 5, 20), "james@email.com", Region.UNITED_KINGDOM);
    }

    @Test
    void rejectsBadCreationInput() {
        assertThrows(ValidationException.class, () ->
                service.validateCreation("", LocalDate.of(1990, 1, 1), "a@b.com"));
        assertThrows(ValidationException.class, () ->
                service.validateCreation("Name", LocalDate.now().plusDays(1), "a@b.com"));
        assertThrows(ValidationException.class, () ->
                service.validateCreation("Name", LocalDate.of(1990, 1, 1), "not-an-email"));
    }

    @Test
    void revokedWorkerCannotChangeStatusOrBeUpdated() {
        Worker w = makeWorker();
        w.changeStatus(WorkerStatus.REVOKED);
        assertThrows(InvalidOperationException.class, () ->
                service.validateStatusChange(w, WorkerStatus.ACTIVE));
        assertThrows(InvalidOperationException.class, () -> service.validateUpdate(w));
    }

    @Test
    void activeWorkerCanBeSuspended() {
        Worker w = makeWorker();
        assertDoesNotThrow(() -> service.validateStatusChange(w, WorkerStatus.SUSPENDED));
    }
}
