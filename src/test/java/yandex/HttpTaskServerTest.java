package yandex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HttpTaskManager;
import service.Managers;
import service.http.HttpTaskServer;
import service.http.KVClient;
import service.http.KVServer;
import service.http.adapters.DurationAdapter;
import service.http.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static models.TaskStatus.*;
import static models.TaskType.*;
import static yandex.InMemoryHistoryManagerTest.formatter;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private Task task;
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;
    private Subtask subtask3;
    private Gson gson;
    private KVServer kvServer;
    private KVClient kvClient;
    private HttpTaskManager httpTaskManager;
    private HttpTaskServer httpTaskServer;

    @BeforeEach
    public void beforeEach() {
        task = new Task(1, TASK, "Таска", "Описание таски", NEW,
                LocalDateTime.parse("12:03 28.08.2022", formatter), 30L);
        epic = new Epic(2, EPIC, "Эпик", "Описание эпика");
        subtask1 = new Subtask(3, SUBTASK, "Первая сабтаска", "Описание первой сабтаски",
                NEW, 2, LocalDateTime.parse("12:34 28.08.2022", formatter), 10L);
        subtask2 = new Subtask(4, SUBTASK, "Вторая сабтаска", "Описание второй сабтаски",
                NEW, 2, LocalDateTime.parse("12:45 28.08.2022", formatter), 10L);
        subtask3 = new Subtask(5, SUBTASK, "Третья сабтаска", "Описание третьей сабтаски",
                NEW, 2, LocalDateTime.parse("12:56 28.08.2022", formatter), 10L);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        try {
            kvServer = new KVServer();
            kvServer.start();
            kvClient = new KVClient("http://localhost:8078");
            httpTaskManager = Managers.getDefaultHttp(kvClient, "test");
            httpTaskServer = new HttpTaskServer(httpTaskManager, 8080);
            httpTaskServer.startServer();
        } catch (IOException e) {
            System.out.println("Произошла ошибка в beforeEach");
            e.printStackTrace();
        }
    }

    @AfterEach
    public void afterEach() {
        kvServer.stop(1);
        httpTaskServer.stopServer(1);
    }

    @Test
    public void addAnyTask() { // Вот я написал только этот тест и хотел спросить, нету ли другого способа это делать? А то слишком много выходит, если на все эндпоинты писать
        epic.addToListOfSubtasks(subtask1);
        String taskJson = gson.toJson(task);
        String epicJson = gson.toJson(epic);
        String subtaskJson = gson.toJson(subtask1);
        HttpRequest taskHttpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .build();
        HttpRequest epicHttpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .build();
        HttpRequest subtaskHttpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        try {
            httpClient.send(taskHttpRequest, handler);
            httpClient.send(epicHttpRequest, handler);
            httpClient.send(subtaskHttpRequest, handler);
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка при добавлении тасок в addAnyTask");
            e.printStackTrace();
        }
        String taskJsonFromServer = null;
        String epicJsonFromServer = null;
        String subtaskJsonFromServer = null;
        HttpRequest getTaskHttpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/task?id=1"))
                .build();
        HttpRequest getEpicHttpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/epic?id=2"))
                .build();
        HttpRequest getSubtaskHttpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/subtask?id=3"))
                .build();
        try {
            HttpResponse<String> taskResponse = httpClient.send(getTaskHttpRequest, handler);
            HttpResponse<String> epicResponse = httpClient.send(getEpicHttpRequest, handler);
            HttpResponse<String> subtaskResponse = httpClient.send(getSubtaskHttpRequest, handler);
            taskJsonFromServer = taskResponse.body();
            epicJsonFromServer = epicResponse.body();
            subtaskJsonFromServer = subtaskResponse.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка при получении тасок в addAnyTask");
            e.printStackTrace();
        }
        assertEquals(taskJson, taskJsonFromServer);
        assertEquals(epicJson, epicJsonFromServer);
        assertEquals(subtaskJson, subtaskJsonFromServer);
    }
}
