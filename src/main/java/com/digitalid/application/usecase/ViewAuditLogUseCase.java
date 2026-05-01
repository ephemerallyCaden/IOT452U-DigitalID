package com.digitalid.application.usecase;

import java.util.List;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.AuditLogRepository;
import com.digitalid.application.request.AuditLogRequest;
import com.digitalid.domain.model.OrganisationContext;


public class ViewAuditLogUseCase implements UseCase<AuditLogRequest, List<String>> {

    private final OrganisationContext org;
    private final AuditLogRepository auditLogRepository;

    public ViewAuditLogUseCase (OrganisationContext org, AuditLogRepository auditLogRepository) {
        this.org = org;
        this.auditLogRepository = auditLogRepository;
    }

    public List<String> execute(AuditLogRequest request) {

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
