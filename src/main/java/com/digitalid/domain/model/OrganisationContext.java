package com.digitalid.domain.model;

import java.time.LocalDateTime;
import java.util.Set;

public class OrganisationContext {

    private final String organisationId;
    private final OrganisationType type;
    private final String organisationName;
    private final Region operatingRegion; // null for Central Authority (operates globally)
    private final Set<ToolType> allowedTools;
    private final LocalDateTime requestTimestamp;

    public OrganisationContext(String organisationId, OrganisationType type,
                               String organisationName, Region operatingRegion,
                               Set<ToolType> allowedTools) {
        this.organisationId = organisationId;
        this.type = type;
        this.organisationName = organisationName;
        this.operatingRegion = operatingRegion;
        this.allowedTools = Set.copyOf(allowedTools);
        this.requestTimestamp = LocalDateTime.now();
    }

    public boolean hasToolAccess(ToolType tool) {
        return allowedTools.contains(tool);
    }

    public boolean isGlobalAuthority() {
        return type == OrganisationType.CENTRAL_AUTHORITY;
    }

    public String getOrganisationId() { return organisationId; }
    public OrganisationType getType() { return type; }
    public String getOrganisationName() { return organisationName; }
    public Region getOperatingRegion() { return operatingRegion; }
    public Set<ToolType> getAllowedTools() { return allowedTools; }
    public LocalDateTime getRequestTimestamp() { return requestTimestamp; }
}
