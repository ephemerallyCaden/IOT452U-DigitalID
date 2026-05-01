package com.digitalid.application.port.out;

import java.util.List;

import com.digitalid.domain.model.WorkAuthorisation;

public interface WorkAuthorisationRepository {
    void save(WorkAuthorisation workAuthorisation);
    WorkAuthorisation findById(String id);
    List<WorkAuthorisation> findByWorkerId(String workerId);
    void delete(String id);
}
