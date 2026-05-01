package com.digitalid.application.usecase;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.ViewWorkerRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Worker;


public class ViewWorkerUseCase implements UseCase<ViewWorkerRequest, Worker> {

    private final OrganisationContext org;
    private final WorkerRepository repository;
    private final AuditService logger;

    public ViewWorkerUseCase (
        OrganisationContext org,
        WorkerRepository repository,
        AuditService logger
    ) {
        this.org = org;
        this.repository = repository;
        this.logger = logger;
    }

    public Worker execute(ViewWorkerRequest request) {

        String reqWorkerId = request.getWorkerId();

        Worker worker = repository.findById(reqWorkerId);

        // Logging
        logger.log("VIEW_WORKER", reqWorkerId, "Worker", org);

        return worker;

    }

}
