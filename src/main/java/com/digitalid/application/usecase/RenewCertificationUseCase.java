package com.digitalid.application.usecase;

import java.util.UUID;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.RenewCertificationRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.exception.ValidationException;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.service.CertificationValidationService;


public class RenewCertificationUseCase implements UseCase<RenewCertificationRequest, Certification> {

    private final OrganisationContext org;
    private final CertificationValidationService validator;
    private final CertificationRepository certRepository;
    private final WorkerRepository workerRepository;
    private final AuditService logger;

    public RenewCertificationUseCase (
        OrganisationContext org,
        CertificationValidationService validator,
        CertificationRepository certRepository,
        WorkerRepository workerRepository,
        AuditService logger
    ) {
        this.org = org;
        this.validator = validator;
        this.certRepository = certRepository;
        this.workerRepository = workerRepository;
        this.logger = logger;
    }

    public Certification execute(RenewCertificationRequest request) {

        String reqWorkerId = request.getWorkerId();
        String reqCertId = request.getCertificationId();

        // Verify worker exists
        workerRepository.findById(reqWorkerId);

        // Find existing cert and validate ownership
        Certification existing = certRepository.findById(reqCertId);
        if (!existing.getWorkerId().equals(reqWorkerId)) {
            throw new ValidationException(
                    "Certification " + reqCertId + " does not belong to worker " + reqWorkerId);
        }

        // Validate renewal eligibility
        validator.validateRenewal(existing);

        // Mark old cert as expired
        existing.markExpired();
        certRepository.save(existing);

        // Create renewed cert
        String newCertId = "CERT-" + UUID.randomUUID().toString().substring(0, 8);
        Certification renewed = new Certification(newCertId, reqWorkerId, existing.getType(),
                existing.getIssuingAuthority(), existing.getCertificationNumber(),
                request.getNewIssueDate(), request.getNewExpirationDate());

        certRepository.save(renewed);

        // Logging
        logger.log("RENEW_CERTIFICATION", newCertId, "Certification", org);

        return renewed;

    }

}
