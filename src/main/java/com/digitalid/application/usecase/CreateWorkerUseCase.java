package com.digitalid.application.usecase;

import java.time.LocalDate;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Region;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.service.WorkerValidationService;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.CreateWorkerRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.application.service.WorkerLifecycleNotifier;


public class CreateWorkerUseCase implements UseCase<CreateWorkerRequest, Worker> {

    private final OrganisationContext org;
    private final WorkerValidationService validator;
    private final WorkerRepository repository;
    private final AuditService logger;
    private final WorkerLifecycleNotifier notifier;

    public CreateWorkerUseCase (OrganisationContext org, WorkerValidationService validator,
                                WorkerRepository repository, AuditService logger,
                                WorkerLifecycleNotifier notifier) {
        this.org = org;
        this.validator = validator;
        this.repository = repository;
        this.logger = logger;
        this.notifier = notifier;
    }

    private String generateWorkerId(Region region, int num) {
        return "WK-" + region.getCountryCode() + "-" + num;
    }

    public Worker execute(CreateWorkerRequest request) {

        String reqFullName = request.getFullName();
        LocalDate reqDateOfBirth = request.getDateOfBirth();
        String reqEmail = request.getEmail();
        Region reqRegion = request.getRegion();

        validator.validateCreation(reqFullName,reqDateOfBirth,reqEmail);
        String workerId = generateWorkerId(reqRegion, repository.nextNum());
        Worker worker = new Worker(workerId, reqFullName, reqDateOfBirth, reqEmail, reqRegion);
        repository.save(worker);

        // Notify observers of new worker
        notifier.notifyWorkerCreated(worker);

        // Logging
        logger.log("CREATE_WORKER", workerId, "Worker", org);

        return worker;

    }

}

