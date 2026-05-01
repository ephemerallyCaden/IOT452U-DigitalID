package com.digitalid.domain.model;

import java.util.EnumSet;
import java.util.Set;

public class OrganisationProfile {

    private final OrganisationType type;
    private final Set<ToolType> allowedTools;

    private OrganisationProfile(OrganisationType type, Set<ToolType> allowedTools) {
        this.type = type;
        this.allowedTools = Set.copyOf(allowedTools);
    }

    public static OrganisationProfile forType(OrganisationType type) {
        switch (type) {
            case CENTRAL_AUTHORITY:
                return new OrganisationProfile(type, EnumSet.allOf(ToolType.class));

            case FINE_DINING:
                return new OrganisationProfile(type, EnumSet.of(
                        ToolType.VIEW_WORKER,
                        ToolType.VERIFY_BASIC,
                        ToolType.VERIFY_WORK_AUTHORISATION,
                        ToolType.VERIFY_WITH_CERT_HISTORY,
                        ToolType.VERIFY_WITH_ATTRIBUTES));

            case DELIVERY_SERVICE:
                return new OrganisationProfile(type, EnumSet.of(
                        ToolType.VIEW_WORKER,
                        ToolType.VERIFY_BASIC,
                        ToolType.VERIFY_WORK_AUTHORISATION,
                        ToolType.VERIFY_WITH_CONDITIONS));

            case STREET_VENDOR:
                return new OrganisationProfile(type, EnumSet.of(
                        ToolType.VIEW_WORKER,
                        ToolType.VERIFY_BASIC,
                        ToolType.VERIFY_WORK_AUTHORISATION,
                        ToolType.VERIFY_WITH_PERMITS));

            case FINANCIAL_SERVICE:
            case FAST_FOOD:
            case COFFEE_SHOP:
                return new OrganisationProfile(type, EnumSet.of(
                        ToolType.VIEW_WORKER,
                        ToolType.VERIFY_BASIC,
                        ToolType.VERIFY_WORK_AUTHORISATION));

            default:
                throw new IllegalArgumentException("Unknown organisation type: " + type);
        }
    }

    public boolean hasTool(ToolType tool) {
        return allowedTools.contains(tool);
    }

    public OrganisationType getType() { return type; }
    public Set<ToolType> getAllowedTools() { return allowedTools; }
    public int getToolCount() { return allowedTools.size(); }
}
