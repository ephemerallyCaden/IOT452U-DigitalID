package com.digitalid.application.usecase;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.NotificationPort;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.NotificationRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.Worker;


public class SendStatusNotificationUseCase implements UseCase<NotificationRequest, Void> {

    private final OrganisationContext org;
    private final WorkerRepository workerRepository;
    private final NotificationPort notificationPort;
    private final AuditService logger;

    public SendStatusNotificationUseCase (OrganisationContext org, WorkerRepository workerRepository,
                                          NotificationPort notificationPort, AuditService logger) {
        this.org = org;
        this.workerRepository = workerRepository;
        this.notificationPort = notificationPort;
        this.logger = logger;
    }

    public Void execute(NotificationRequest request) {

        String reqWorkerId = request.getWorkerId();

        Worker worker = workerRepository.findById(reqWorkerId);

        notificationPort.send(worker.getFullName(), worker.getEmail(),
                "Status notification: " + request.getMessage());

        // Logging
        logger.log("SEND_STATUS_NOTIFICATION", reqWorkerId, "Notification", org,
                "Notification sent: " + request.getMessage());

        return null;

    }

}
