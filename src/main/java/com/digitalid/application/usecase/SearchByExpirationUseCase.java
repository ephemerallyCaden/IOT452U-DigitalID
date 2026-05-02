package com.digitalid.application.usecase;

import java.util.List;
import java.util.stream.Collectors;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.SearchByExpirationRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Worker;


public class SearchByExpirationUseCase implements UseCase<SearchByExpirationRequest, List<Worker>> {

    private final OrganisationContext org;
    private final CertificationRepository certRepository;
    private final WorkerRepository workerRepository;
    private final AuditService logger;

    public SearchByExpirationUseCase (
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

    public List<Worker> execute(SearchByExpirationRequest request) {

        int withinDays = request.getWithinDays();
        List<Certification> expiring = certRepository.findExpiringSoon(withinDays);

        // Get unique workers with expiring certs
        List<String> workerIds = expiring.stream()
                .map(Certification::getWorkerId)
                .distinct()
                .collect(Collectors.toList());

        List<Worker> workers = workerIds.stream()
                .map(workerRepository::findById)
                .collect(Collectors.toList());

        // Logging
        logger.log("SEARCH_BY_EXPIRATION", "ALL", "Worker", org,
                workers.size() + " worker(s) with certs expiring within " + withinDays + " days");

        return workers;

    }

}
