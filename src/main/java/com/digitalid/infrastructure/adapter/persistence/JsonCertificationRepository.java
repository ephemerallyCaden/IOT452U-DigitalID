package com.digitalid.infrastructure.adapter.persistence;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.domain.exception.EntityNotFoundException;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.CertificationType;
import com.digitalid.infrastructure.config.DataStorePath;
import com.digitalid.infrastructure.config.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class JsonCertificationRepository implements CertificationRepository {

    private final Path filePath;
    private final Gson gson;

    public JsonCertificationRepository(DataStorePath connection) {
        this.filePath = connection.getFilePath("certifications.json");
        this.gson = GsonFactory.create();
    }

    @Override
    public void save(Certification certification) {
        List<Certification> certs = loadAll();
        certs.removeIf(c -> c.getId().equals(certification.getId()));
        certs.add(certification);
        writeAll(certs);
    }

    @Override
    public Certification findById(String certificationId) {
        return loadAll().stream()
                .filter(c -> c.getId().equals(certificationId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Certification", certificationId));
    }

    @Override
    public List<Certification> findByWorkerId(String workerId) {
        return loadAll().stream()
                .filter(c -> c.getWorkerId().equals(workerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Certification> findByType(CertificationType type) {
        return loadAll().stream()
                .filter(c -> c.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Certification> findExpiringSoon(int withinDays) {
        return loadAll().stream()
                .filter(c -> c.getExpirationDate() != null)
                .filter(c -> !c.isExpired())
                .filter(c -> c.daysUntilExpiration() <= withinDays)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String certificationId) {
        List<Certification> certs = loadAll();
        certs.removeIf(c -> c.getId().equals(certificationId));
        writeAll(certs);
    }

    private List<Certification> loadAll() {
        try {
            String content = Files.readString(filePath);
            Type listType = new TypeToken<ArrayList<Certification>>(){}.getType();
            List<Certification> certs = gson.fromJson(content, listType);
            return certs != null ? certs : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read certifications data store: " + filePath, e);
        }
    }

    private void writeAll(List<Certification> certs) {
        try {
            Files.writeString(filePath, gson.toJson(certs));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write certifications.json", e);
        }
    }

}
