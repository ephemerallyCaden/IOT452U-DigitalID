package com.digitalid.application.usecase;

import java.util.UUID;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.AddCertificationRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.service.CertificationValidationService;


public class AddCertificationUseCase implements UseCase<AddCertificationRequest, Certification> {

    private final OrganisationContext org;
    private final CertificationValidationService validator;
    private final CertificationRepository certRepository;
    private final WorkerRepository workerRepository;
    private final AuditService logger;

    public AddCertificationUseCase (
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

    public Certification execute(AddCertificationRequest request) {

        String reqWorkerId = request.getWorkerId();

        // Check worker exists
        Worker worker = workerRepository.findById(reqWorkerId);

        // Validate cert type matches worker region
        validator.validateCertificationForRegion(request.getCertificationType(), worker.getRegion());

        // Create the certification
        String certId = "CERT-" + UUID.randomUUID().toString().substring(0, 8);
        Certification cert = new Certification(certId, reqWorkerId, request.getCertificationType(),
                request.getIssuingAuthority(), request.getCertificationNumber(),
                request.getIssueDate(), request.getExpirationDate());

        validator.validateCertification(cert);

        // Save and link to worker
        certRepository.save(cert);
        worker.addCertification(cert);
        workerRepository.save(worker);

        // Logging
        logger.log("ADD_CERTIFICATION", certId, "Certification", org);

        return cert;

    }

}
