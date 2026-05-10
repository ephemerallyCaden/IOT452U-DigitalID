package com.digitalid.application.usecase;

import java.util.List;
import java.util.stream.Collectors;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.VerifyWorkerRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Region;
import com.digitalid.domain.model.VerificationResult;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.service.CertificationValidationService;
import com.digitalid.domain.service.VerificationService;


public abstract class AbstractEnhancedVerificationUseCase implements UseCase<VerifyWorkerRequest, VerificationResult> {

    protected final OrganisationContext org;
    protected final VerificationService verificationService;
    protected final CertificationValidationService certValidationService;
    protected final WorkerRepository workerRepository;
    protected final CertificationRepository certRepository;
    protected final AuditService logger;

    protected AbstractEnhancedVerificationUseCase(
            OrganisationContext org,
            VerificationService verificationService,
            CertificationValidationService certValidationService,
            WorkerRepository workerRepository,
            CertificationRepository certRepository,
            AuditService logger) {
        this.org = org;
        this.verificationService = verificationService;
        this.certValidationService = certValidationService;
        this.workerRepository = workerRepository;
        this.certRepository = certRepository;
        this.logger = logger;
    }

    @Override
    public VerificationResult execute(VerifyWorkerRequest request) {
        String reqWorkerId = request.getWorkerId();

        Worker worker = workerRepository.findById(reqWorkerId);
        List<Certification> allCerts = certRepository.findByWorkerId(reqWorkerId);

        // Filter certifications to those relevant to the requesting org's region
        Region operatingRegion = org.getOperatingRegion();
        List<Certification> relevantCerts = allCerts.stream()
                .filter(c -> certValidationService.isCertRelevantForRegion(c.getType(), operatingRegion))
                .collect(Collectors.toList());

        VerificationResult result = verify(worker, relevantCerts);

        logger.log(getAuditAction(), reqWorkerId, "Worker", org);

        return result;
    }

    protected abstract VerificationResult verify(Worker worker, List<Certification> certs);

    protected abstract String getAuditAction();
}
