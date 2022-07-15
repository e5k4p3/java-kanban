package service;

import interfaces.HistoryManager;
import models.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> tasksInHistory;

    public InMemoryHistoryManager() {
        tasksInHistory = new LinkedList<>();
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
            tasksInHistory.removeFirst();
            tasksInHistory.add(task);
        }
    }
}
