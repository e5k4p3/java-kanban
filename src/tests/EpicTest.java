package tests;

import models.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static models.TaskType.*;
import static models.TaskStatus.*;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;
    private static final DateTimeFormatter formatter = FileBackedTaskManager.LOCAL_DATE_TIME_FORMATTER;

    @BeforeEach
    public void beforeEach() {
        epic = new Epic(1, EPIC, "Эпик", "Описание эпика");
        subtask1 = new Subtask(2, SUBTASK, "Первая сабтаска", "Описание первой сабтаски",
                NEW, 1, LocalDateTime.parse("10:46 28.08.2022", formatter), 30L);
        subtask2 = new Subtask(3, SUBTASK, "Вторая сабтаска", "Описание второй сабтаски",
                NEW, 1, LocalDateTime.parse("12:46 28.08.2022", formatter), 20L);
    }

    @Test
    public void shouldReturnStatusNewWithEmptyListOfSubtasks() {
        assertEquals(NEW, epic.getStatus());
    }

    @Test
    public void shouldReturnStatusNewWithAllSubtasksStatusNew() {
        epic.addToListOfSubtasks(subtask1);
        epic.addToListOfSubtasks(subtask2);
        assertEquals(NEW, epic.getStatus());
    }

    @Test
    public void shouldReturnStatusDoneWithAllSubtasksStatusDone() {
        subtask1.setStatus(DONE);
        subtask2.setStatus(DONE);
        epic.addToListOfSubtasks(subtask1);
        epic.addToListOfSubtasks(subtask2);
        assertEquals(DONE, epic.getStatus());
    }

    @Test
    public void shouldReturnStatusInProgressWithSubtasksStatusNewAndDone() {
        subtask1.setStatus(NEW);
        subtask2.setStatus(DONE);
        epic.addToListOfSubtasks(subtask1);
        epic.addToListOfSubtasks(subtask2);
        assertEquals(IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldReturnStatusInProgressWithSubtasksStatusInProgress() {
        subtask1.setStatus(IN_PROGRESS);
        subtask2.setStatus(IN_PROGRESS);
        epic.addToListOfSubtasks(subtask1);
        epic.addToListOfSubtasks(subtask2);
        assertEquals(IN_PROGRESS, epic.getStatus());
    }

}