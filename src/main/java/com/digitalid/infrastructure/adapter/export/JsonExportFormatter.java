package com.digitalid.infrastructure.adapter.export;

import java.util.List;

import com.digitalid.application.port.out.ExportFormatter;
import com.digitalid.domain.model.Worker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonExportFormatter implements ExportFormatter {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String format(List<Worker> workers) {
        JsonArray array = new JsonArray();
        for (Worker w : workers) {
            JsonObject obj = new JsonObject();
            obj.addProperty("workerId", w.getWorkerId());
            obj.addProperty("fullName", w.getFullName());
            obj.addProperty("email", w.getEmail());
            obj.addProperty("region", w.getRegion().name());
            obj.addProperty("status", w.getStatus().name());
            array.add(obj);
        }
        return gson.toJson(array);
    }
}
