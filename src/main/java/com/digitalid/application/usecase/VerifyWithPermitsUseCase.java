package com.digitalid.application.usecase;

import java.util.List;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.VerifyWorkerRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.VerificationResult;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.service.VerificationService;


public class VerifyWithPermitsUseCase implements UseCase<VerifyWorkerRequest, VerificationResult> {

    private final OrganisationContext org;
    private final VerificationService verificationService;
    private final WorkerRepository workerRepository;
    private final CertificationRepository certRepository;
    private final AuditService logger;

    public VerifyWithPermitsUseCase (
        OrganisationContext org,
        VerificationService verificationService,
        WorkerRepository workerRepository,
        CertificationRepository certRepository,
        AuditService logger
    ) {
        this.org = org;
        this.verificationService = verificationService;
        this.workerRepository = workerRepository;
        this.certRepository = certRepository;
        this.logger = logger;
    }

    public VerificationResult execute(VerifyWorkerRequest request) {

        String reqWorkerId = request.getWorkerId();

        Worker worker = workerRepository.findById(reqWorkerId);
        List<Certification> certs = certRepository.findByWorkerId(reqWorkerId);

        VerificationResult result = verificationService.verifyWithPermits(worker, certs);

        // Logging
        logger.log("VERIFY_WITH_PERMITS", reqWorkerId, "Worker", org);

        return result;

    }

}
