package service;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import service.http.KVClient;
import service.http.KVServer;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getDefaultFileBacked(File file) {
        return new FileBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static HttpTaskManager getDefaultHttp(KVClient kvClient, String key) {
        return new HttpTaskManager(kvClient, key);
    }
}
