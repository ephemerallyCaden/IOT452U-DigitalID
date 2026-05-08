package com.digitalid.application.usecase;

import java.util.ArrayList;
import java.util.List;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.BulkStatusUpdateRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.exception.DomainException;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.service.WorkerValidationService;


public class BulkStatusUpdateUseCase implements UseCase<BulkStatusUpdateRequest, List<Worker>> {

    private final OrganisationContext org;
    private final WorkerValidationService validator;
    private final WorkerRepository workerRepository;
    private final AuditService logger;

    public BulkStatusUpdateUseCase (OrganisationContext org, WorkerValidationService validator,
                                    WorkerRepository workerRepository, AuditService logger) {
        this.org = org;
        this.validator = validator;
        this.workerRepository = workerRepository;
        this.logger = logger;
    }

    public List<Worker> execute(BulkStatusUpdateRequest request) {

        List<Worker> updated = new ArrayList<>();
        List<String> failures = new ArrayList<>();

        for (String workerId : request.getWorkerIds()) {
            try {
                Worker worker = workerRepository.findById(workerId);
                validator.validateStatusChange(worker, request.getNewStatus());
                worker.changeStatus(request.getNewStatus());
                workerRepository.save(worker);
                updated.add(worker);
            } catch (DomainException e) {
                failures.add(workerId + ": " + e.getMessage());
            }
        }

        // Logging
        String details = updated.size() + " worker(s) updated to " + request.getNewStatus();
        if (!failures.isEmpty()) {
            details += "; " + failures.size() + " failed: " + String.join(", ", failures);
        }
        logger.log("BULK_STATUS_UPDATE", "BATCH", "Worker", org, details);

        return updated;

    }

}
