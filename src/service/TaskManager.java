package service;

import models.Epic;
import models.Subtask;
import models.Task;


import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int id = 1;
    private final HashMap<Integer, Task> allTasks;
    private final HashMap<Integer, Subtask> allSubtasks;
    private final HashMap<Integer, Epic> allEpics;

    public TaskManager() {
        this.allTasks = new HashMap<>();
        this.allSubtasks = new HashMap<>();
        this.allEpics = new HashMap<>();
    }

    public static int getNewId() {
        return id++;
    }

    public HashMap<Integer, Task> getAllTasks() {
        return new HashMap<>(allTasks);
    }

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return new HashMap<>(allSubtasks);
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return new HashMap<>(allEpics);
    }

    public void clearAllTasks() {
        allTasks.clear();
    }

    public void clearAllSubtasks() {
        allSubtasks.clear();
        for (Integer id : allEpics.keySet()) {
            allEpics.get(id).clearListOfSubtasks();
            allEpics.get(id).updateStatus();
        }
    }

    public void clearAllEpics() {
        allEpics.clear();
        allSubtasks.clear();
    }

    public void clearAllTypesOfTasks() {
        allTasks.clear();
        allSubtasks.clear();
        allEpics.clear();
    }

    public Task getTaskById(int id) {
        return allTasks.getOrDefault(id, null);
    }

    public Subtask getSubtaskById(int id) {
        return allSubtasks.getOrDefault(id, null);
    }

    public Epic getEpicById(int id) {
        return allEpics.getOrDefault(id, null);
    }

    public void addTask(Task task) {
        allTasks.put(task.getId(), task);
    }

    public void addSubtask(Subtask subtask) {
        allSubtasks.put(subtask.getId(), subtask);
        allEpics.get(subtask.getEpicId()).addToListOfSubtasks(subtask);
        allEpics.get(subtask.getEpicId()).updateStatus();
    }

    public void addEpic(Epic epic) {
        allEpics.put(epic.getId(), epic);
    }

    public void updateTask(Task task) {
        if (allTasks.containsKey(task.getId())) {
            allTasks.put(task.getId(), task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (allSubtasks.containsKey(subtask.getId())) {
            allSubtasks.put(subtask.getId(), subtask);
            allEpics.get(subtask.getEpicId()).updateStatus();
        }
    }

    public void updateEpic(Epic epic) {
        if (allEpics.containsKey(epic.getId())) {
            allEpics.get(epic.getId()).setName(epic.getName());
            allEpics.get(epic.getId()).setDescription(epic.getDescription());
        }
    }

    public void removeTaskById(int id) {
        if (allTasks.getOrDefault(id, null) != null) {
            allTasks.remove(id);
        }
    }

    public void removeSubtaskById(int id) {
        if (allSubtasks.getOrDefault(id, null) != null) {
            if (allEpics.get(allSubtasks.get(id).getEpicId()).getListOfSubtasks().containsKey(id)) {
                allEpics.get(allSubtasks.get(id).getEpicId()).removeFromListOfSubtasks(id);
                allEpics.get(allSubtasks.get(id).getEpicId()).updateStatus();
                allSubtasks.remove(id);
            }
        }
    }

    public HashMap<Integer, Subtask> getSubtasksByEpicId (int id) {
        return allEpics.get(id).getListOfSubtasks();
    }

    public void removeEpicById(int id) {
        if (allEpics.containsKey(id)) {
            if (allEpics.get(id).getListOfSubtasks() != null) {
                ArrayList<Integer> subtasksIdToRemove = new ArrayList<>(getSubtasksByEpicId(id).keySet());
                for (Integer index : subtasksIdToRemove) {
                    removeSubtaskById(index);
                }
            }
            allEpics.remove(id);
        }
    }
}
