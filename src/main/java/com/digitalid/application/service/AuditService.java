package com.digitalid.application.service;

import com.digitalid.application.port.out.AuditLogRepository;
import com.digitalid.domain.model.OrganisationContext;

public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String action, String entityId, String entityType,
                    OrganisationContext context, String details) {
        auditLogRepository.save(
                action,
                entityId,
                entityType,
                context.getOrganisationId(),
                context.getType().name(),
                details
        );
    }

    public void log(String action, String entityId, String entityType,
                    OrganisationContext context) {
        log(action, entityId, entityType, context, null);
    }
}
