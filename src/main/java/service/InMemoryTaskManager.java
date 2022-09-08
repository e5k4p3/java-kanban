package service;

import models.Epic;
import models.Subtask;
import models.Task;
import service.exceptions.TaskValidationException;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import service.exceptions.SubtaskWithoutEpicException;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    private static int id = 1;
    private final HashMap<Integer, Task> allTasks;
    private final HashMap<Integer, Subtask> allSubtasks;
    private final HashMap<Integer, Epic> allEpics;
    private final TreeSet<Task> allTasksSortedByStartTime;
    protected final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.allTasks = new HashMap<>();
        this.allSubtasks = new HashMap<>();
        this.allEpics = new HashMap<>();
        this.allTasksSortedByStartTime = new TreeSet<>(Comparator.comparing(Task::getStartTime));
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
        try {
            if (validateTask(task)) {
                allTasks.putIfAbsent(task.getId(), task);
                allTasksSortedByStartTime.add(task);
            }
        } catch (TaskValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        try {
            if (validateTask(subtask)) {
                if (allEpics.containsKey(subtask.getEpicId())) {
                    allSubtasks.putIfAbsent(subtask.getId(), subtask);
                    allEpics.get(subtask.getEpicId()).addToListOfSubtasks(subtask);
                    allTasksSortedByStartTime.add(subtask);
                } else {
                    throw new SubtaskWithoutEpicException("Попытка создать Subtask для Epic, которого нет");
                }
            }
        } catch (TaskValidationException | SubtaskWithoutEpicException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic != null) {
            allEpics.putIfAbsent(epic.getId(), epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        try {
            if (validateTask(task)) {
                if (allTasks.containsKey(task.getId())) {
                    allTasks.put(task.getId(), task);
                }
            }
        } catch (TaskValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        try {
            if (validateTask(subtask)) {
                if (allSubtasks.containsKey(subtask.getId())) {
                    allSubtasks.put(subtask.getId(), subtask);
                    allEpics.get(subtask.getEpicId()).addToListOfSubtasks(subtask);
                }
            }
        } catch (TaskValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            if (allEpics.containsKey(epic.getId())) {
                allEpics.get(epic.getId()).setName(epic.getName());
                allEpics.get(epic.getId()).setDescription(epic.getDescription());
            }
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (allTasks.getOrDefault(id, null) != null) {
            allTasksSortedByStartTime.remove(getTaskById(id));
            allTasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        if (allSubtasks.getOrDefault(id, null) != null) {
            if (allEpics.get(allSubtasks.get(id).getEpicId()).getListOfSubtasks().containsKey(id)) {
                allEpics.get(allSubtasks.get(id).getEpicId()).removeFromListOfSubtasks(id);
                allTasksSortedByStartTime.remove(getSubtaskById(id));
                allSubtasks.remove(id);
                historyManager.remove(id);
            }
        }
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

    @Override
    public Set<Task> getPrioritizedTasks() {
        return allTasksSortedByStartTime;
    }

    protected Map<Integer, Task> getAllTaskTreeMap() {
        final Map<Integer, Task> sortedAllTasks = new TreeMap<>();

        sortedAllTasks.putAll(getAllTasks());
        sortedAllTasks.putAll(getAllEpics());
        sortedAllTasks.putAll(getAllSubtasks());
        return sortedAllTasks;
    }

    protected Boolean validateTask(Task task) throws TaskValidationException {
        if (task != null) {
            for (Task sortedTask : allTasksSortedByStartTime) {
                if (sortedTask.getStartTime().isBefore(task.getStartTime()) &&
                        sortedTask.getEndTime().isAfter(task.getStartTime())) {
                    throw new TaskValidationException(task.getName() + " заканчивается после начала " +
                            sortedTask.getName());
                }
                if (sortedTask.getStartTime().isAfter(task.getStartTime()) &&
                        sortedTask.getStartTime().isBefore(task.getEndTime())) {
                    throw new TaskValidationException(task.getName() + " начинается до завершения " +
                            sortedTask.getName());
                }
            }
            return true;
        }
        return false;

    }

    private HashMap<Integer, Subtask> getSubtasksByEpicId(int id) {
        return allEpics.get(id).getListOfSubtasks();
    }
}
