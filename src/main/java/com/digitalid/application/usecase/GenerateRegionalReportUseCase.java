package com.digitalid.application.usecase;

import java.util.List;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.GenerateReportRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Region;
import com.digitalid.domain.model.Worker;


public class GenerateRegionalReportUseCase implements UseCase<GenerateReportRequest, String> {

    private final OrganisationContext org;
    private final WorkerRepository workerRepository;
    private final AuditService logger;

    public GenerateRegionalReportUseCase (OrganisationContext org, WorkerRepository workerRepository,
                                          AuditService logger) {
        this.org = org;
        this.workerRepository = workerRepository;
        this.logger = logger;
    }

    public String execute(GenerateReportRequest request) {

        Region region = request.getRegion();
        List<Worker> workers = workerRepository.findByRegion(region);

        StringBuilder report = new StringBuilder();
        report.append("=== Regional Report: ").append(region.getDisplayName()).append(" ===\n");
        report.append("Total workers in region: ").append(workers.size()).append("\n");
        report.append("Active: ").append(
                workers.stream().filter(Worker::isActive).count()).append("\n");

        if (!workers.isEmpty()) {
            report.append("\nWorkers:\n");
            for (Worker w : workers) {
                report.append("  - ").append(w.getWorkerId())
                        .append(" | ").append(w.getFullName())
                        .append(" | ").append(w.getStatus()).append("\n");
            }
        }

        // Logging
        logger.log("GENERATE_REGIONAL_REPORT", region.name(), "Report", org);

        return report.toString();

    }

}
