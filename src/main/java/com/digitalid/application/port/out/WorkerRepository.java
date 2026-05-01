package com.digitalid.application.port.out;

import java.util.List;

import com.digitalid.domain.model.Region;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.model.WorkerStatus;

public interface WorkerRepository {

    int nextNum();
    void save(Worker worker);
    List<Worker> listAll();
    void delete(String workerId);
    Worker findById(String workerId);
    List<Worker> findByRegion(Region region);
    List<Worker> findByStatus(WorkerStatus status);


}