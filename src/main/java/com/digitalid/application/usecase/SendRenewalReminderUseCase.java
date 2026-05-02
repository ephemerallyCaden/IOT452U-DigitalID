package com.digitalid.application.usecase;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.NotificationRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Worker;


public class SendRenewalReminderUseCase implements UseCase<NotificationRequest, Void> {

    private final OrganisationContext org;
    private final WorkerRepository workerRepository;
    private final AuditService logger;

    public SendRenewalReminderUseCase (OrganisationContext org, WorkerRepository workerRepository,
                                       AuditService logger) {
        this.org = org;
        this.workerRepository = workerRepository;
        this.logger = logger;
    }

    public Void execute(NotificationRequest request) {

        String reqWorkerId = request.getWorkerId();

        // Verify worker exists
        Worker worker = workerRepository.findById(reqWorkerId);

        // Simulate sending the reminder (would be email/SMS in production)
        System.out.println("[NOTIFICATION] Renewal reminder sent to "
                + worker.getFullName() + " (" + worker.getEmail() + "): " + request.getMessage());

        // Logging
        logger.log("SEND_RENEWAL_REMINDER", reqWorkerId, "Notification", org,
                "Reminder sent: " + request.getMessage());

        return null;

    }

}
