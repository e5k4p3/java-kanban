package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import service.http.HttpTaskServer;
import service.http.KVClient;
import service.http.adapters.DurationAdapter;
import service.http.adapters.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;


public class HttpTaskManager extends FileBackedTaskManager {
    private final KVClient kvClient;
    private final String key;
    private final Gson gson;
    public HttpTaskManager(KVClient kvClient, String key) {
        super();
        this.kvClient = kvClient;
        this.key = key;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public HttpTaskManager loadFromServer() {
        String taskManagerJson = kvClient.load(key);
        if (taskManagerJson == null) {
            save();
        }
        return gson.fromJson(taskManagerJson, HttpTaskManager.class);
    }

    @Override
    public void loadFromFile() {
        throw new UnsupportedOperationException("HttpTaskManager не работает с файлами, воспользуйтесь loadFromServer()");
    }

    @Override
    protected void save() {
        String taskManagerJson = gson.toJson(this);
        kvClient.save(key, taskManagerJson);
    }
}
