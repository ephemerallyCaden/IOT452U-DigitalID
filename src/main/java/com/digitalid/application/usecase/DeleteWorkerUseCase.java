package com.digitalid.application.usecase;

import java.util.List;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.application.request.DeleteWorkerRequest;
import com.digitalid.application.service.AuditService;
import com.digitalid.application.service.WorkerLifecycleNotifier;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.OrganisationContext;


public class DeleteWorkerUseCase implements UseCase<DeleteWorkerRequest, Void> {

    private final OrganisationContext org;
    private final WorkerRepository repository;
    private final CertificationRepository certRepository;
    private final AuditService logger;
    private final WorkerLifecycleNotifier notifier;

    public DeleteWorkerUseCase (OrganisationContext org, WorkerRepository repository,
                                CertificationRepository certRepository,
                                AuditService logger, WorkerLifecycleNotifier notifier) {
        this.org = org;
        this.repository = repository;
        this.certRepository = certRepository;
        this.logger = logger;
        this.notifier = notifier;
    }

    public Void execute(DeleteWorkerRequest request) {

        String reqWorkerId = request.getWorkerId();

        // Clean up associated certifications before deleting the worker
        List<Certification> workerCerts = certRepository.findByWorkerId(reqWorkerId);
        for (Certification cert : workerCerts) {
            certRepository.delete(cert.getId());
        }

        repository.delete(reqWorkerId);

        // Notify observers of worker removal
        notifier.notifyWorkerDeleted(reqWorkerId);

        // Logging
        logger.log("DELETE_WORKER", reqWorkerId, "Worker", org);

        return null;

    }

}
