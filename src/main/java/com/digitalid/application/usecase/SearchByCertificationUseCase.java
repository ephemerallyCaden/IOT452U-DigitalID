package com.digitalid.application.usecase;

import java.util.List;
import java.util.stream.Collectors;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.SearchByCertificationRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Worker;


public class SearchByCertificationUseCase implements UseCase<SearchByCertificationRequest, List<Worker>> {

    private final OrganisationContext org;
    private final CertificationRepository certRepository;
    private final WorkerRepository workerRepository;
    private final AuditService logger;

    public SearchByCertificationUseCase (
        OrganisationContext org,
        CertificationRepository certRepository,
        WorkerRepository workerRepository,
        AuditService logger
) {
        this.org = org;
        this.certRepository = certRepository;
        this.workerRepository = workerRepository;
        this.logger = logger;
    }

    public List<Worker> execute(SearchByCertificationRequest request) {

        List<Certification> certs = certRepository.findByType(request.getCertificationType());

        // Get unique worker IDs from matching certs
        List<String> workerIds = certs.stream()
                .map(Certification::getWorkerId)
                .distinct()
                .collect(Collectors.toList());

        // Look up each worker
        List<Worker> workers = workerIds.stream()
                .map(workerRepository::findById)
                .collect(Collectors.toList());

        // Logging
        logger.log("SEARCH_BY_CERTIFICATION", request.getCertificationType().name(),
                "Worker", org, "Found " + workers.size() + " worker(s)");

        return workers;

    }

}
