package com.digitalid.application.usecase;

import java.util.List;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.AuditLogRepository;
import com.digitalid.application.request.AuditLogRequest;
import com.digitalid.domain.model.AuditLogEntry;
import com.digitalid.domain.model.OrganisationContext;


public class ViewAuditLogUseCase implements UseCase<AuditLogRequest, List<AuditLogEntry>> {

    private final AuditLogRepository auditLogRepository;

    public ViewAuditLogUseCase (OrganisationContext org, AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLogEntry> execute(AuditLogRequest request) {

        String entityId = request.getEntityId();
        String organisationId = request.getOrganisationId();

        if (entityId != null && !entityId.isEmpty()) {
            return auditLogRepository.findByEntityId(entityId);
        }
        if (organisationId != null && !organisationId.isEmpty()) {
            return auditLogRepository.findByOrganisationId(organisationId);
        }

        return auditLogRepository.findAll();

    }

}
