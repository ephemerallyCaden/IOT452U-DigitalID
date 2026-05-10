package com.digitalid.infrastructure.config;

import com.digitalid.application.port.out.AuditLogRepository;
import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.port.out.NotificationPort;
import com.digitalid.application.port.out.WorkAuthorisationRepository;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.registry.UseCaseRegistry;
import com.digitalid.application.service.AuditService;
import com.digitalid.application.service.ConsoleNotificationListener;
import com.digitalid.application.service.WorkerLifecycleNotifier;
import com.digitalid.application.usecase.*;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.OrganisationProfile;
import com.digitalid.domain.model.OrganisationType;
import com.digitalid.domain.model.Region;
import com.digitalid.domain.model.ToolType;
import com.digitalid.domain.service.CertificationValidationService;
import com.digitalid.domain.service.VerificationService;
import com.digitalid.domain.service.WorkerValidationService;
import com.digitalid.infrastructure.adapter.export.CsvExportFormatter;
import com.digitalid.infrastructure.adapter.export.JsonExportFormatter;
import com.digitalid.infrastructure.adapter.notification.ConsoleNotificationAdapter;
import com.digitalid.infrastructure.adapter.persistence.JsonAuditLogRepository;
import com.digitalid.infrastructure.adapter.persistence.JsonCertificationRepository;
import com.digitalid.infrastructure.adapter.persistence.JsonWorkAuthorisationRepository;
import com.digitalid.infrastructure.adapter.persistence.JsonWorkerRepository;


public class DependencyInjection {

    private final DataStorePath connection;
    private final WorkerRepository workerRepository;
    private final CertificationRepository certificationRepository;
    private final WorkAuthorisationRepository workAuthorisationRepository;
    private final AuditLogRepository auditLogRepository;
    private final AuditService auditService;
    private final WorkerValidationService workerValidationService;
    private final CertificationValidationService certificationValidationService;
    private final VerificationService verificationService;
    private final WorkerLifecycleNotifier statusChangeNotifier;
    private final NotificationPort notificationPort;

    public DependencyInjection() {
        this("data");
    }

    public DependencyInjection(String dataDir) {
        // Infrastructure
        this.connection = new DataStorePath(dataDir);
        new DataStoreInitialiser(connection).migrate();

        // Repositories
        this.workerRepository = new JsonWorkerRepository(connection);
        this.certificationRepository = new JsonCertificationRepository(connection);
        this.workAuthorisationRepository = new JsonWorkAuthorisationRepository(connection);
        this.auditLogRepository = new JsonAuditLogRepository(connection);

        // Services
        this.auditService = new AuditService(auditLogRepository);
        this.workerValidationService = new WorkerValidationService();
        this.certificationValidationService = new CertificationValidationService();
        this.verificationService = new VerificationService();

        // Observer: status change notifier with registered listeners
        this.statusChangeNotifier = new WorkerLifecycleNotifier();
        this.statusChangeNotifier.addListener(new ConsoleNotificationListener());

        // Notification adapter
        this.notificationPort = new ConsoleNotificationAdapter();

        // Seed sample data if empty
        new DataSeeder(workerRepository, certificationRepository).seed();
    }

    public UseCaseRegistry buildRegistry(OrganisationContext context) {
        UseCaseRegistry registry = new UseCaseRegistry();

        // Identity Management
        registry.register(ToolType.CREATE_WORKER,
                new CreateWorkerUseCase(context, workerValidationService, workerRepository, auditService, statusChangeNotifier));
        registry.register(ToolType.UPDATE_WORKER,
                new UpdateWorkerUseCase(context, workerValidationService, workerRepository, auditService));
        registry.register(ToolType.CHANGE_STATUS,
                new ChangeStatusUseCase(context, workerValidationService, workerRepository, auditService, statusChangeNotifier));
        registry.register(ToolType.DELETE_WORKER,
                new DeleteWorkerUseCase(context, workerRepository, certificationRepository, auditService, statusChangeNotifier));

        // Certification Management
        registry.register(ToolType.ADD_CERTIFICATION,
                new AddCertificationUseCase(context, certificationValidationService, certificationRepository, workerRepository, auditService));
        registry.register(ToolType.RENEW_CERTIFICATION,
                new RenewCertificationUseCase(context, certificationValidationService, certificationRepository, workerRepository, auditService));
        registry.register(ToolType.UPDATE_CERTIFICATION_STATUS,
                new UpdateCertificationStatusUseCase(context, certificationRepository, auditService));

        // Core Verification
        registry.register(ToolType.VIEW_WORKER,
                new ViewWorkerUseCase(context, workerRepository, auditService));
        registry.register(ToolType.VERIFY_BASIC,
                new VerifyBasicUseCase(context, verificationService, workerRepository, auditService));
        registry.register(ToolType.VERIFY_WORK_AUTHORISATION,
                new VerifyWorkAuthorisationUseCase(context, verificationService, workerRepository, workAuthorisationRepository, auditService));

        // Enhanced Verification
        registry.register(ToolType.VERIFY_WITH_CERT_HISTORY,
                new VerifyWithCertHistoryUseCase(context, verificationService, certificationValidationService, workerRepository, certificationRepository, auditService));
        registry.register(ToolType.VERIFY_WITH_CONDITIONS,
                new VerifyWithConditionsUseCase(context, verificationService, certificationValidationService, workerRepository, certificationRepository, auditService));
        registry.register(ToolType.VERIFY_WITH_PERMITS,
                new VerifyWithPermitsUseCase(context, verificationService, certificationValidationService, workerRepository, certificationRepository, auditService));
        registry.register(ToolType.VERIFY_WITH_ATTRIBUTES,
                new VerifyWithAttributesUseCase(context, verificationService, certificationValidationService, workerRepository, certificationRepository, auditService));

        // Reporting
        registry.register(ToolType.VIEW_AUDIT_LOG,
                new ViewAuditLogUseCase(context, auditLogRepository));
        registry.register(ToolType.GENERATE_COMPLIANCE_REPORT,
                new GenerateComplianceReportUseCase(context, workerRepository, certificationRepository, auditService));
        registry.register(ToolType.CHECK_EXPIRING_CERTS,
                new CheckExpiringCertsUseCase(context, certificationRepository, auditService));
        registry.register(ToolType.CHECK_REGIONAL_COMPLIANCE,
                new CheckRegionalComplianceUseCase(context, workerRepository, auditService));
        registry.register(ToolType.VIEW_ORGANISATION_ACTIVITY,
                new ViewOrganisationActivityUseCase(context, auditLogRepository));

        // Search
        registry.register(ToolType.SEARCH_WORKERS,
                new SearchWorkersUseCase(context, workerRepository, auditService));

        // Batch Operations
        registry.register(ToolType.BULK_STATUS_UPDATE,
                new BulkStatusUpdateUseCase(context, workerValidationService, workerRepository, auditService));
        registry.register(ToolType.EXPORT_WORKER_DATA,
                new ExportWorkerDataUseCase(context, workerRepository,
                        java.util.Map.of("CSV", new CsvExportFormatter(), "JSON", new JsonExportFormatter()),
                        auditService));

        // Notifications
        registry.register(ToolType.SEND_RENEWAL_REMINDER,
                new SendRenewalReminderUseCase(context, workerRepository, notificationPort, auditService));
        registry.register(ToolType.SEND_STATUS_NOTIFICATION,
                new SendStatusNotificationUseCase(context, workerRepository, notificationPort, auditService));

        return registry;
    }

    public OrganisationContext createContext(String orgId, OrganisationType type, String orgName, Region operatingRegion) {
        OrganisationProfile profile = OrganisationProfile.forType(type);
        return new OrganisationContext(orgId, type, orgName, operatingRegion, profile.getAllowedTools());
    }

    public WorkerRepository getWorkerRepository() { return workerRepository; }
    public CertificationRepository getCertificationRepository() { return certificationRepository; }
    public WorkAuthorisationRepository getWorkAuthorisationRepository() { return workAuthorisationRepository; }
    public AuditLogRepository getAuditLogRepository() { return auditLogRepository; }

}
