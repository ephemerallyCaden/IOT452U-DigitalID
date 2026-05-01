package com.digitalid.application.port.out;

import java.util.List;

public interface AuditLogRepository {
    void save(String action, String entityId, String entityType,
              String organisationId, String organisationType, String details);
    List<String> findByEntityId(String entityId);
    List<String> findByOrganisationId(String organisationId);
    List<String> findAll();
}
