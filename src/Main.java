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
        Task task3 = new Task("Третья таска", "Описание третьей таски", NEW);
        Task task4 = new Task("Четвертая таска", "Описание четвертой таски", NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addTask(task4);
        System.out.println("Проверка обычных тасков");
        System.out.println("-----------------------------------------------------------------");
        for (Integer id : taskManager.getAllTasks().keySet()) {
            System.out.println(taskManager.getTaskById(id).toString());
        }
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Проверка истории c обычными тасками");
        System.out.println("-----------------------------------------------------------------");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-----------------------------------------------------------------");
        task3.setName("Обновленная третья таска");
        task3.setDescription("Обновленное описание третьей таски");
        task3.setStatus(DONE);
        taskManager.updateTask(task3);
        taskManager.removeTaskById(task2.getId());
        taskManager.removeTaskById(34); // Проверка на NullPointerException
        System.out.println("Проверка обновленных обычных тасков");
        System.out.println("-----------------------------------------------------------------");
        for (Integer id : taskManager.getAllTasks().keySet()) {
            System.out.println(taskManager.getTaskById(id).toString());
        }
        System.out.println("-----------------------------------------------------------------");

        Epic epic1 = new Epic("Первый эпик", "Описание первого эрика");
        Epic epic2 = new Epic("Второй эпик", "Описание второго эпика");
        Epic epic3 = new Epic("Третий эпик", "Описание третьего эпика");
        Subtask subtask1 = new Subtask("Первая сабтаска", "Описание первой сабтаски",
                NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Вторая сабтаска", "Описание второй сабтаски",
                IN_PROGRESS, epic1.getId());
        Subtask subtask3 = new Subtask("Третья сабтаска", "Описание третьей сабтаски",
                DONE, epic1.getId());
        Subtask subtask4 = new Subtask("Четвертая сабтаска", "Описание четвертой сабтаски",
                DONE, epic2.getId());
        Subtask subtask5 = new Subtask("Пятая сабтаска", "Описание пятой сабтаски",
                DONE, epic2.getId());
        Subtask subtask6 = new Subtask("Шестая сабтаска", "Описание шестой сабтаски",
                DONE, epic2.getId());

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        taskManager.addSubtask(subtask4);
        taskManager.addSubtask(subtask5);
        taskManager.addSubtask(subtask6);
        System.out.println("Проверка эпиков и сабтасков");
        System.out.println("-----------------------------------------------------------------");
        for (Integer id : taskManager.getAllEpics().keySet()) {
            System.out.println(taskManager.getEpicById(id));
        }
        for (Integer id : taskManager.getAllSubtasks().keySet()) {
            System.out.println(taskManager.getSubtaskById(id));
        }
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Проверка истории с эпиками и сабтасками");
        System.out.println("-----------------------------------------------------------------");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-----------------------------------------------------------------");
        subtask4.setStatus(IN_PROGRESS);
        taskManager.updateSubtask(subtask4);
        taskManager.removeSubtaskById(subtask1.getId());
        taskManager.removeSubtaskById(213); // Проверка на NullPointerException
        epic3.setName("Новый третий эпик");
        Subtask subtask7 = new Subtask("Седьмая сабтаска", "Описание седьмой сабтаски",
                DONE, epic3.getId());
        Subtask subtask8 = new Subtask("Восьмая сабтаска", "Описание восьмой сабтаски",
                IN_PROGRESS, epic3.getId());
        taskManager.addSubtask(subtask7);
        taskManager.addSubtask(subtask8);
        epic3.setStatus(NEW); // Проверка на ручное изменение статуса эпика
        epic3.setDescription("Новое описание третьего эпика");
        System.out.println("Проверка обновленных эпиков и сабтасков");
        System.out.println("-----------------------------------------------------------------");
        for (Integer id : taskManager.getAllEpics().keySet()) {
            System.out.println(taskManager.getEpicById(id));
        }
        for (Integer id : taskManager.getAllSubtasks().keySet()) {
            System.out.println(taskManager.getSubtaskById(id));
        }
        System.out.println("-----------------------------------------------------------------");
        taskManager.removeEpicById(epic1.getId());
        taskManager.removeEpicById(345); // Проверка на NullPointerException
        System.out.println("Проверка удаления эпика и добавления элементов в эпик");
        System.out.println("-----------------------------------------------------------------");
        for (Integer id : taskManager.getAllEpics().keySet()) {
            System.out.println(taskManager.getEpicById(id));
        }
        for (Integer id : taskManager.getAllSubtasks().keySet()) {
            System.out.println(taskManager.getSubtaskById(id));
        }
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Финальная проверка истории");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-----------------------------------------------------------------");
    }
}
