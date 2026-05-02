package com.digitalid.application.usecase;

import java.util.List;
import java.util.stream.Collectors;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.SearchWorkersRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Worker;


public class SearchWorkersUseCase implements UseCase<SearchWorkersRequest, List<Worker>> {

    private final OrganisationContext org;
    private final WorkerRepository workerRepository;
    private final AuditService logger;

    public SearchWorkersUseCase (
        OrganisationContext org,
        WorkerRepository workerRepository,
        AuditService logger
    ) {
        this.org = org;
        this.workerRepository = workerRepository;
        this.logger = logger;
    }

    public List<Worker> execute(SearchWorkersRequest request) {

        List<Worker> results = workerRepository.listAll();

        // Filter by region if provided
        if (request.getRegion() != null) {
            results = results.stream()
                    .filter(w -> w.getRegion() == request.getRegion())
                    .collect(Collectors.toList());
        }

        // Filter by status if provided
        if (request.getStatus() != null) {
            results = results.stream()
                    .filter(w -> w.getStatus() == request.getStatus())
                    .collect(Collectors.toList());
        }

        // Filter by name if provided
        if (request.getNameQuery() != null && !request.getNameQuery().isEmpty()) {
            String query = request.getNameQuery().toLowerCase();
            results = results.stream()
                    .filter(w -> w.getFullName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        // Logging
        logger.log("SEARCH_WORKERS", "ALL", "Worker", org,
                "Found " + results.size() + " result(s)");

        return results;

    }

}
