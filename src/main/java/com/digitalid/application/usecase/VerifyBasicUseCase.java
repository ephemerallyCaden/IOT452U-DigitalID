package com.digitalid.application.usecase;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.VerifyWorkerRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.VerificationResult;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.service.VerificationService;


public class VerifyBasicUseCase implements UseCase<VerifyWorkerRequest, VerificationResult> {

    private final OrganisationContext org;
    private final VerificationService verificationService;
    private final WorkerRepository repository;
    private final AuditService logger;

    public VerifyBasicUseCase (OrganisationContext org, VerificationService verificationService,
                               WorkerRepository repository, AuditService logger) {
        this.org = org;
        this.verificationService = verificationService;
        this.repository = repository;
        this.logger = logger;
    }

    public VerificationResult execute(VerifyWorkerRequest request) {

        String reqWorkerId = request.getWorkerId();

        Worker worker = repository.findById(reqWorkerId);
        VerificationResult result = verificationService.verifyBasic(worker);

        // Logging
        logger.log("VERIFY_BASIC", reqWorkerId, "Worker", org);

        return result;

    }

}
