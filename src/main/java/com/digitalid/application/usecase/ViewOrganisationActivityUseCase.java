package com.digitalid.application.usecase;

import java.util.List;

import com.digitalid.application.port.in.UseCase;
import com.digitalid.application.port.out.AuditLogRepository;
import com.digitalid.application.request.AuditLogRequest;
import com.digitalid.domain.model.OrganisationContext;


public class ViewOrganisationActivityUseCase implements UseCase<AuditLogRequest, List<String>> {

    private final OrganisationContext org;
    private final AuditLogRepository auditLogRepository;

    public ViewOrganisationActivityUseCase (OrganisationContext org, AuditLogRepository auditLogRepository) {
        this.org = org;
        this.auditLogRepository = auditLogRepository;
    }

    public List<String> execute(AuditLogRequest request) {

        // Show activity for this organisation
        String organisationId = request.getOrganisationId();

        if (organisationId != null && !organisationId.isEmpty()) {
            return auditLogRepository.findByOrganisationId(organisationId);
        }

        // Default to current org's activity
        return auditLogRepository.findByOrganisationId(org.getOrganisationId());

    }

}
