package com.digitalid.domain.model;

import com.digitalid.domain.exception.ValidationException;

import java.time.LocalDateTime;

public class AuditLogEntry {

    private final String id;
    private final String action;
    private final String entityId;
    private final String entityType;
    private final String organisationId;
    private final String organisationType;
    private final String details;
    private final LocalDateTime timestamp;

    public AuditLogEntry(String id, String action, String entityId, String entityType,
                         String organisationId, String organisationType, String details,
                         LocalDateTime timestamp) {
        if (action == null || action.isBlank()) {
            throw new ValidationException("Action cannot be empty");
        }
        if (entityId == null || entityId.isBlank()) {
            throw new ValidationException("Entity ID cannot be empty");
        }
        this.id = id;
        this.action = action;
        this.entityId = entityId;
        this.entityType = entityType;
        this.organisationId = organisationId;
        this.organisationType = organisationType;
        this.details = details;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    // Getters

    public String getId() { return id; }
    public String getAction() { return action; }
    public String getEntityId() { return entityId; }
    public String getEntityType() { return entityType; }
    public String getOrganisationId() { return organisationId; }
    public String getOrganisationType() { return organisationType; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
