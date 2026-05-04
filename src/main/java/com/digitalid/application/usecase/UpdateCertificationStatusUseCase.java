package com.digitalid.application.usecase;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.request.UpdateCertificationStatusRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.exception.ValidationException;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.CertificationStatus;
import com.digitalid.domain.model.OrganisationContext;


public class UpdateCertificationStatusUseCase implements UseCase<UpdateCertificationStatusRequest, Certification> {

    private final OrganisationContext org;
    private final CertificationRepository certRepository;
    private final AuditService logger;

    public UpdateCertificationStatusUseCase (
        OrganisationContext org,
        CertificationRepository certRepository,
        AuditService logger
    ) {
        this.org = org;
        this.certRepository = certRepository;
        this.logger = logger;
    }

    public Certification execute(UpdateCertificationStatusRequest request) {

        String reqCertId = request.getCertificationId();
        CertificationStatus newStatus = parseStatus(request.getNewStatus());

        Certification cert = certRepository.findById(reqCertId);

        switch (newStatus) {
            case SUSPENDED:
                cert.suspend();
                break;
            case EXPIRED:
                cert.markExpired();
                break;
            case ACTIVE:
                cert.reactivate();
                break;
        }

        certRepository.save(cert);

        // Logging
        logger.log("UPDATE_CERTIFICATION_STATUS", reqCertId, "Certification", org);

        return cert;

    }

    private CertificationStatus parseStatus(String status) {
        try {
            return CertificationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown certification status: " + status);
        }
    }

}
