package com.digitalid.infrastructure.adapter.persistence;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.domain.exception.WorkerNotFoundException;
import com.digitalid.domain.model.Region;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.model.WorkerStatus;
import com.digitalid.infrastructure.config.DatabaseConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class JsonWorkerRepository implements WorkerRepository {

    private final Path filePath;
    private final Path sequencePath;
    private final Gson gson;

    public JsonWorkerRepository(DatabaseConnection connection) {
        this.filePath = connection.getFilePath("workers.json");
        this.sequencePath = connection.getFilePath("sequence.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public int nextNum() {
        try {
            String content = Files.readString(sequencePath);
            SequenceData seq = gson.fromJson(content, SequenceData.class);
            int num = seq.nextWorkerNum;
            seq.nextWorkerNum++;
            Files.writeString(sequencePath, gson.toJson(seq));
            return num;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read sequence file", e);
        }
    }

    @Override
    public void save(Worker worker) {
        List<Worker> workers = loadAll();
        // Remove existing if updating
        workers.removeIf(w -> w.getWorkerId().equals(worker.getWorkerId()));
        workers.add(worker);
        writeAll(workers);
    }

    @Override
    public List<Worker> listAll() {
        return loadAll();
    }

    @Override
    public void delete(String workerId) {
        List<Worker> workers = loadAll();
        workers.removeIf(w -> w.getWorkerId().equals(workerId));
        writeAll(workers);
    }

    @Override
    public Worker findById(String workerId) {
        return loadAll().stream()
                .filter(w -> w.getWorkerId().equals(workerId))
                .findFirst()
                .orElseThrow(() -> new WorkerNotFoundException(workerId));
    }

    @Override
    public List<Worker> findByRegion(Region region) {
        return loadAll().stream()
                .filter(w -> w.getRegion() == region)
                .collect(Collectors.toList());
    }

    @Override
    public List<Worker> findByStatus(WorkerStatus status) {
        return loadAll().stream()
                .filter(w -> w.getStatus() == status)
                .collect(Collectors.toList());
    }

    private List<Worker> loadAll() {
        try {
            String content = Files.readString(filePath);
            Type listType = new TypeToken<ArrayList<Worker>>(){}.getType();
            List<Worker> workers = gson.fromJson(content, listType);
            return workers != null ? workers : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void writeAll(List<Worker> workers) {
        try {
            Files.writeString(filePath, gson.toJson(workers));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write workers.json", e);
        }
    }

    private static class SequenceData {
        int nextWorkerNum = 1;
    }

}
