package service;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;

import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private static int id = 1;
    private final HashMap<Integer, Task> allTasks;
    private final HashMap<Integer, Subtask> allSubtasks;
    private final HashMap<Integer, Epic> allEpics;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.allTasks = new HashMap<>();
        this.allSubtasks = new HashMap<>();
        this.allEpics = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    public static int getNewId() {
        return id++;
    }

    @Override
    public HashMap<Integer, Task> getAllTasks() {
        return new HashMap<>(allTasks);
    }

    @Override
    public HashMap<Integer, Subtask> getAllSubtasks() {
        return new HashMap<>(allSubtasks);
    }

    @Override
    public HashMap<Integer, Epic> getAllEpics() {
        return new HashMap<>(allEpics);
    }

    @Override
    public void clearAllTasks() {
        allTasks.clear();
    }

    @Override
    public void clearAllSubtasks() {
        allSubtasks.clear();
        for (Integer id : allEpics.keySet()) {
            allEpics.get(id).clearListOfSubtasks();
            allEpics.get(id).updateStatus();
        }
    }

    @Override
    public void clearAllEpics() {
        allEpics.clear();
        allSubtasks.clear();
    }

    @Override
    public void clearAllTypesOfTasks() {
        allTasks.clear();
        allSubtasks.clear();
        allEpics.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(allTasks.get(id));
        return allTasks.getOrDefault(id, null);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(allSubtasks.get(id));
        return allSubtasks.getOrDefault(id, null);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(allEpics.get(id));
        return allEpics.getOrDefault(id, null);
    }

    @Override
    public void addTask(Task task) {
        task.setId(getNewId());
        allTasks.put(task.getId(), task);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(getNewId());
        allSubtasks.put(subtask.getId(), subtask);
        allEpics.get(subtask.getEpicId()).addToListOfSubtasks(subtask);
        allEpics.get(subtask.getEpicId()).updateStatus();
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(getNewId());
        allEpics.put(epic.getId(), epic);
    }

    @Override
    public void updateTask(Task task) {
        if (allTasks.containsKey(task.getId())) {
            allTasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (allSubtasks.containsKey(subtask.getId())) {
            allSubtasks.put(subtask.getId(), subtask);
            allEpics.get(subtask.getEpicId()).updateStatus();
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (allEpics.containsKey(epic.getId())) {
            allEpics.get(epic.getId()).setName(epic.getName());
            allEpics.get(epic.getId()).setDescription(epic.getDescription());
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (allTasks.getOrDefault(id, null) != null) {
            allTasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        if (allSubtasks.getOrDefault(id, null) != null) {
            if (allEpics.get(allSubtasks.get(id).getEpicId()).getListOfSubtasks().containsKey(id)) {
                allEpics.get(allSubtasks.get(id).getEpicId()).removeFromListOfSubtasks(id);
                allEpics.get(allSubtasks.get(id).getEpicId()).updateStatus();
                allSubtasks.remove(id);
                historyManager.remove(id);
            }
        }
    }

    private HashMap<Integer, Subtask> getSubtasksByEpicId(int id) {
        return allEpics.get(id).getListOfSubtasks();
    }

    @Override
    public void removeEpicById(int id) {
        if (allEpics.containsKey(id)) {
            if (allEpics.get(id).getListOfSubtasks() != null) {
                for (Integer index : getSubtasksByEpicId(id).keySet()) {
                    allSubtasks.remove(index);
                    historyManager.remove(index);
                }
            }
            allEpics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
