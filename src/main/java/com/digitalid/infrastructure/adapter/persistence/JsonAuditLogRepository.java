package com.digitalid.infrastructure.adapter.persistence;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.digitalid.application.port.out.AuditLogRepository;
import com.digitalid.infrastructure.config.DatabaseConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class JsonAuditLogRepository implements AuditLogRepository {

    private final Path filePath;
    private final Gson gson;

    public JsonAuditLogRepository(DatabaseConnection connection) {
        this.filePath = connection.getFilePath("audit_log.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void save(
        String action,
        String entityId,
        String entityType,
        String organisationId,
        String organisationType,
        String details
    ) {
        List<AuditEntry> entries = loadAll();
        AuditEntry entry = new AuditEntry();
        entry.action = action;
        entry.entityId = entityId;
        entry.entityType = entityType;
        entry.organisationId = organisationId;
        entry.organisationType = organisationType;
        entry.details = details;
        entry.timestamp = LocalDateTime.now().toString();
        entries.add(entry);
        writeAll(entries);
    }

    @Override
    public List<String> findByEntityId(String entityId) {
        return loadAll().stream()
                .filter(e -> entityId.equals(e.entityId))
                .map(this::formatEntry)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findByOrganisationId(String organisationId) {
        return loadAll().stream()
                .filter(e -> organisationId.equals(e.organisationId))
                .map(this::formatEntry)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAll() {
        return loadAll().stream()
                .map(this::formatEntry)
                .collect(Collectors.toList());
    }

    private String formatEntry(AuditEntry entry) {
        return "[" + entry.timestamp + "] " + entry.action + " | "
                + entry.entityType + ":" + entry.entityId + " | "
                + entry.organisationType + " (" + entry.organisationId + ")"
                + (entry.details != null ? " | " + entry.details : "");
    }

    private List<AuditEntry> loadAll() {
        try {
            String content = Files.readString(filePath);
            Type listType = new TypeToken<ArrayList<AuditEntry>>(){}.getType();
            List<AuditEntry> entries = gson.fromJson(content, listType);
            return entries != null ? entries : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void writeAll(List<AuditEntry> entries) {
        try {
            Files.writeString(filePath, gson.toJson(entries));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write audit_log.json", e);
        }
    }

    private static class AuditEntry {
        String action;
        String entityId;
        String entityType;
        String organisationId;
        String organisationType;
        String details;
        String timestamp;
    }

}
