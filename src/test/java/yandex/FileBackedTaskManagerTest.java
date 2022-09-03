package yandex;

import interfaces.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;
import service.Managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    protected File file;
    @BeforeEach
    public void beforeEachFileBacked() {
        file = new File("java/yandex/save_test.csv");
        taskManager = Managers.getDefaultFileBacked(file);
        super.beforeEach();
    }

    @Test
    public void save() {
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        try {
            List<String> testSave = Files.readAllLines(Paths.get(String.valueOf(file)));
            List<String> exampleSave = Files.readAllLines(Paths.get("java/yandex/save_example.csv"));
            assertEquals(exampleSave, testSave);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadFromFile() {
        TaskManager newTaskManager = Managers.getDefault();
        newTaskManager.addTask(task);
        newTaskManager.addEpic(epic);
        newTaskManager.addSubtask(subtask1);
        newTaskManager.addSubtask(subtask2);
        newTaskManager.addSubtask(subtask3);
        newTaskManager.getTaskById(task.getId());
        newTaskManager.getEpicById(epic.getId());
        newTaskManager.getSubtaskById(subtask1.getId());
        newTaskManager.getSubtaskById(subtask2.getId());
        newTaskManager.getSubtaskById(subtask3.getId());
        taskManager.loadFromFile();
        assertEquals(newTaskManager.getAllTasks(), taskManager.getAllTasks());
        assertEquals(newTaskManager.getAllEpics(), taskManager.getAllEpics());
        assertEquals(newTaskManager.getAllSubtasks(), taskManager.getAllSubtasks());
        assertEquals(newTaskManager.getHistory(), taskManager.getHistory());
    }
}