package com.digitalid.application.registry;

import java.util.EnumMap;
import java.util.Map;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.domain.exception.UnauthorisedAccessException;
import com.digitalid.domain.model.OrganisationContext;
import com.digitalid.domain.model.ToolType;

public class UseCaseRegistry {

    private final Map<ToolType, UseCase<?, ?>> useCases = new EnumMap<>(ToolType.class);

    public void register(ToolType toolType, UseCase<?, ?> useCase) {
        useCases.put(toolType, useCase);
    }

    /**
     * Retrieves the use case for the given tool, checking that the organisation
     * has permission to use it. Throws UnauthorisedAccessException if denied.
     */
    public UseCase<?, ?> getUseCase(ToolType toolType, OrganisationContext context) {
        if (!context.hasToolAccess(toolType)) {
            throw new UnauthorisedAccessException(
                    context.getOrganisationName(), toolType.getDisplayName());
        }

        UseCase<?, ?> useCase = useCases.get(toolType);
        if (useCase == null) {
            throw new IllegalStateException("No use case registered for tool: " + toolType);
        }
        return useCase;
    }

    public boolean isRegistered(ToolType toolType) {
        return useCases.containsKey(toolType);
    }

    public int size() {
        return useCases.size();
    }
}
