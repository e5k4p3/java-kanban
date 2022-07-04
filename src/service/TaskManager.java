package service;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;

import static models.TaskStatus.*;

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
        return allTasks;
    }

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return allSubtasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return allEpics;
    }

    public ArrayList<HashMap> getAllTypesOfTasks() {
        ArrayList<HashMap> result = new ArrayList<>();

        result.add(getAllTasks());
        result.add(getAllSubtasks());
        result.add(getAllEpics());
        return result;
    }

    public void clearAllTasks() {
        allTasks.clear();
    }

    public void clearAllSubtasks() {
        allSubtasks.clear();
        for (Integer id : allEpics.keySet()) {
            allEpics.get(id).setStatus(NEW);
            allEpics.get(id).clearListOfSubtasksId();
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

    public ArrayList<Integer> getSubtasksByEpicId(int epicId) {
        if (allEpics.getOrDefault(epicId, null) != null) {
            return allEpics.get(epicId).getListOfSubtasksId();
        } else {
            return null;
        }
    }

    public void addTask(Task task) {
        allTasks.put(task.getId(), task);
    }

    public void addSubtask(Subtask subtask) {
        allSubtasks.put(subtask.getId(), subtask);
        getSubtasksByEpicId(subtask.getEpicId()).add(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
    }

    public void addEpic(Epic epic) {
        allEpics.put(epic.getId(), epic);
    }

    public void updateTask(Task task) {
        if (allTasks.containsKey(task.getId())) {
            allTasks.put(task.getId(), task);
        }
    }

    public void updateSubtask(Subtask subtask) { //Если среди эпиков нету того, который указан в сабтаске, то, по идее,
        if (allSubtasks.containsKey(subtask.getId())) { // ничего не должно происходить, ибо сабтаска без эпика не
            allSubtasks.put(subtask.getId(), subtask); // может существовать. Поэтому либо ничего не должно происходить,
            updateEpicStatus(subtask.getEpicId()); // либо должен создаваться обычный Task без epicId.
        }
    }

    public void updateEpic(Epic epic) {
        if (allEpics.containsKey(epic.getId())) {
            allEpics.get(epic.getId()).setName(epic.getName());
            allEpics.get(epic.getId()).setDescription(epic.getDescription());
        }
    }

    public void updateEpicStatus(int id) {
        int inProgressStatus = 0;
        int doneStatus = 0;

        if (getSubtasksByEpicId(id) != null) {
            for (Integer index : getSubtasksByEpicId(id)) {
                if (allSubtasks.get(index).getStatus() == IN_PROGRESS) {
                    inProgressStatus++;
                } else if (allSubtasks.get(index).getStatus() == IN_PROGRESS) {
                    doneStatus++;
                }
            }
            if (inProgressStatus > 0 && doneStatus > 0) {
                allEpics.get(id).setStatus(NEW);
            } else if (inProgressStatus >= 1) {
                allEpics.get(id).setStatus(IN_PROGRESS);
            } else {
                allEpics.get(id).setStatus(DONE);
            }
        } else {
            allEpics.get(id).setStatus(NEW);
        }
    }

    public void removeTaskById(int id) {
        if (allTasks.getOrDefault(id, null) != null) {
            allTasks.remove(id);
        }
    }

    public void removeSubtaskById(int id) {
        if (getSubtasksByEpicId(allSubtasks.getOrDefault(id, null).getEpicId()) != null) {
            getSubtasksByEpicId(allSubtasks.get(id).getEpicId()).remove((Integer) id);
            updateEpicStatus(allSubtasks.get(id).getEpicId());
            allSubtasks.remove(id);
        }
    }

    public void removeEpicById(int id) {
        ArrayList<Integer> subtasksIdToRemove = getSubtasksByEpicId(id);
        if (allEpics.getOrDefault(id, null) != null) {
            if (getSubtasksByEpicId(id) != null) {
                for (int i = 0; i <= subtasksIdToRemove.size(); i++) {
                    removeSubtaskById(subtasksIdToRemove.get(0));
                }
            }
            allEpics.remove(id);
        }
    }
}
