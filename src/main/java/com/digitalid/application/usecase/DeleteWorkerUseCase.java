package com.digitalid.application.usecase;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.service.WorkerValidationService;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.DeleteWorkerRequest;
import com.digitalid.application.service.AuditService;


public class DeleteWorkerUseCase implements UseCase<DeleteWorkerRequest, Void> {

    private final OrganisationContext org;
    private final WorkerRepository repository;
    private final AuditService logger;

    public DeleteWorkerUseCase (OrganisationContext org, WorkerRepository repository, AuditService logger) {
        this.org = org;
        this.repository = repository;
        this.logger = logger;
    }

    public Void execute(DeleteWorkerRequest request) {

        String reqWorkerId = request.getWorkerId();

        repository.delete(reqWorkerId);

        // Logging
        logger.log("DELETE_WORKER", reqWorkerId, "Worker", org);

        return null;

    }

}

