package com.digitalid.domain.model;

import com.digitalid.domain.exception.InvalidOperationException;
import com.digitalid.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WorkerTest {

    private Worker makeWorker() {
        return new Worker("WK-US-2024-001", "Maria Rodriguez",
                LocalDate.of(1995, 6, 15), "maria@email.com", Region.UNITED_STATES);
    }

    @Test
    void newWorkerIsActive() {
        Worker w = makeWorker();
        assertTrue(w.isActive());
        assertTrue(w.canBeUpdated());
    }

    @Test
    void canSuspendActiveWorker() {
        Worker w = makeWorker();
        w.changeStatus(WorkerStatus.SUSPENDED);
        assertFalse(w.isActive());
        assertEquals(WorkerStatus.SUSPENDED, w.getStatus());
    }

    @Test
    void canReactivateSuspendedWorker() {
        Worker w = makeWorker();
        w.changeStatus(WorkerStatus.SUSPENDED);
        w.changeStatus(WorkerStatus.ACTIVE);
        assertTrue(w.isActive());
    }

    @Test
    void revokedWorkerCannotBeUpdated() {
        Worker w = makeWorker();
        w.changeStatus(WorkerStatus.REVOKED);
        assertFalse(w.canBeUpdated());
        assertThrows(InvalidOperationException.class, () -> w.updateEmail("new@email.com"));
    }

    @Test
    void cannotTransitionFromRevokedToActive() {
        Worker w = makeWorker();
        w.changeStatus(WorkerStatus.REVOKED);
        assertThrows(InvalidOperationException.class, () -> w.changeStatus(WorkerStatus.ACTIVE));
    }

    @Test
    void rejectsBlankName() {
        assertThrows(ValidationException.class, () ->
                new Worker("WK-US-2024-002", "", LocalDate.of(1990, 1, 1),
                        "test@test.com", Region.UNITED_STATES));
    }

    @Test
    void rejectsInvalidEmail() {
        assertThrows(ValidationException.class, () ->
                new Worker("WK-US-2024-002", "Test Person", LocalDate.of(1990, 1, 1),
                        "not-an-email", Region.UNITED_STATES));
    }

    @Test
    void rejectsFutureDateOfBirth() {
        assertThrows(ValidationException.class, () ->
                new Worker("WK-US-2024-002", "Test Person", LocalDate.now().plusDays(1),
                        "test@test.com", Region.UNITED_STATES));
    }

    @Test
    void hasCertificationForRegionChecksValidOnly() {
        Worker w = makeWorker();
        Certification validCert = new Certification("C-1", w.getWorkerId(),
                CertificationType.US_FOOD_HANDLER, "State Health Dept", "FH-001",
                LocalDate.of(2024, 1, 1), LocalDate.of(2027, 1, 1));
        w.addCertification(validCert);
        assertTrue(w.hasCertificationForRegion(Region.UNITED_STATES));
        assertFalse(w.hasCertificationForRegion(Region.UNITED_KINGDOM));
    }

    @Test
    void expiredCertDoesNotCountForRegion() {
        Worker w = makeWorker();
        Certification expired = new Certification("C-2", w.getWorkerId(),
                CertificationType.US_FOOD_HANDLER, "State Health Dept", "FH-002",
                LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1));
        w.addCertification(expired);
        assertFalse(w.hasCertificationForRegion(Region.UNITED_STATES));
    }

    @Test
    void updateEmailChangesValue() {
        Worker w = makeWorker();
        w.updateEmail("new@email.com");
        assertEquals("new@email.com", w.getEmail());
    }
}
