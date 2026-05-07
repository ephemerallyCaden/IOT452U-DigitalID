package com.digitalid.application.usecase;

import java.util.List;

import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.VerificationResult;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.service.VerificationService;


public class VerifyWithCertHistoryUseCase extends AbstractEnhancedVerificationUseCase {

    public VerifyWithCertHistoryUseCase(OrganisationContext org, VerificationService verificationService,
                                        WorkerRepository workerRepository, CertificationRepository certRepository,
                                        AuditService logger) {
        super(org, verificationService, workerRepository, certRepository, logger);
    }

    @Override
    protected VerificationResult verify(Worker worker, List<Certification> certs) {
        return verificationService.verifyWithCertHistory(worker, certs);
    }

    @Override
    protected String getAuditAction() {
        return "VERIFY_WITH_CERT_HISTORY";
    }

}
