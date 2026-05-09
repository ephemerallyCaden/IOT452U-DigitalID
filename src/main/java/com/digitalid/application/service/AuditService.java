package com.digitalid.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.digitalid.application.port.out.AuditLogRepository;
import com.digitalid.domain.model.AuditLogEntry;
import com.digitalid.domain.model.OrganisationContext;

public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String action, String entityId, String entityType,
                    OrganisationContext context, String details) {
        String id = "AUD-" + UUID.randomUUID().toString().substring(0, 8);
        AuditLogEntry entry = new AuditLogEntry(
                id, action, entityId, entityType,
                context.getOrganisationId(), context.getType().name(),
                details, LocalDateTime.now()
        );
        auditLogRepository.save(entry);
    }

    public void log(String action, String entityId, String entityType,
                    OrganisationContext context) {
        log(action, entityId, entityType, context, null);
    }
}
