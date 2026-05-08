package com.digitalid.application.usecase;

import java.util.List;
import java.util.Map;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.ExportFormatter;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.ExportWorkerDataRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.exception.ValidationException;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Worker;


public class ExportWorkerDataUseCase implements UseCase<ExportWorkerDataRequest, String> {

    private final OrganisationContext org;
    private final WorkerRepository workerRepository;
    private final Map<String, ExportFormatter> formatters;
    private final AuditService logger;

    public ExportWorkerDataUseCase (OrganisationContext org, WorkerRepository workerRepository,
                                    Map<String, ExportFormatter> formatters, AuditService logger) {
        this.org = org;
        this.workerRepository = workerRepository;
        this.formatters = formatters;
        this.logger = logger;
    }

    public String execute(ExportWorkerDataRequest request) {

        List<Worker> workers;
        if (request.getRegion() != null) {
            workers = workerRepository.findByRegion(request.getRegion());
        } else {
            workers = workerRepository.listAll();
        }

        String formatKey = request.getFormat().toUpperCase();
        ExportFormatter formatter = formatters.get(formatKey);
        if (formatter == null) {
            throw new ValidationException("Unsupported export format: " + request.getFormat());
        }

        String output = formatter.format(workers);

        // Logging
        logger.log("EXPORT_WORKER_DATA", "ALL", "Worker", org,
                "Exported " + workers.size() + " worker(s) as " + request.getFormat());

        return output;

    }

}
