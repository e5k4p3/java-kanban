package yandex;

import interfaces.HistoryManager;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;
import service.Managers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static models.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;
import static models.TaskType.*;

class InMemoryHistoryManagerTest {
    protected Task task;
    protected Epic epic;
    protected Subtask subtask1;
    protected Subtask subtask2;
    protected Subtask subtask3;
    protected List<Task> testHistory;
    protected HistoryManager historyManager;
    protected static final DateTimeFormatter formatter = FileBackedTaskManager.LOCAL_DATE_TIME_FORMATTER;

    @BeforeEach
    public void beforeEach() {
        testHistory = new ArrayList<>();
        historyManager = Managers.getDefaultHistory();
        task = new Task(1, TASK, "Таска", "Описание таски", NEW,
                LocalDateTime.parse("12:03 28.08.2022", formatter), 30L);
        epic = new Epic(2, EPIC, "Эпик", "Описание эпика");
        subtask1 = new Subtask(3, SUBTASK, "Первая сабтаска", "Описание первой сабтаски",
                NEW, 2, LocalDateTime.parse("12:34 28.08.2022", formatter), 10L);
        subtask2 = new Subtask(4, SUBTASK, "Вторая сабтаска", "Описание второй сабтаски",
                NEW, 2, LocalDateTime.parse("12:45 28.08.2022", formatter), 10L);
        subtask3 = new Subtask(5, SUBTASK, "Третья сабтаска", "Описание третьей сабтаски",
                NEW, 2, LocalDateTime.parse("12:56 28.08.2022", formatter), 10L);
    }

    @Test
    public void getHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
        testHistory.add(epic);
        testHistory.add(task);
        testHistory.add(subtask1);
        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(subtask1);
        assertEquals(testHistory, historyManager.getHistory());
    }

    @Test
    public void add() {
        testHistory.add(epic);
        testHistory.add(task);
        testHistory.add(subtask1);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask1);
        assertNotEquals(testHistory, historyManager.getHistory());
        historyManager.add(epic);
        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(subtask1);
        assertEquals(testHistory, historyManager.getHistory());
    }

    @Test
    public void remove() {
        assertDoesNotThrow(() -> historyManager.remove(78));
        testHistory.add(epic);
        testHistory.add(task);
        testHistory.add(subtask1);
        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(subtask1);
        assertEquals(testHistory, historyManager.getHistory());
        historyManager.remove(epic.getId());
        assertNotEquals(testHistory, historyManager.getHistory());
        testHistory.remove(epic);
        assertEquals(testHistory, historyManager.getHistory());
        historyManager.remove(subtask1.getId());
        assertNotEquals(testHistory, historyManager.getHistory());
        testHistory.remove(subtask1);
        assertEquals(testHistory, historyManager.getHistory());
        historyManager.remove(task.getId());
        assertTrue(historyManager.getHistory().isEmpty());
    }
}