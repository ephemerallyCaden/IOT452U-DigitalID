package com.digitalid.application.usecase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.request.AddCertificationRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.exception.ValidationException;
import com.digitalid.domain.model.*;
import com.digitalid.domain.service.CertificationValidationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class AddCertificationUseCaseTest {

    private AddCertificationUseCase useCase;
    private CreateWorkerUseCaseTest.FakeWorkerRepository workerRepo;
    private FakeCertificationRepository certRepo;
    private CreateWorkerUseCaseTest.FakeAuditLogRepository auditRepo;

    @BeforeEach
    void setUp() {
        workerRepo = new CreateWorkerUseCaseTest.FakeWorkerRepository();
        certRepo = new FakeCertificationRepository();
        auditRepo = new CreateWorkerUseCaseTest.FakeAuditLogRepository();
        AuditService auditService = new AuditService(auditRepo);
        CertificationValidationService validator = new CertificationValidationService();
        OrganisationContext context = makeContext();
        useCase = new AddCertificationUseCase(context, validator, certRepo, workerRepo, auditService);

        Worker worker = new Worker("WK-US-1", "Test Worker", LocalDate.of(2000, 1, 1),
                "test@email.com", Region.UNITED_STATES);
        workerRepo.save(worker);
    }

    @Test
    void addsCertificationToWorker() {
        AddCertificationRequest request = new AddCertificationRequest(
                "WK-US-1", CertificationType.US_FOOD_HANDLER, "State Health Dept",
                "FH-12345", LocalDate.of(2025, 1, 1), LocalDate.of(2028, 1, 1));

        Certification result = useCase.execute(request);

        assertNotNull(result.getId());
        assertEquals("WK-US-1", result.getWorkerId());
        assertEquals(CertificationType.US_FOOD_HANDLER, result.getType());
    }

    @Test
    void savesCertificationToRepository() {
        AddCertificationRequest request = new AddCertificationRequest(
                "WK-US-1", CertificationType.US_FOOD_HANDLER, "State Health Dept",
                "FH-99999", LocalDate.of(2025, 3, 15), LocalDate.of(2028, 3, 15));

        useCase.execute(request);

        assertEquals(1, certRepo.savedCerts.size());
    }

    @Test
    void centralAuthorityCanAddAnyCertRegardlessOfWorkerRegion() {
        // UK cert for a US worker is allowed — Central Authority operates globally
        AddCertificationRequest request = new AddCertificationRequest(
                "WK-US-1", CertificationType.UK_LEVEL_2_FOOD_SAFETY, "CIEH",
                "L2-00001", LocalDate.of(2025, 1, 1), LocalDate.of(2028, 1, 1));

        Certification result = useCase.execute(request);
        assertNotNull(result);
        assertEquals(CertificationType.UK_LEVEL_2_FOOD_SAFETY, result.getType());
    }

    @Test
    void rejectsIssueDateAfterExpiration() {
        AddCertificationRequest request = new AddCertificationRequest(
                "WK-US-1", CertificationType.US_FOOD_HANDLER, "State Health Dept",
                "FH-BAD", LocalDate.of(2028, 1, 1), LocalDate.of(2025, 1, 1));

        assertThrows(Exception.class, () -> useCase.execute(request));
    }

    @Test
    void logsAddCertificationToAudit() {
        AddCertificationRequest request = new AddCertificationRequest(
                "WK-US-1", CertificationType.US_FOOD_HANDLER, "State Health Dept",
                "FH-AUDIT", LocalDate.of(2025, 6, 1), LocalDate.of(2028, 6, 1));

        useCase.execute(request);

        assertEquals(1, auditRepo.entries.size());
        assertEquals("ADD_CERTIFICATION", auditRepo.entries.get(0).action);
    }

    private OrganisationContext makeContext() {
        OrganisationProfile profile = OrganisationProfile.forType(OrganisationType.CENTRAL_AUTHORITY);
        return new OrganisationContext("ORG-TEST", OrganisationType.CENTRAL_AUTHORITY,
                "Test Authority", null, profile.getAllowedTools());
    }

    // --- Fake ---

    static class FakeCertificationRepository implements CertificationRepository {
        final List<Certification> savedCerts = new ArrayList<>();

        @Override
        public void save(Certification certification) { savedCerts.add(certification); }

        @Override
        public Certification findById(String certificationId) {
            return savedCerts.stream()
                    .filter(c -> c.getId().equals(certificationId))
                    .findFirst().orElse(null);
        }

        @Override
        public List<Certification> findByWorkerId(String workerId) {
            return savedCerts.stream()
                    .filter(c -> c.getWorkerId().equals(workerId))
                    .collect(java.util.stream.Collectors.toList());
        }

        @Override
        public List<Certification> findByType(CertificationType type) {
            return savedCerts.stream()
                    .filter(c -> c.getType() == type)
                    .collect(java.util.stream.Collectors.toList());
        }

        @Override
        public List<Certification> findExpiringSoon(int withinDays) {
            return savedCerts.stream()
                    .filter(c -> c.getExpirationDate() != null && c.daysUntilExpiration() <= withinDays)
                    .collect(java.util.stream.Collectors.toList());
        }

        @Override
        public void delete(String certificationId) {
            savedCerts.removeIf(c -> c.getId().equals(certificationId));
        }
    }
}
