import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> allTasks;
    private final HashMap<Integer, Subtask> allSubtasks;
    private final HashMap<Integer, Epic> allEpics;
    private int id = 0;

    public TaskManager() {
        this.allTasks = new HashMap<>();
        this.allSubtasks = new HashMap<>();
        this.allEpics = new HashMap<>();
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

    public void clearAllTasks() {
        allTasks.clear();
    }

    public void clearAllSubtasks() {
        allSubtasks.clear();
        for (Integer id : allEpics.keySet()) {
            allEpics.get(id).setStatus(TaskStatus.NEW);
            allEpics.get(id).clearListOfSubtasksId();
        }
    }

    public void clearAllEpics() {
        allEpics.clear();
        allSubtasks.clear();
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
        task.setId(id++);
        allTasks.put(task.getId(), task);
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(id++);
        allSubtasks.put(subtask.getId(), subtask);
        getSubtasksByEpicId(subtask.getEpicId()).add(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
    }

    public void addEpic(Epic epic) {
        epic.setId(id++);
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
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public void updateEpic(Epic epic) {
        if (allEpics.containsKey(epic.getId())) {
            allEpics.put(epic.getId(), epic);
        }
    }

    public void updateEpicStatus(int id) {
        int inProgressStatus = 0;
        int doneStatus = 0;

        if (getSubtasksByEpicId(id) != null) {
            for (int i = 0; i < getSubtasksByEpicId(id).size(); i++) {
                if (allSubtasks.getOrDefault(getSubtasksByEpicId(id).get(i), null) != null) {
                    if (allSubtasks.get(getSubtasksByEpicId(id).get(i)).getStatus() == TaskStatus.IN_PROGRESS) {
                        inProgressStatus++;
                    } else if (allSubtasks.get(getSubtasksByEpicId(id).get(i)).getStatus() == TaskStatus.DONE) {
                        doneStatus++;
                    }
                }
            }
            if (inProgressStatus == 0 && doneStatus == 0) {
                allEpics.get(id).setStatus(TaskStatus.NEW);
            } else if (inProgressStatus > 1) {
                allEpics.get(id).setStatus(TaskStatus.IN_PROGRESS);
            } else {
                allEpics.get(id).setStatus(TaskStatus.DONE);
            }
        }
    }

    public void removeTaskById(int id) {
        if (allTasks.getOrDefault(id, null) != null) {
            allTasks.remove(id);
        }
    }

    public void removeSubtaskById(int id) {
        if (getSubtasksByEpicId(allSubtasks.getOrDefault(id, null).getEpicId()) != null) {
            getSubtasksByEpicId(allSubtasks.getOrDefault(id, null).getEpicId()).remove((Integer) id);
            updateEpicStatus(allSubtasks.get(id).getEpicId());
            allSubtasks.remove(id);
        }
    }

    public void removeEpicById(int id) {
        if (allEpics.getOrDefault(id, null) != null) {
            if (getSubtasksByEpicId(allSubtasks.getOrDefault(id, null).getEpicId()) != null) {
                getSubtasksByEpicId(allSubtasks.get(id).getEpicId()).remove((Integer) id);
            }
            allEpics.remove(id);
        }
    }
}
