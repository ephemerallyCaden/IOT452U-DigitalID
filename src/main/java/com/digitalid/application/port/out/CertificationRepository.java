package com.digitalid.application.port.out;

import java.util.List;

import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.CertificationType;

public interface CertificationRepository {
    void save(Certification certification);
    Certification findById(String certificationId);
    List<Certification> findByWorkerId(String workerId);
    List<Certification> findByType(CertificationType type);
    List<Certification> findExpiringSoon(int withinDays);
    void delete(String certificationId);
}
