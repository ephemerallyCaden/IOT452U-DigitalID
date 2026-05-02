package com.digitalid.application.usecase;

import java.util.List;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.ExportWorkerDataRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Worker;


public class ExportWorkerDataUseCase implements UseCase<ExportWorkerDataRequest, String> {

    private final OrganisationContext org;
    private final WorkerRepository workerRepository;
    private final AuditService logger;

    public ExportWorkerDataUseCase (OrganisationContext org, WorkerRepository workerRepository,
                                    AuditService logger) {
        this.org = org;
        this.workerRepository = workerRepository;
        this.logger = logger;
    }

    public String execute(ExportWorkerDataRequest request) {

        List<Worker> workers;
        if (request.getRegion() != null) {
            workers = workerRepository.findByRegion(request.getRegion());
        } else {
            workers = workerRepository.listAll();
        }

        String output;
        if ("CSV".equalsIgnoreCase(request.getFormat())) {
            output = exportCsv(workers);
        } else {
            output = exportJson(workers);
        }

        // Logging
        logger.log("EXPORT_WORKER_DATA", "ALL", "Worker", org,
                "Exported " + workers.size() + " worker(s) as " + request.getFormat());

        return output;

    }

    private String exportCsv(List<Worker> workers) {
        StringBuilder sb = new StringBuilder();
        sb.append("workerId,fullName,email,region,status\n");
        for (Worker w : workers) {
            sb.append(w.getWorkerId()).append(",")
                    .append(w.getFullName()).append(",")
                    .append(w.getEmail()).append(",")
                    .append(w.getRegion().name()).append(",")
                    .append(w.getStatus().name()).append("\n");
        }
        return sb.toString();
    }

    private String exportJson(List<Worker> workers) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < workers.size(); i++) {
            Worker w = workers.get(i);
            sb.append("  {")
                    .append("\"workerId\": \"").append(w.getWorkerId()).append("\", ")
                    .append("\"fullName\": \"").append(w.getFullName()).append("\", ")
                    .append("\"email\": \"").append(w.getEmail()).append("\", ")
                    .append("\"region\": \"").append(w.getRegion().name()).append("\", ")
                    .append("\"status\": \"").append(w.getStatus().name()).append("\"")
                    .append("}");
            if (i < workers.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

}
