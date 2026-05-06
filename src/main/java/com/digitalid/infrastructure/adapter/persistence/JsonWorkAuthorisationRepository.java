package com.digitalid.infrastructure.adapter.persistence;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.digitalid.application.port.out.WorkAuthorisationRepository;
import com.digitalid.domain.exception.EntityNotFoundException;
import com.digitalid.domain.model.WorkAuthorisation;
import com.digitalid.infrastructure.config.DataStorePath;
import com.google.gson.Gson;
import com.digitalid.infrastructure.config.GsonFactory;
import com.google.gson.reflect.TypeToken;


public class JsonWorkAuthorisationRepository implements WorkAuthorisationRepository {

    private final Path filePath;
    private final Gson gson;

    public JsonWorkAuthorisationRepository(DataStorePath connection) {
        this.filePath = connection.getFilePath("work_authorisations.json");
        this.gson = GsonFactory.create();
    }

    @Override
    public void save(WorkAuthorisation workAuthorisation) {
        List<WorkAuthorisation> auths = loadAll();
        auths.removeIf(a -> a.getId().equals(workAuthorisation.getId()));
        auths.add(workAuthorisation);
        writeAll(auths);
    }

    @Override
    public WorkAuthorisation findById(String id) {
        return loadAll().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("WorkAuthorisation", id));
    }

    @Override
    public List<WorkAuthorisation> findByWorkerId(String workerId) {
        return loadAll().stream()
                .filter(a -> a.getWorkerId().equals(workerId))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        List<WorkAuthorisation> auths = loadAll();
        auths.removeIf(a -> a.getId().equals(id));
        writeAll(auths);
    }

    private List<WorkAuthorisation> loadAll() {
        try {
            String content = Files.readString(filePath);
            Type listType = new TypeToken<ArrayList<WorkAuthorisation>>(){}.getType();
            List<WorkAuthorisation> auths = gson.fromJson(content, listType);
            return auths != null ? auths : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read work authorisations data store: " + filePath, e);
        }
    }

    private void writeAll(List<WorkAuthorisation> auths) {
        try {
            Files.writeString(filePath, gson.toJson(auths));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write work_authorisations.json", e);
        }
    }

}
