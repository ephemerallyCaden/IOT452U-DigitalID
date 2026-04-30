package com.digitalid.domain.model;

import java.time.LocalDateTime;
import java.util.Set;

public class OrganisationContext {

    private final String organisationId;
    private final OrganisationType type;
    private final String organisationName;
    private final Set<ToolType> allowedTools;
    private final LocalDateTime requestTimestamp;

    public OrganisationContext(String organisationId, OrganisationType type,
                               String organisationName, Set<ToolType> allowedTools) {
        this.organisationId = organisationId;
        this.type = type;
        this.organisationName = organisationName;
        this.allowedTools = Set.copyOf(allowedTools);
        this.requestTimestamp = LocalDateTime.now();
    }

    public boolean hasToolAccess(ToolType tool) {
        return allowedTools.contains(tool);
    }

    public String getOrganisationId() { return organisationId; }
    public OrganisationType getType() { return type; }
    public String getOrganisationName() { return organisationName; }
    public Set<ToolType> getAllowedTools() { return allowedTools; }
    public LocalDateTime getRequestTimestamp() { return requestTimestamp; }
}
