package service.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import models.*;
import service.FileBackedTaskManager;
import service.HttpTaskManager;
import service.http.adapters.DurationAdapter;
import service.http.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final HttpTaskManager taskManager;
    private final HttpServer httpServer;
    private final Gson gson;

    public HttpTaskServer(HttpTaskManager taskManager, int port) throws IOException {
        this.taskManager = taskManager;
        this.httpServer = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        httpServer.createContext("/tasks", new TasksHandler<>());
    }

    public void startServer() {
        httpServer.start();
    }

    public void stopServer(int delay) {
        httpServer.stop(delay);
    }

    class TasksHandler<T extends Task> implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                String method = httpExchange.getRequestMethod();
                String path = httpExchange.getRequestURI().getPath();
                String query = httpExchange.getRequestURI().getQuery();
                String[] splitPath = path.split("/");

                switch(HttpMethods.valueOf(method)) {
                    case GET:
                        if (query == null) {
                            getMethodsWithoutQuery(httpExchange, splitPath);
                        } else {
                            getMethodsWithQuery(httpExchange, splitPath, query);
                        }
                        break;
                    case POST:
                        postMethods(httpExchange, splitPath);
                        break;
                    case DELETE:
                        if (query == null) {
                            deleteMethodsWithoutQuery(httpExchange, splitPath);
                        } else {
                            deleteMethodsWithQuery(httpExchange, splitPath, query);
                        }
                        break;
                    default:
                        httpExchange.sendResponseHeaders(501, 0);
                }
            } catch (IOException e) {
                System.out.println("Во время запроса произошла ошибка.");
                e.printStackTrace();
            } finally {
                httpExchange.close();
            }
        }

        private void getMethodsWithoutQuery(HttpExchange httpExchange, String[] splitPath) throws IOException {
            if (splitPath.length == 2) {
                sendJson(httpExchange, gson.toJson(taskManager.getPrioritizedTasks()));
            }
            switch (splitPath[2]) {
                case "history":
                    sendJson(httpExchange, gson.toJson(taskManager.getHistory()));
                    break;
                case "task":
                    sendJson(httpExchange, gson.toJson(taskManager.getAllTasks()));
                    break;
                case "subtask":
                    sendJson(httpExchange, gson.toJson(taskManager.getAllSubtasks()));
                    break;
                case "epic":
                    sendJson(httpExchange, gson.toJson(taskManager.getAllEpics()));
                    break;
                default:
                    httpExchange.sendResponseHeaders(400, 0);
                    break;
            }
        }

        private void getMethodsWithQuery(HttpExchange httpExchange, String[] splitPath, String query) throws IOException {
            try {
                int id = getParametersFromQuery(query);
                switch (splitPath[2]) {
                    case "task":
                        if (!taskManager.getAllTasks().containsKey(id)) {
                            httpExchange.sendResponseHeaders(404, 0);
                        } else {
                            sendJson(httpExchange, gson.toJson(taskManager.getTaskById(id)));
                        }
                        break;
                    case "subtask":
                        if (splitPath[splitPath.length - 1].equals("epic")) {
                            if (!taskManager.getAllEpics().containsKey(id)) {
                                httpExchange.sendResponseHeaders(404, 0);
                            } else {
                                sendJson(httpExchange, gson.toJson(taskManager.getEpicById(id).getListOfSubtasks()));
                            }
                            break;
                        }
                        if (!taskManager.getAllSubtasks().containsKey(id)) {
                            httpExchange.sendResponseHeaders(404, 0);
                        } else {
                            sendJson(httpExchange, gson.toJson(taskManager.getSubtaskById(id)));
                        }
                        break;
                    case "epic":
                        if (!taskManager.getAllEpics().containsKey(id)) {
                            httpExchange.sendResponseHeaders(404, 0);
                        } else {
                            sendJson(httpExchange, gson.toJson(taskManager.getEpicById(id)));
                        }
                        break;
                    default:
                        httpExchange.sendResponseHeaders(400, 0);
                        break;
                }
            } catch (NumberFormatException e) {
                httpExchange.sendResponseHeaders(400, 0);
            }
        }

        private void postMethods(HttpExchange httpExchange, String[] splitPath) throws IOException {
            InputStream inputStream = httpExchange.getRequestBody();
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            inputStream.close();

            try {
                switch(splitPath[2]) {
                    case "task":
                        Task task = gson.fromJson(json, Task.class);
                        if (taskManager.getAllTasks().containsKey(task.getId())) {
                            taskManager.updateTask(task);
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            taskManager.addTask(gson.fromJson(json, Task.class));
                            httpExchange.sendResponseHeaders(201, 0);
                        }
                        break;
                    case "subtask":
                        Subtask subtask = gson.fromJson(json, Subtask.class);
                        if (taskManager.getAllSubtasks().containsKey(subtask.getId())) {
                            taskManager.updateSubtask(subtask);
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            taskManager.addSubtask(gson.fromJson(json, Subtask.class));
                            httpExchange.sendResponseHeaders(201, 0);
                        }
                        break;
                    case "epic":
                        Epic epic = gson.fromJson(json, Epic.class);
                        if (taskManager.getAllEpics().containsKey(epic.getId())) {
                            taskManager.updateEpic(epic);
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            taskManager.addEpic(gson.fromJson(json, Epic.class));
                            httpExchange.sendResponseHeaders(201, 0);
                        }
                        break;
                    default:
                        httpExchange.sendResponseHeaders(400, 0);
                        break;
                }
            } catch (NullPointerException | JsonSyntaxException e) {
                httpExchange.sendResponseHeaders(400, 0);
            }
        }

        private void deleteMethodsWithoutQuery(HttpExchange httpExchange, String[] splitPath) throws IOException {
            switch(splitPath[2]) {
                case "task":
                    taskManager.clearAllTasks();
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                case "subtask":
                    taskManager.clearAllSubtasks();
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                case "epic":
                    taskManager.clearAllEpics();
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                default:
                    httpExchange.sendResponseHeaders(400, 0);
            }
        }

        private void deleteMethodsWithQuery(HttpExchange httpExchange, String[] splitPath, String query) throws IOException {
            try {
                int id = getParametersFromQuery(query);
                switch(splitPath[2]) {
                    case "task":
                        if (!taskManager.getAllTasks().containsKey(id)) {
                            httpExchange.sendResponseHeaders(404, 0);
                        } else {
                            taskManager.removeTaskById(id);
                            httpExchange.sendResponseHeaders(200, 0);
                        }
                        break;
                    case "subtask":
                        if (!taskManager.getAllSubtasks().containsKey(id)) {
                            httpExchange.sendResponseHeaders(404, 0);
                        } else {
                            taskManager.removeSubtaskById(id);
                            httpExchange.sendResponseHeaders(200, 0);
                        }
                        break;
                    case "epic":
                        if (!taskManager.getAllEpics().containsKey(id)) {
                            httpExchange.sendResponseHeaders(404, 0);
                        } else {
                            taskManager.removeEpicById(id);
                            httpExchange.sendResponseHeaders(200, 0);
                        }
                        break;
                    default:
                        httpExchange.sendResponseHeaders(400, 0);
                        break;
                }
            } catch (NumberFormatException e) {
                httpExchange.sendResponseHeaders(400, 0);
            }
        }

        private int getParametersFromQuery(String query) throws NumberFormatException{
            return Integer.parseInt(query.split("=")[1]);
        }

        private void sendJson(HttpExchange httpExchange, String json) throws IOException {
            httpExchange.sendResponseHeaders(200, json.getBytes().length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(json.getBytes());
            }
        }
    }
}

