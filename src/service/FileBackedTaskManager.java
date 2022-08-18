package service;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.*;
import service.exceptions.*;

import java.io.*;
import java.util.*;

import static models.TaskStatus.*;
import static models.TaskType.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(new File("src/resources/save.csv"));
        Task task1 = new Task(getNewId(), TASK, "Первая таска", "Описание первой таски", NEW);
        Task task2 = new Task(getNewId(), TASK, "Вторая таска", "Описание второй таски", NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Epic epic1 = new Epic(getNewId(), EPIC, "Первый эпик", "Описание первого эпика");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask(getNewId(), SUBTASK, "Первая сабтаска", "Описание первой сабтаски",
                NEW, epic1.getId());
        Subtask subtask2 = new Subtask(getNewId(), SUBTASK, "Вторая сабтаска", "Описание второй сабтаски",
                IN_PROGRESS, epic1.getId());
        Subtask subtask3 = new Subtask(getNewId(), SUBTASK, "Третья сабтаска", "Описание третьей сабтаски",
                DONE, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        Epic epic2 = new Epic(getNewId(), EPIC, "Второй эпик", "Описание второго эпика");
        taskManager.addEpic(epic2);
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask1.getId());

        FileBackedTaskManager newTaskManager = new FileBackedTaskManager(new File("src/resources/save.csv"));
        newTaskManager.loadFromFile();
        Map<Integer, Task> allTasksMap = newTaskManager.getAllTaskTreeMap();
        for (Task task : allTasksMap.values()) {
            System.out.println(task);
        }
        System.out.println("-----------------------------------------------------------------");
        List<Task> tasksHistory = newTaskManager.getHistory();
        for (Task task : tasksHistory) {
            System.out.println(task);
        }
        System.out.println("-----------------------------------------------------------------");
    }

    public void loadFromFile() {
        final List<String> taskData = getSaveData(file).get("Tasks");
        final List<String> historyData = getSaveData(file).get("History");

        for (String line : taskData) {
            final Task task = getTaskFromString(line);

            if (task.getType() == TASK && !getAllTaskTreeMap().containsValue(task)) {
                super.addTask(getTaskFromString(line));
            } else if (task.getType() == EPIC && !getAllTaskTreeMap().containsValue(task)) {
                super.addEpic((Epic) getTaskFromString(line));
            } else if (task.getType() == SUBTASK && !getAllTaskTreeMap().containsValue(task)) {
                super.addSubtask((Subtask) getTaskFromString(line));
            }
        }
        for (String line : historyData) {
            final int id = Integer.parseInt(line);
            if (getAllTaskTreeMap().get(id).getType() == TASK) {
                super.getTaskById(id);
            } else if (getAllTaskTreeMap().get(id).getType() == EPIC) {
                super.getEpicById(id);
            } else if (getAllTaskTreeMap().get(id).getType() == SUBTASK) {
                super.getSubtaskById(id);
            }
        }
    }

    public Map<Integer, Task> getAllTaskTreeMap() {
        final Map<Integer, Task> sortedAllTasks = new TreeMap<>();

        sortedAllTasks.putAll(getAllTasks());
        sortedAllTasks.putAll(getAllEpics());
        sortedAllTasks.putAll(getAllSubtasks());
        return sortedAllTasks;
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            final Map<Integer, Task> tasksToSave = getAllTaskTreeMap();

            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : tasksToSave.values()) {
                fileWriter.write(convertTaskToString(task));
            }
            fileWriter.write("\n" + convertHistoryToString(historyManager));
        } catch (IOException exp) {
            throw new ManagerSaveException("Произошла ошибка во время записи в файл.");
        }
    }

    private String convertTaskToString(Task task) {
        final int id = task.getId();
        final TaskType type = task.getType();
        final String name = task.getName();
        final TaskStatus status = task.getStatus();
        final String description = task.getDescription();

        if (task.getType() != SUBTASK) {
            return String.format("%s,%s,%s,%s,%s,\n", id, type, name, status, description);
        } else {
            int epicId = ((Subtask) task).getEpicId();
            return String.format("%s,%s,%s,%s,%s,%s\n", id, type, name, status, description, epicId);
        }
    }

    private String convertHistoryToString(HistoryManager manager) {
        final List<String> tasksInHistoryId = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            tasksInHistoryId.add(String.valueOf(task.getId()));
        }
        return String.join(",", tasksInHistoryId);
    }

    private Map<String, List<String>> getSaveData(File file) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            final Map<String, List<String>> allData = new HashMap<>();
            final List<String> taskData = new ArrayList<>();
            final List<String> historyData;

            fileReader.readLine();
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                if (!line.isEmpty() || !line.isBlank()) {
                    taskData.add(line);
                } else {
                    break;
                }
            }
            historyData = getHistoryData(fileReader.readLine());
            allData.put("Tasks", taskData);
            allData.put("History", historyData);
            return allData;
        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка во время считывания файла.");
        }
    }

    private List<String> getHistoryData(String line) {
        final List<String> taskInHistoryId = new ArrayList<>();
        final String[] values = line.split(",");

        Collections.addAll(taskInHistoryId, values);
        return taskInHistoryId;
    }

    private Task getTaskFromString(String line) {
        final String[] values = line.split(",");
        switch (values[1]) {
            case "TASK": {
                final int id = Integer.parseInt(values[0]);
                final TaskType type = TaskType.valueOf(values[1]);
                final String name = values[2];
                final TaskStatus status = TaskStatus.valueOf(values[3]);
                final String description = values[4];
                return new Task(id, type, name, description, status);
            }
            case "EPIC": {
                final int id = Integer.parseInt(values[0]);
                final TaskType type = TaskType.valueOf(values[1]);
                final String name = values[2];
                final String description = values[4];
                return new Epic(id, type, name, description);
            }
            case "SUBTASK": {
                final int id = Integer.parseInt(values[0]);
                final TaskType type = TaskType.valueOf(values[1]);
                final String name = values[2];
                final TaskStatus status = TaskStatus.valueOf(values[3]);
                final String description = values[4];
                final int epicId = Integer.parseInt(values[5]);
                return new Subtask(id, type, name, description, status, epicId);
            }
        }
        return null;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        final Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        final Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        final Epic epic = super.getEpicById(id);
        save();
        return epic;
    }
}
