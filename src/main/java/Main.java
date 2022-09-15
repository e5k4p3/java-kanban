import models.Epic;
import models.Subtask;
import models.Task;
import service.FileBackedTaskManager;
import service.HttpTaskManager;
import service.Managers;
import service.http.HttpTaskServer;
import service.http.KVClient;
import service.http.KVServer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static models.TaskStatus.*;
import static models.TaskType.*;
import static service.FileBackedTaskManager.LOCAL_DATE_TIME_FORMATTER;

public class Main {

    public static void main(String[] args) {
        try {
            KVServer kvServer = new KVServer();
            kvServer.start();
            KVClient kvClient1 = new KVClient("http://localhost:8078");
            KVClient kvClient2 = new KVClient("http://localhost:8078");
            HttpTaskManager taskManager = Managers.getDefaultHttp(kvClient1, "asd");


            Task task1 = new Task(taskManager.getNewId(), TASK, "Первая таска", "Описание первой таски", NEW,
                    LocalDateTime.parse("09:10 11.07.1995", LOCAL_DATE_TIME_FORMATTER), 30L);
            Task task2 = new Task(taskManager.getNewId(), TASK, "Вторая таска", "Описание второй таски", NEW,
                    LocalDateTime.parse("09:20 11.07.1995", LOCAL_DATE_TIME_FORMATTER), 40L);
            taskManager.addTask(task1);
            taskManager.addTask(task2);
            Epic epic1 = new Epic(taskManager.getNewId(), EPIC, "Первый эпик", "Описание первого эпика");
            taskManager.addEpic(epic1);
            Subtask subtask1 = new Subtask(taskManager.getNewId(), SUBTASK, "Первая сабтаска", "Описание первой сабтаски",
                    NEW, epic1.getId(), LocalDateTime.parse("18:20 11.07.1995", LOCAL_DATE_TIME_FORMATTER), 30L);
            Subtask subtask2 = new Subtask(taskManager.getNewId(), SUBTASK, "Вторая сабтаска", "Описание второй сабтаски",
                    IN_PROGRESS, epic1.getId(), LocalDateTime.parse("19:20 11.07.1995", LOCAL_DATE_TIME_FORMATTER), 10L);
            Subtask subtask3 = new Subtask(taskManager.getNewId(), SUBTASK, "Третья сабтаска", "Описание третьей сабтаски",
                    DONE, epic1.getId(), LocalDateTime.parse("20:20 11.07.1995", LOCAL_DATE_TIME_FORMATTER), 15L);
            taskManager.addSubtask(subtask1);
            taskManager.addSubtask(subtask2);
            taskManager.addSubtask(subtask3);
            Epic epic2 = new Epic(taskManager.getNewId(), EPIC, "Второй эпик", "Описание второго эпика");
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


            HttpTaskServer taskServer = new HttpTaskServer(taskManager, 8080);
            HttpTaskManager taskManager1 = new HttpTaskManager(kvClient2, "asd");
            taskManager1.loadFromServer();
            HttpTaskServer taskServer1 = new HttpTaskServer(taskManager1, 8081);
            taskServer.startServer();
            taskServer1.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }


        /* FileBackedTaskManager taskManager = new FileBackedTaskManager(new File("src/main/java/resources/save.csv"));
        Task task1 = new Task(getNewId(), TASK, "Первая таска", "Описание первой таски", NEW,
                LocalDateTime.parse("09:10 11.07.1995", LOCAL_DATE_TIME_FORMATTER), 30L);
        Task task2 = new Task(getNewId(), TASK, "Вторая таска", "Описание второй таски", NEW,
                LocalDateTime.parse("09:20 11.07.1995", LOCAL_DATE_TIME_FORMATTER), 40L);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Epic epic1 = new Epic(getNewId(), EPIC, "Первый эпик", "Описание первого эпика");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask(getNewId(), SUBTASK, "Первая сабтаска", "Описание первой сабтаски",
                NEW, epic1.getId(), LocalDateTime.parse("18:20 11.07.1995", LOCAL_DATE_TIME_FORMATTER), 30L);
        Subtask subtask2 = new Subtask(getNewId(), SUBTASK, "Вторая сабтаска", "Описание второй сабтаски",
                IN_PROGRESS, epic1.getId(), LocalDateTime.parse("19:20 11.07.1995", LOCAL_DATE_TIME_FORMATTER), 10L);
        Subtask subtask3 = new Subtask(getNewId(), SUBTASK, "Третья сабтаска", "Описание третьей сабтаски",
                DONE, epic1.getId(), LocalDateTime.parse("20:20 11.07.1995", LOCAL_DATE_TIME_FORMATTER), 15L);
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

        FileBackedTaskManager newTaskManager = Managers.getDefaultFileBacked(new File("src/main/java/resources/save.csv"));
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
        for (Task task : taskManager.getPrioritizedTasks()) {
            System.out.println(task);
        } */
    }
}
