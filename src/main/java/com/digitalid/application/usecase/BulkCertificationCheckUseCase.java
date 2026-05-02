package com.digitalid.application.usecase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.request.BulkCertificationCheckRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.OrganisationContext;


public class BulkCertificationCheckUseCase implements UseCase<BulkCertificationCheckRequest, Map<String, List<Certification>>> {

    private final OrganisationContext org;
    private final CertificationRepository certRepository;
    private final AuditService logger;

    public BulkCertificationCheckUseCase (OrganisationContext org, CertificationRepository certRepository,
                                          AuditService logger) {
        this.org = org;
        this.certRepository = certRepository;
        this.logger = logger;
    }

    public Map<String, List<Certification>> execute(BulkCertificationCheckRequest request) {

        Map<String, List<Certification>> results = new HashMap<>();

        for (String workerId : request.getWorkerIds()) {
            List<Certification> certs = certRepository.findByWorkerId(workerId);
            results.put(workerId, certs);
        }

        // Logging
        logger.log("BULK_CERTIFICATION_CHECK", "BATCH", "Certification", org,
                "Checked " + results.size() + " worker(s)");

        return results;

    }

}
