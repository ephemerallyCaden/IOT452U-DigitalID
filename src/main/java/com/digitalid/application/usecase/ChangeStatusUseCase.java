package com.digitalid.application.usecase;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.model.WorkerStatus;
import com.digitalid.domain.service.WorkerValidationService;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.ChangeStatusRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.application.service.WorkerLifecycleNotifier;


public class ChangeStatusUseCase implements UseCase<ChangeStatusRequest, Worker> {

    private final OrganisationContext org;
    private final WorkerValidationService validator;
    private final WorkerRepository repository;
    private final AuditService logger;
    private final WorkerLifecycleNotifier notifier;

    public ChangeStatusUseCase (OrganisationContext org, WorkerValidationService validator,
                                WorkerRepository repository, AuditService logger,
                                WorkerLifecycleNotifier notifier) {
        this.org = org;
        this.validator = validator;
        this.repository = repository;
        this.logger = logger;
        this.notifier = notifier;
    }

    public Worker execute(ChangeStatusRequest request) {

        String reqWorkerId = request.getWorkerId();
        WorkerStatus reqNewStatus = request.getNewStatus();

        Worker worker = repository.findById(reqWorkerId);
        validator.validateStatusChange(worker, reqNewStatus);

        WorkerStatus previousStatus = worker.getStatus();
        worker.changeStatus(reqNewStatus);

        repository.save(worker);

        // Notify observers of the status change
        notifier.notifyStatusChanged(worker, previousStatus, reqNewStatus);

        // Logging
        logger.log("CHANGE_STATUS", reqWorkerId, "Worker", org);

        return worker;

    }

}

