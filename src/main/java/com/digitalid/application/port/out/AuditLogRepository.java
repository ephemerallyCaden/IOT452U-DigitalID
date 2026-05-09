package com.digitalid.application.port.out;

import java.util.List;

import com.digitalid.domain.model.AuditLogEntry;

public interface AuditLogRepository {
    void save(AuditLogEntry entry);
    List<AuditLogEntry> findByEntityId(String entityId);
    List<AuditLogEntry> findByOrganisationId(String organisationId);
    List<AuditLogEntry> findAll();
}
