package service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import models.Epic;
import models.Subtask;
import models.Task;
import service.http.KVClient;
import service.http.adapters.DurationAdapter;
import service.http.adapters.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;


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

    public void loadFromServer() {
        String taskManagerStateJson = kvClient.load(key);
        if (taskManagerStateJson == null) {
            return;
        }
        HashMap<String, String> taskManagerState = gson.fromJson(taskManagerStateJson,
                new TypeToken<HashMap<String, String>>(){}.getType());
        HashMap<Integer, Task> tasks = gson.fromJson(taskManagerState.get("tasks"),
                new TypeToken<HashMap<Integer, Task>>(){}.getType());
        for (Task task : tasks.values()) {
            super.addTask(task);
        }
        HashMap<Integer, Epic> epics = gson.fromJson(taskManagerState.get("epics"),
                new TypeToken<HashMap<Integer, Epic>>(){}.getType());
        for (Epic epic : epics.values()) {
            super.addEpic(epic);
        }
        HashMap<Integer, Subtask> subtasks = gson.fromJson(taskManagerState.get("subtasks"),
                new TypeToken<HashMap<Integer, Subtask>>(){}.getType());
        for (Subtask subtask : subtasks.values()) {
            super.addSubtask(subtask);
        }
        ArrayList<Task> history = gson.fromJson(taskManagerState.get("history"),
                new TypeToken<ArrayList<Task>>(){}.getType());
        for (Task task : history) {
            historyManager.add(task);
        }
    }

    @Override
    public void loadFromFile() {
        throw new UnsupportedOperationException("HttpTaskManager не работает с файлами, воспользуйтесь loadFromServer()");
    }

    @Override
    protected void save() {
        HashMap<String, String> taskManagerState = new HashMap<>();
        taskManagerState.put("tasks", gson.toJson(getAllTasks()));
        taskManagerState.put("subtasks", gson.toJson(getAllSubtasks()));
        taskManagerState.put("epics", gson.toJson(getAllEpics()));
        taskManagerState.put("history", gson.toJson(getHistory()));
        kvClient.save(key, gson.toJson(taskManagerState));
    }
}
