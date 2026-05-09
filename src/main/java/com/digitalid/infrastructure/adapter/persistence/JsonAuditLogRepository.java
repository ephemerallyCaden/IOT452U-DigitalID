package com.digitalid.infrastructure.adapter.persistence;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.digitalid.application.port.out.AuditLogRepository;
import com.digitalid.domain.model.AuditLogEntry;
import com.digitalid.infrastructure.config.DataStorePath;
import com.digitalid.infrastructure.config.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class JsonAuditLogRepository implements AuditLogRepository {

    private final Path filePath;
    private final Gson gson;

    public JsonAuditLogRepository(DataStorePath connection) {
        this.filePath = connection.getFilePath("audit_log.json");
        this.gson = GsonFactory.create();
    }

    @Override
    public void save(AuditLogEntry entry) {
        List<AuditLogEntry> entries = loadAll();
        entries.add(entry);
        writeAll(entries);
    }

    @Override
    public List<AuditLogEntry> findByEntityId(String entityId) {
        return loadAll().stream()
                .filter(e -> entityId.equals(e.getEntityId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLogEntry> findByOrganisationId(String organisationId) {
        return loadAll().stream()
                .filter(e -> organisationId.equals(e.getOrganisationId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLogEntry> findAll() {
        return loadAll();
    }

    private List<AuditLogEntry> loadAll() {
        try {
            String content = Files.readString(filePath);
            Type listType = new TypeToken<ArrayList<AuditLogEntry>>(){}.getType();
            List<AuditLogEntry> entries = gson.fromJson(content, listType);
            return entries != null ? entries : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read audit log data store: " + filePath, e);
        }
    }

    private void writeAll(List<AuditLogEntry> entries) {
        try {
            Files.writeString(filePath, gson.toJson(entries));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write audit_log.json", e);
        }
    }

}
