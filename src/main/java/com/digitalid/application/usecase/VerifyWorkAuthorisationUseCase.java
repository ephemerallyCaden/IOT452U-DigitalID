package com.digitalid.application.usecase;

import java.util.List;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.WorkAuthorisationRepository;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.VerifyWorkerRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.VerificationResult;
import com.digitalid.domain.model.WorkAuthorisation;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.service.VerificationService;


public class VerifyWorkAuthorisationUseCase implements UseCase<VerifyWorkerRequest, VerificationResult> {

    private final OrganisationContext org;
    private final VerificationService verificationService;
    private final WorkerRepository workerRepository;
    private final WorkAuthorisationRepository workAuthRepository;
    private final AuditService logger;

    public VerifyWorkAuthorisationUseCase(
        OrganisationContext org,
        VerificationService verificationService,
        WorkerRepository workerRepository,
        WorkAuthorisationRepository workAuthRepository,
        AuditService logger
    ) {
        this.org = org;
        this.verificationService = verificationService;
        this.workerRepository = workerRepository;
        this.workAuthRepository = workAuthRepository;
        this.logger = logger;
    }

    public VerificationResult execute(VerifyWorkerRequest request) {

        String reqWorkerId = request.getWorkerId();

        Worker worker = workerRepository.findById(reqWorkerId);
        List<WorkAuthorisation> authorisations = workAuthRepository.findByWorkerId(reqWorkerId);

        VerificationResult result = verificationService.verifyWorkAuthorisation(worker, authorisations);

        // Logging
        logger.log("VERIFY_WORK_AUTHORISATION", reqWorkerId, "Worker", org);

        return result;

    }

}
