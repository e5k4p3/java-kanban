import interfaces.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import service.Managers;

import static models.TaskStatus.*;


public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Первая таска", "Описание первой таски", NEW);
        Task task2 = new Task("Вторая таска", "Описание второй таски", NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Epic epic1 = new Epic("Первый эпик", "Описание первого эпика");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Первая сабтаска", "Описание первой сабтаски",
                NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Вторая сабтаска", "Описание второй сабтаски",
                IN_PROGRESS, epic1.getId());
        Subtask subtask3 = new Subtask("Третья сабтаска", "Описание третьей сабтаски",
                DONE, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        Epic epic2 = new Epic("Второй эпик", "Описание второго эпика");
        taskManager.addEpic(epic2);
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Проверка истории на повторения и порядок");
        System.out.println("-----------------------------------------------------------------");
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
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Проверка истории при удалении таски");
        System.out.println("-----------------------------------------------------------------");
        taskManager.removeTaskById(task2.getId());
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Проверка истории при удалении эпика");
        System.out.println("-----------------------------------------------------------------");
        taskManager.removeEpicById(epic1.getId());
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-----------------------------------------------------------------");
    }
}
