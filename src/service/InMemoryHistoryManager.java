package service;

import interfaces.HistoryManager;
import models.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    List<Task> tasksInHistory;

    public InMemoryHistoryManager() {
        tasksInHistory = new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return tasksInHistory;
    }

    @Override
    public void addTaskToHistory(Task task) {
        if (tasksInHistory.size() < 10) {
            tasksInHistory.add(task);
        } else {
            tasksInHistory.remove(0);
            tasksInHistory.add(task);
        }
    }
}
