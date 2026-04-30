package com.digitalid.domain.service;

import com.digitalid.domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VerificationServiceTest {

    private final VerificationService service = new VerificationService();

    private Worker makeActiveWorker() {
        return new Worker("WK-GB-2024-001", "James Smith",
                LocalDate.of(1990, 5, 20), "james@email.com", Region.UNITED_KINGDOM);
    }

    private Certification makeFoodSafetyCert(boolean valid) {
        LocalDate expiry = valid ? LocalDate.of(2027, 1, 1) : LocalDate.of(2022, 1, 1);
        return new Certification("C-1", "WK-GB-2024-001",
                CertificationType.UK_LEVEL_2_FOOD_SAFETY, "CIEH", "L2-001",
                LocalDate.of(2021, 1, 1), expiry);
    }

    private Certification makeBackgroundCheck(boolean valid) {
        LocalDate expiry = valid ? LocalDate.of(2027, 6, 1) : LocalDate.of(2023, 1, 1);
        return new Certification("C-2", "WK-GB-2024-001",
                CertificationType.UK_DBS_CHECK, "DBS", "DBS-001",
                LocalDate.of(2021, 1, 1), expiry);
    }

    private Certification makePermit(boolean valid) {
        LocalDate expiry = valid ? LocalDate.of(2027, 1, 1) : LocalDate.of(2023, 1, 1);
        return new Certification("C-3", "WK-GB-2024-001",
                CertificationType.UK_STREET_TRADING_LICENCE, "Local Authority", "STL-001",
                LocalDate.of(2021, 1, 1), expiry);
    }

    @Test
    void basicVerificationReflectsWorkerStatus() {
        Worker active = makeActiveWorker();
        assertTrue(service.verifyBasic(active).isValid());

        active.changeStatus(WorkerStatus.SUSPENDED);
        assertFalse(service.verifyBasic(active).isValid());
    }

    @Test
    void certHistoryReturnsAllCerts() {
        Worker w = makeActiveWorker();
        List<Certification> certs = List.of(makeFoodSafetyCert(true), makeFoodSafetyCert(false));
        VerificationResult result = service.verifyWithCertHistory(w, certs);
        assertTrue(result.isValid());
        assertEquals(2, result.getCertifications().size());
    }

    @Test
    void conditionsFailWhenBackgroundCheckExpired() {
        Worker w = makeActiveWorker();
        List<Certification> certs = List.of(makeFoodSafetyCert(true), makeBackgroundCheck(false));
        VerificationResult result = service.verifyWithConditions(w, certs);
        assertFalse(result.isValid());
    }

    @Test
    void permitsNeedAtLeastOneValid() {
        Worker w = makeActiveWorker();
        assertFalse(service.verifyWithPermits(w, List.of(makePermit(false))).isValid());
        assertTrue(service.verifyWithPermits(w, List.of(makePermit(true))).isValid());
    }
}
