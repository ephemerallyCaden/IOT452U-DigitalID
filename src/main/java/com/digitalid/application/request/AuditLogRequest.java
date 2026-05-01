package com.digitalid.application.request;

import com.digitalid.application.port.in.Query;

// At least one of entityId or organisationId should be provided
public class AuditLogRequest implements Query {

    private final String entityId;
    private final String organisationId;

    public AuditLogRequest(String entityId, String organisationId) {
        this.entityId = entityId;
        this.organisationId = organisationId;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getOrganisationId() {
        return organisationId;
    }
}
