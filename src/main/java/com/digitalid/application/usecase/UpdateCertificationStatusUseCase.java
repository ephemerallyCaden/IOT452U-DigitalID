package com.digitalid.application.usecase;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.request.UpdateCertificationStatusRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.Certification;
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
        String reqNewStatus = request.getNewStatus();

        Certification cert = certRepository.findById(reqCertId);

        // Apply the status change
        switch (reqNewStatus) {
            case "SUSPENDED":
                cert.suspend();
                break;
            case "EXPIRED":
                cert.markExpired();
                break;
            case "ACTIVE":
                cert.reactivate();
                break;
            default:
                throw new IllegalArgumentException("Unknown certification status: " + reqNewStatus);
        }

        certRepository.save(cert);

        // Logging
        logger.log("UPDATE_CERTIFICATION_STATUS", reqCertId, "Certification", org);

        return cert;

    }

}
