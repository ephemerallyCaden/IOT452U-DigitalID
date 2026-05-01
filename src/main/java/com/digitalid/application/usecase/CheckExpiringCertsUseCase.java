package com.digitalid.application.usecase;

import java.util.List;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.request.CheckExpiringCertsRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.OrganisationContext;


public class CheckExpiringCertsUseCase implements UseCase<CheckExpiringCertsRequest, List<Certification>> {

    private final OrganisationContext org;
    private final CertificationRepository certRepository;
    private final AuditService logger;

    public CheckExpiringCertsUseCase (
        OrganisationContext org,
        CertificationRepository certRepository,
        AuditService logger
    ) {
        this.org = org;
        this.certRepository = certRepository;
        this.logger = logger;
    }

    public List<Certification> execute(CheckExpiringCertsRequest request) {

        int withinDays = request.getWithinDays();

        List<Certification> expiring = certRepository.findExpiringSoon(withinDays);

        // Logging
        logger.log("CHECK_EXPIRING_CERTS", "ALL", "Certification", org,
                expiring.size() + " cert(s) expiring within " + withinDays + " days");

        return expiring;

    }

}
