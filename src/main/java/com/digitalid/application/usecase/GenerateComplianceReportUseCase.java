package com.digitalid.application.usecase;

import java.util.List;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.GenerateReportRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Worker;


public class GenerateComplianceReportUseCase implements UseCase<GenerateReportRequest, String> {

    private final OrganisationContext org;
    private final WorkerRepository workerRepository;
    private final CertificationRepository certRepository;
    private final AuditService logger;

    public GenerateComplianceReportUseCase (OrganisationContext org, WorkerRepository workerRepository,
                                            CertificationRepository certRepository, AuditService logger) {
        this.org = org;
        this.workerRepository = workerRepository;
        this.certRepository = certRepository;
        this.logger = logger;
    }

    public String execute(GenerateReportRequest request) {

        List<Worker> workers = workerRepository.listAll();
        List<Certification> expiring = certRepository.findExpiringSoon(30);

        StringBuilder report = new StringBuilder();
        report.append("=== Compliance Report ===\n");
        report.append("Total workers: ").append(workers.size()).append("\n");
        report.append("Active workers: ").append(
                workers.stream().filter(Worker::isActive).count()).append("\n");
        report.append("Certifications expiring within 30 days: ").append(expiring.size()).append("\n");

        if (!expiring.isEmpty()) {
            report.append("\nExpiring certifications:\n");
            for (Certification cert : expiring) {
                report.append("  - ").append(cert.getWorkerId())
                        .append(" | ").append(cert.getType().getDisplayName())
                        .append(" | Expires: ").append(cert.getExpirationDate()).append("\n");
            }
        }

        // Logging
        logger.log("GENERATE_COMPLIANCE_REPORT", "ALL", "Report", org);

        return report.toString();

    }

}
