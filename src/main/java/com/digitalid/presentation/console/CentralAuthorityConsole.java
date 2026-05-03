package com.digitalid.presentation.console;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.registry.UseCaseRegistry;
import com.digitalid.application.request.*;
import com.digitalid.domain.model.*;


public class CentralAuthorityConsole extends ConsoleUI {

    private final UseCaseRegistry registry;
    private final OrganisationContext context;
    private final WorkerRepository workerRepository;
    private final CertificationRepository certRepository;
    private final List<MenuOption> menuOptions;

    public CentralAuthorityConsole(TerminalMenu terminal, UseCaseRegistry registry,
                                   OrganisationContext context, WorkerRepository workerRepository,
                                   CertificationRepository certRepository) {
        super(terminal, workerRepository);
        this.registry = registry;
        this.context = context;
        this.workerRepository = workerRepository;
        this.certRepository = certRepository;
        this.menuOptions = buildMenu();
    }

    private List<MenuOption> buildMenu() {
        List<MenuOption> options = new ArrayList<>();
        options.add(new MenuOption(1, "Create Worker", ToolType.CREATE_WORKER));
        options.add(new MenuOption(2, "Update Worker", ToolType.UPDATE_WORKER));
        options.add(new MenuOption(3, "Change Worker Status", ToolType.CHANGE_STATUS));
        options.add(new MenuOption(4, "Delete Worker", ToolType.DELETE_WORKER));
        options.add(new MenuOption(5, "Add Certification", ToolType.ADD_CERTIFICATION));
        options.add(new MenuOption(6, "Renew Certification", ToolType.RENEW_CERTIFICATION));
        options.add(new MenuOption(7, "Update Certification Status", ToolType.UPDATE_CERTIFICATION_STATUS));
        options.add(new MenuOption(8, "View Worker", ToolType.VIEW_WORKER));
        options.add(new MenuOption(9, "Verify Worker (Basic)", ToolType.VERIFY_BASIC));
        options.add(new MenuOption(10, "Verify with Cert History", ToolType.VERIFY_WITH_CERT_HISTORY));
        options.add(new MenuOption(11, "Verify with Conditions", ToolType.VERIFY_WITH_CONDITIONS));
        options.add(new MenuOption(12, "Verify with Permits", ToolType.VERIFY_WITH_PERMITS));
        options.add(new MenuOption(13, "Verify with Attributes", ToolType.VERIFY_WITH_ATTRIBUTES));
        options.add(new MenuOption(14, "View Audit Log", ToolType.VIEW_AUDIT_LOG));
        options.add(new MenuOption(15, "Compliance Report", ToolType.GENERATE_COMPLIANCE_REPORT));
        options.add(new MenuOption(16, "Check Expiring Certs", ToolType.CHECK_EXPIRING_CERTS));
        options.add(new MenuOption(17, "Regional Report", ToolType.CHECK_REGIONAL_COMPLIANCE));
        options.add(new MenuOption(18, "Organisation Activity", ToolType.VIEW_ORGANISATION_ACTIVITY));
        options.add(new MenuOption(19, "Search Workers", ToolType.SEARCH_WORKERS));
        options.add(new MenuOption(20, "Bulk Status Update", ToolType.BULK_STATUS_UPDATE));
        options.add(new MenuOption(21, "Export Worker Data", ToolType.EXPORT_WORKER_DATA));
        options.add(new MenuOption(22, "Send Renewal Reminder", ToolType.SEND_RENEWAL_REMINDER));
        options.add(new MenuOption(23, "Send Status Notification", ToolType.SEND_STATUS_NOTIFICATION));
        return options;
    }

    @Override
    public List<MenuOption> getMenuOptions() {
        return menuOptions;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleChoice(int choice) {
        try {
            switch (choice) {
                case 1: handleCreateWorker(); break;
                case 2: handleUpdateWorker(); break;
                case 3: handleChangeStatus(); break;
                case 4: handleDeleteWorker(); break;
                case 5: handleAddCertification(); break;
                case 6: handleRenewCertification(); break;
                case 7: handleUpdateCertStatus(); break;
                case 8: handleViewWorker(); break;
                case 9: handleVerifyBasic(); break;
                case 10: case 11: case 12: case 13: handleEnhancedVerify(choice); break;
                case 14: handleViewAuditLog(); break;
                case 15: handleComplianceReport(); break;
                case 16: handleCheckExpiring(); break;
                case 17: handleRegionalReport(); break;
                case 18: handleOrgActivity(); break;
                case 19: handleSearchWorkers(); break;
                case 20: handleBulkStatus(); break;
                case 21: handleExportData(); break;
                case 22: handleRenewalReminder(); break;
                case 23: handleStatusNotification(); break;
                default: printError("Invalid choice");
            }
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void handleCreateWorker() {
        String name = readInput("Full name: ");
        String dob = readInput("Date of birth (DD/MM/YYYY, DD-MM-YYYY, or YYYY-MM-DD): ");
        String email = readInput("Email: ");
        String regionStr = readInput("Region (e.g. UNITED_STATES, UNITED_KINGDOM): ");

        LocalDate dateOfBirth = parseDate(dob);
        Region region = Region.valueOf(regionStr.toUpperCase());

        CreateWorkerRequest request = new CreateWorkerRequest(name, dateOfBirth, email, region);
        UseCase<CreateWorkerRequest, Worker> useCase =
                (UseCase<CreateWorkerRequest, Worker>) registry.getUseCase(ToolType.CREATE_WORKER, context);
        Worker worker = useCase.execute(request);
        printSuccess("Worker created: " + worker.getWorkerId());
    }

    private void handleUpdateWorker() {
        String workerId = promptWorkerId();
        String email = readInput("New email (or press Enter to skip): ");
        String regionStr = readInput("New region (or press Enter to skip): ");

        Region region = regionStr.isEmpty() ? null : Region.valueOf(regionStr.toUpperCase());
        String emailVal = email.isEmpty() ? null : email;

        UpdateWorkerRequest request = new UpdateWorkerRequest(workerId, emailVal, region);
        UseCase<UpdateWorkerRequest, Worker> useCase =
                (UseCase<UpdateWorkerRequest, Worker>) registry.getUseCase(ToolType.UPDATE_WORKER, context);
        Worker worker = useCase.execute(request);
        printSuccess("Worker updated: " + worker.getWorkerId());
    }

    private void handleChangeStatus() {
        String workerId = promptWorkerId();
        String status = readInput("New status (ACTIVE, SUSPENDED, REVOKED): ");

        WorkerStatus newStatus = WorkerStatus.valueOf(status.toUpperCase());
        ChangeStatusRequest request = new ChangeStatusRequest(workerId, newStatus);
        UseCase<ChangeStatusRequest, Worker> useCase =
                (UseCase<ChangeStatusRequest, Worker>) registry.getUseCase(ToolType.CHANGE_STATUS, context);
        Worker worker = useCase.execute(request);
        printSuccess("Status changed to " + worker.getStatus());
    }

    private void handleDeleteWorker() {
        String workerId = promptWorkerId();
        DeleteWorkerRequest request = new DeleteWorkerRequest(workerId);
        UseCase<DeleteWorkerRequest, Void> useCase =
                (UseCase<DeleteWorkerRequest, Void>) registry.getUseCase(ToolType.DELETE_WORKER, context);
        useCase.execute(request);
        printSuccess("Worker deleted: " + workerId);
    }

    private void handleAddCertification() {
        String workerId = promptWorkerId();
        String typeStr = readInput("Certification type (e.g. US_FOOD_HANDLER): ");
        String authority = readInput("Issuing authority: ");
        String certNum = readInput("Certification number: ");
        String issueStr = readInput("Issue date (YYYY-MM-DD): ");
        String expiryStr = readInput("Expiration date (YYYY-MM-DD, or blank for lifetime): ");

        CertificationType type = CertificationType.valueOf(typeStr.toUpperCase());
        LocalDate issueDate = LocalDate.parse(issueStr);
        LocalDate expiryDate = expiryStr.isEmpty() ? null : LocalDate.parse(expiryStr);

        AddCertificationRequest request = new AddCertificationRequest(workerId, type, authority, certNum, issueDate, expiryDate);
        UseCase<AddCertificationRequest, Certification> useCase =
                (UseCase<AddCertificationRequest, Certification>) registry.getUseCase(ToolType.ADD_CERTIFICATION, context);
        Certification cert = useCase.execute(request);
        printSuccess("Certification added: " + cert.getId());
    }

    private void handleRenewCertification() {
        String workerId = promptWorkerId();
        String certId = promptCertId(workerId);
        String issueStr = readInput("New issue date (YYYY-MM-DD): ");
        String expiryStr = readInput("New expiration date (YYYY-MM-DD): ");

        RenewCertificationRequest request = new RenewCertificationRequest(workerId, certId,
                LocalDate.parse(issueStr), LocalDate.parse(expiryStr));
        UseCase<RenewCertificationRequest, Certification> useCase =
                (UseCase<RenewCertificationRequest, Certification>) registry.getUseCase(ToolType.RENEW_CERTIFICATION, context);
        Certification cert = useCase.execute(request);
        printSuccess("Certification renewed: " + cert.getId());
    }

    private void handleUpdateCertStatus() {
        String workerId = promptWorkerId();
        String certId = promptCertId(workerId);
        String status = readInput("New status (ACTIVE, SUSPENDED, EXPIRED): ");

        UpdateCertificationStatusRequest request = new UpdateCertificationStatusRequest(workerId, certId, status.toUpperCase());
        UseCase<UpdateCertificationStatusRequest, Certification> useCase =
                (UseCase<UpdateCertificationStatusRequest, Certification>) registry.getUseCase(ToolType.UPDATE_CERTIFICATION_STATUS, context);
        useCase.execute(request);
        printSuccess("Certification status updated");
    }

    private void handleViewWorker() {
        String workerId = promptWorkerId();
        ViewWorkerRequest request = new ViewWorkerRequest(workerId);
        UseCase<ViewWorkerRequest, Worker> useCase =
                (UseCase<ViewWorkerRequest, Worker>) registry.getUseCase(ToolType.VIEW_WORKER, context);
        Worker worker = useCase.execute(request);

        printInfo("\n--- Worker Details ---");
        printInfo("ID:     " + worker.getWorkerId());
        printInfo("Name:   " + worker.getFullName());
        printInfo("Email:  " + worker.getEmail());
        printInfo("Region: " + worker.getRegion().getDisplayName());
        printInfo("Status: " + worker.getStatus());
    }

    private void handleVerifyBasic() {
        String workerId = promptWorkerId();
        VerifyWorkerRequest request = new VerifyWorkerRequest(workerId, ToolType.VERIFY_BASIC);
        UseCase<VerifyWorkerRequest, VerificationResult> useCase =
                (UseCase<VerifyWorkerRequest, VerificationResult>) registry.getUseCase(ToolType.VERIFY_BASIC, context);
        VerificationResult result = useCase.execute(request);
        printInfo("Valid: " + result.isValid());
        printInfo("Message: " + result.getMessage());
    }

    private void handleEnhancedVerify(int choice) {
        String workerId = promptWorkerId();
        ToolType tool;
        switch (choice) {
            case 10: tool = ToolType.VERIFY_WITH_CERT_HISTORY; break;
            case 11: tool = ToolType.VERIFY_WITH_CONDITIONS; break;
            case 12: tool = ToolType.VERIFY_WITH_PERMITS; break;
            default: tool = ToolType.VERIFY_WITH_ATTRIBUTES;
        }

        VerifyWorkerRequest request = new VerifyWorkerRequest(workerId, tool);
        UseCase<VerifyWorkerRequest, VerificationResult> useCase =
                (UseCase<VerifyWorkerRequest, VerificationResult>) registry.getUseCase(tool, context);
        VerificationResult result = useCase.execute(request);
        printInfo("Valid: " + result.isValid());
        printInfo("Message: " + result.getMessage());
        if (result.getCertifications() != null && !result.getCertifications().isEmpty()) {
            printInfo("Certifications: " + result.getCertifications().size());
        }
    }

    @SuppressWarnings("unchecked")
    private void handleViewAuditLog() {
        String entityId = readInput("Entity ID (or blank for all): ");
        AuditLogRequest request = new AuditLogRequest(
                entityId.isEmpty() ? null : entityId, null);
        UseCase<AuditLogRequest, List> useCase =
                (UseCase<AuditLogRequest, List>) registry.getUseCase(ToolType.VIEW_AUDIT_LOG, context);
        List<String> entries = useCase.execute(request);
        printInfo("\n--- Audit Log (" + entries.size() + " entries) ---");
        entries.forEach(this::printInfo);
    }

    @SuppressWarnings("unchecked")
    private void handleComplianceReport() {
        GenerateReportRequest request = new GenerateReportRequest("COMPLIANCE", null);
        UseCase<GenerateReportRequest, String> useCase =
                (UseCase<GenerateReportRequest, String>) registry.getUseCase(ToolType.GENERATE_COMPLIANCE_REPORT, context);
        String report = useCase.execute(request);
        printInfo(report);
    }

    @SuppressWarnings("unchecked")
    private void handleCheckExpiring() {
        String days = readInput("Days threshold: ");
        CheckExpiringCertsRequest request = new CheckExpiringCertsRequest(Integer.parseInt(days));
        UseCase<CheckExpiringCertsRequest, List> useCase =
                (UseCase<CheckExpiringCertsRequest, List>) registry.getUseCase(ToolType.CHECK_EXPIRING_CERTS, context);
        List<Certification> certs = useCase.execute(request);
        printInfo("Found " + certs.size() + " expiring certification(s)");
        for (Certification c : certs) {
            printInfo("  - " + c.getWorkerId() + " | " + c.getType().getDisplayName() + " | Expires: " + c.getExpirationDate());
        }
    }

    @SuppressWarnings("unchecked")
    private void handleRegionalReport() {
        String regionStr = readInput("Region: ");
        Region region = Region.valueOf(regionStr.toUpperCase());
        GenerateReportRequest request = new GenerateReportRequest("REGIONAL", region);
        UseCase<GenerateReportRequest, String> useCase =
                (UseCase<GenerateReportRequest, String>) registry.getUseCase(ToolType.CHECK_REGIONAL_COMPLIANCE, context);
        String report = useCase.execute(request);
        printInfo(report);
    }

    @SuppressWarnings("unchecked")
    private void handleOrgActivity() {
        AuditLogRequest request = new AuditLogRequest(null, context.getOrganisationId());
        UseCase<AuditLogRequest, List> useCase =
                (UseCase<AuditLogRequest, List>) registry.getUseCase(ToolType.VIEW_ORGANISATION_ACTIVITY, context);
        List<String> entries = useCase.execute(request);
        printInfo("\n--- Organisation Activity (" + entries.size() + " entries) ---");
        entries.forEach(this::printInfo);
    }

    @SuppressWarnings("unchecked")
    private void handleSearchWorkers() {
        String name = readInput("Name query (or blank): ");
        String regionStr = readInput("Region filter (or blank): ");
        String statusStr = readInput("Status filter (or blank): ");

        Region region = regionStr.isEmpty() ? null : Region.valueOf(regionStr.toUpperCase());
        WorkerStatus status = statusStr.isEmpty() ? null : WorkerStatus.valueOf(statusStr.toUpperCase());

        SearchWorkersRequest request = new SearchWorkersRequest(
                name.isEmpty() ? null : name, region, status);
        UseCase<SearchWorkersRequest, List> useCase =
                (UseCase<SearchWorkersRequest, List>) registry.getUseCase(ToolType.SEARCH_WORKERS, context);
        List<Worker> workers = useCase.execute(request);
        printInfo("\nFound " + workers.size() + " worker(s):");
        for (Worker w : workers) {
            printInfo("  - " + w.getWorkerId() + " | " + w.getFullName() + " | " + w.getStatus());
        }
    }

    @SuppressWarnings("unchecked")
    private void handleBulkStatus() {
        String idsStr = readInput("Worker IDs (comma separated): ");
        String status = readInput("New status: ");

        List<String> ids = List.of(idsStr.split(","));
        WorkerStatus newStatus = WorkerStatus.valueOf(status.toUpperCase());

        BulkStatusUpdateRequest request = new BulkStatusUpdateRequest(ids, newStatus);
        UseCase<BulkStatusUpdateRequest, List> useCase =
                (UseCase<BulkStatusUpdateRequest, List>) registry.getUseCase(ToolType.BULK_STATUS_UPDATE, context);
        List<Worker> updated = useCase.execute(request);
        printSuccess(updated.size() + " worker(s) updated");
    }

    @SuppressWarnings("unchecked")
    private void handleExportData() {
        String regionStr = readInput("Region (or blank for all): ");
        String format = readInput("Format (JSON/CSV): ");

        Region region = regionStr.isEmpty() ? null : Region.valueOf(regionStr.toUpperCase());
        ExportWorkerDataRequest request = new ExportWorkerDataRequest(region, format);
        UseCase<ExportWorkerDataRequest, String> useCase =
                (UseCase<ExportWorkerDataRequest, String>) registry.getUseCase(ToolType.EXPORT_WORKER_DATA, context);
        String output = useCase.execute(request);
        printInfo(output);
    }

    private void handleRenewalReminder() {
        String workerId = promptWorkerId();
        String message = readInput("Reminder message: ");

        NotificationRequest request = new NotificationRequest(workerId, "RENEWAL_REMINDER", message);
        UseCase<NotificationRequest, Void> useCase =
                (UseCase<NotificationRequest, Void>) registry.getUseCase(ToolType.SEND_RENEWAL_REMINDER, context);
        useCase.execute(request);
        printSuccess("Renewal reminder sent");
    }

    private void handleStatusNotification() {
        String workerId = promptWorkerId();
        String message = readInput("Notification message: ");

        NotificationRequest request = new NotificationRequest(workerId, "STATUS_CHANGE", message);
        UseCase<NotificationRequest, Void> useCase =
                (UseCase<NotificationRequest, Void>) registry.getUseCase(ToolType.SEND_STATUS_NOTIFICATION, context);
        useCase.execute(request);
        printSuccess("Status notification sent");
    }

    private String promptCertId(String workerId) {
        List<Certification> certs = certRepository.findByWorkerId(workerId);
        if (!certs.isEmpty()) {
            printInfo("\nCertifications for " + workerId + ":");
            for (Certification c : certs) {
                printInfo("  " + c.getId() + " - " + c.getType().getDisplayName() + " (" + c.getStatus() + ")");
            }
            printInfo("");
        }
        return readInput("Certification ID: ");
    }

    private LocalDate parseDate(String input) {
        // Try DD/MM/YYYY
        try {
            return LocalDate.parse(input, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException ignored) {}

        // Try DD-MM-YYYY
        try {
            return LocalDate.parse(input, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException ignored) {}

        // Try YYYY-MM-DD (ISO)
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException ignored) {}

        throw new IllegalArgumentException("Could not parse date: " + input);
    }

}
