package tests;

import interfaces.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static models.TaskType.*;
import static models.TaskStatus.*;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest <T extends TaskManager> {
    protected T taskManager;
    protected HashMap<Integer, Task> testTasks;
    protected HashMap<Integer, Subtask> testSubtasks;
    protected HashMap<Integer, Epic> testEpics;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask1;
    protected Subtask subtask2;
    protected Subtask subtask3;
    protected static final DateTimeFormatter formatter = FileBackedTaskManager.LOCAL_DATE_TIME_FORMATTER;

    @BeforeEach
    public void beforeEach() {
        testTasks = new HashMap<>();
        testSubtasks = new HashMap<>();
        testEpics = new HashMap<>();

        task = new Task(1, TASK, "Таска", "Описание таски", NEW,
                LocalDateTime.parse("12:03 28.08.2022", formatter), 30L);
        epic = new Epic(2, EPIC, "Эпик", "Описание эпика");
        subtask1 = new Subtask(3, SUBTASK, "Первая сабтаска", "Описание первой сабтаски",
                NEW, 2, LocalDateTime.parse("12:34 28.08.2022", formatter), 10L);
        subtask2 = new Subtask(4, SUBTASK, "Вторая сабтаска", "Описание второй сабтаски",
                NEW, 2, LocalDateTime.parse("12:45 28.08.2022", formatter), 10L);
        subtask3 = new Subtask(5, SUBTASK, "Первая сабтаска", "Описание первой сабтаски",
                NEW, 2, LocalDateTime.parse("12:56 28.08.2022", formatter), 10L);
        testTasks.put(task.getId(), task);
        testEpics.put(epic.getId(), epic);
        testSubtasks.put(subtask1.getId(), subtask1);
        testSubtasks.put(subtask2.getId(), subtask2);
        testSubtasks.put(subtask3.getId(), subtask3);
    }

    @Test
    public void getAllTasks() {
        taskManager.addTask(task);
        assertEquals(testTasks, taskManager.getAllTasks());
    }

    @Test
    public void getAllSubtasks() {
        taskManager.addSubtask(subtask1);
        assertTrue(taskManager.getAllSubtasks().isEmpty());
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        testSubtasks.put(subtask1.getId(), subtask1);
        testSubtasks.put(subtask2.getId(), subtask2);
        testSubtasks.put(subtask3.getId(), subtask3);
        assertEquals(testSubtasks, taskManager.getAllSubtasks());
    }

    @Test
    public void getAllEpics() {
        taskManager.addEpic(epic);
        testEpics.put(epic.getId(), epic);
        assertEquals(testEpics, taskManager.getAllEpics());
    }

    @Test
    public void clearAllTasks() {
        taskManager.addTask(task);
        taskManager.clearAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    public void clearAllSubtasks() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        assertEquals(testSubtasks, taskManager.getAllSubtasks());
        assertEquals(testEpics, taskManager.getAllEpics());
        taskManager.clearAllSubtasks();
        testSubtasks.clear();
        assertEquals(testSubtasks, taskManager.getAllSubtasks());
        assertEquals(testEpics, taskManager.getAllEpics());
    }

    @Test
    public void clearAllEpics() {
        taskManager.addEpic(epic);
        assertEquals(testEpics, taskManager.getAllEpics());
        taskManager.clearAllEpics();
        testEpics.clear();
        assertEquals(testEpics, taskManager.getAllEpics());
    }

    @Test
    public void clearAllTypesOfTasks() {
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        assertEquals(testTasks, taskManager.getAllTasks());
        assertEquals(testEpics, taskManager.getAllEpics());
        assertEquals(testSubtasks, taskManager.getAllSubtasks());
        taskManager.clearAllTypesOfTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    public void getTaskById() {
        assertNull(taskManager.getTaskById(task.getId()));
        taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertNull(taskManager.getTaskById(99));
    }

    @Test
    public void getSubtaskById() {
        taskManager.addEpic(epic);
        assertNull(taskManager.getSubtaskById(subtask1.getId()));
        taskManager.addSubtask(subtask1);
        assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getId()));
        assertNull(taskManager.getSubtaskById(99));
    }

    @Test
    public void getEpicById() {
        assertNull(taskManager.getEpicById(epic.getId()));
        taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
        assertNull(taskManager.getEpicById(99));
    }

    @Test
    public void addTask() {
        assertDoesNotThrow(() -> taskManager.addTask(null));
        assertTrue(taskManager.getAllTasks().isEmpty());
        taskManager.addTask(task);
        assertEquals(testTasks, taskManager.getAllTasks());
        assertEquals(LocalDateTime.parse("12:03 28.08.2022", formatter),
                taskManager.getTaskById(task.getId()).getStartTime());
        assertEquals(LocalDateTime.parse("12:03 28.08.2022", formatter).plus(task.getDuration()),
                taskManager.getTaskById(task.getId()).getEndTime());
    }

    @Test
    public void addSubtask() {
        assertDoesNotThrow(() -> taskManager.addSubtask(null));
        taskManager.addSubtask(subtask1);
        assertTrue(taskManager.getAllSubtasks().isEmpty());
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        assertEquals(testSubtasks, taskManager.getAllSubtasks());
        assertEquals(LocalDateTime.parse("12:34 28.08.2022", formatter),
                taskManager.getSubtaskById(subtask1.getId()).getStartTime());
        assertEquals(LocalDateTime.parse("12:34 28.08.2022", formatter).plus(subtask1.getDuration()),
                taskManager.getSubtaskById(subtask1.getId()).getEndTime());
    }

    @Test
    public void addEpic() {
        assertDoesNotThrow(() -> taskManager.addEpic(null));
        assertTrue(taskManager.getAllEpics().isEmpty());
        taskManager.addEpic(epic);
        assertEquals(testEpics, taskManager.getAllEpics());
        assertEquals(LocalDateTime.of(9999, 1, 1, 0, 0),
                taskManager.getEpicById(epic.getId()).getStartTime());
        taskManager.addSubtask(subtask1);
        assertEquals(LocalDateTime.parse("12:34 28.08.2022", formatter),
                taskManager.getEpicById(epic.getId()).getStartTime());
        assertEquals(10L, taskManager.getEpicById(epic.getId()).getDuration().toMinutes());
        Subtask newSubtask = new Subtask(6, SUBTASK, "Новая сабтаска", "Описание новой сабтаски",
                DONE, 2, LocalDateTime.parse("10:34 28.08.2022", formatter), 10L);
        taskManager.addSubtask(newSubtask);
        assertEquals(LocalDateTime.parse("10:34 28.08.2022", formatter),
                taskManager.getEpicById(epic.getId()).getStartTime());
        assertEquals(LocalDateTime.parse("12:44 28.08.2022", formatter),
                taskManager.getEpicById(epic.getId()).getEndTime());
        assertEquals(130L, taskManager.getEpicById(epic.getId()).getDuration().toMinutes());
    }

    @Test
    public void updateTask() {
        Task updatedTask = new Task(1, TASK, "Обновленная таска", "Описание обновленной таски", NEW,
                LocalDateTime.parse("22:03 14.03.2053", formatter), 90L);
        assertDoesNotThrow(() -> taskManager.updateTask(updatedTask));
        taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskById(task.getId()));
        taskManager.updateTask(updatedTask);
        assertNotEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(updatedTask, taskManager.getTaskById(task.getId()));
        assertEquals("Обновленная таска", taskManager.getTaskById(task.getId()).getName());
        assertDoesNotThrow(() -> taskManager.updateTask(null));
    }

    @Test
    public void updateSubtask() {
        Subtask updatedSubtask = new Subtask(3, SUBTASK, "Обновленная сабтаска",
                "Описание обновленной сабтаски", DONE, 2,
                LocalDateTime.parse("10:34 28.08.2022", formatter), 10L);
        taskManager.addEpic(epic);
        assertDoesNotThrow(() -> taskManager.updateSubtask(updatedSubtask));
        taskManager.addSubtask(subtask1);
        assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getId()));
        taskManager.updateSubtask(updatedSubtask);
        assertNotEquals(subtask1, taskManager.getSubtaskById(subtask1.getId()));
        assertEquals(updatedSubtask, taskManager.getSubtaskById(subtask1.getId()));
        assertEquals("Обновленная сабтаска", taskManager.getSubtaskById(subtask1.getId()).getName());
        assertDoesNotThrow(() -> taskManager.updateSubtask(null));
    }

    @Test
    public void updateEpic() {
        Epic updatedEpic = new Epic(2, EPIC, "Обновленный эпик", "Описание обновленнего эпика");
        assertDoesNotThrow(() -> taskManager.updateEpic(updatedEpic));
        taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
        taskManager.updateEpic(updatedEpic);
        assertNotEquals(epic, taskManager.getEpicById(epic.getId()));
        assertEquals(updatedEpic, taskManager.getEpicById(epic.getId()));
        assertEquals("Обновленный эпик", taskManager.getEpicById(epic.getId()).getName());
        assertDoesNotThrow(() -> taskManager.updateEpic(null));
    }

    @Test
    public void removeTaskById() {}

    @Test
    public void removeSubtaskById() {}

    @Test
    public void removeEpicById() {}

    @Test
    public void getHistory() {}

    @Test
    public void getPrioritizedTasks() {}

}