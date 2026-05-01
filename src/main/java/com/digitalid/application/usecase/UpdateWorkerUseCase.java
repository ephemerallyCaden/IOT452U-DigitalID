package com.digitalid.application.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Region;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.service.WorkerValidationService;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.UpdateWorkerRequest;
import com.digitalid.application.service.AuditService;


public class UpdateWorkerUseCase implements UseCase<UpdateWorkerRequest, Worker> {

    private final OrganisationContext org;
    private final WorkerValidationService validator;
    private final WorkerRepository repository;
    private final AuditService logger;

    public UpdateWorkerUseCase (OrganisationContext org, WorkerValidationService validator, WorkerRepository repository, AuditService logger) {
        this.org = org;
        this.validator = validator;
        this.repository = repository;
        this.logger = logger;
    }

    public Worker execute(UpdateWorkerRequest request) {

        String reqWorkerId = request.getWorkerId();
        String reqEmail = request.getEmail();
        Region reqRegion = request.getRegion();

        Worker worker = repository.findById(reqWorkerId);
        validator.validateUpdate(worker);

        if (reqEmail != null && !reqEmail.isEmpty()) { worker.updateEmail(reqEmail); }
        if (reqRegion != null) { worker.updateRegion(reqRegion); }
        repository.save(worker);

        // Logging
        logger.log("UPDATE_WORKER", reqWorkerId, "Worker", org);

        return worker;

    }

}

