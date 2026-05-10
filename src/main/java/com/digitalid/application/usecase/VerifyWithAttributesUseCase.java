package com.digitalid.application.usecase;

import java.util.List;

import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.VerificationResult;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.service.CertificationValidationService;
import com.digitalid.domain.service.VerificationService;


public class VerifyWithAttributesUseCase extends AbstractEnhancedVerificationUseCase {

    public VerifyWithAttributesUseCase(OrganisationContext org, VerificationService verificationService,
                                       CertificationValidationService certValidationService,
                                       WorkerRepository workerRepository, CertificationRepository certRepository,
                                       AuditService logger) {
        super(org, verificationService, certValidationService, workerRepository, certRepository, logger);
    }

    @Override
    protected VerificationResult verify(Worker worker, List<Certification> certs) {
        return verificationService.verifyWithAttributes(worker, certs);
    }

    @Override
    protected String getAuditAction() {
        return "VERIFY_WITH_ATTRIBUTES";
    }

}
