package service;

import interfaces.HistoryManager;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node<Task>> tasksInHistory;
    private Node<Task> head;
    private Node<Task> tail;

    public InMemoryHistoryManager() {
        tasksInHistory = new HashMap<>();
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> tasksInHistoryList = new ArrayList<>();
        Node<Task> curHead = head;

        while (curHead != null) {
            tasksInHistoryList.add(curHead.data);
            curHead = curHead.next;
        }

        return tasksInHistoryList;
    }

    @Override
    public void remove(int id) {
        if (tasksInHistory.containsKey(id)) {
            removeNode(tasksInHistory.get(id));
            tasksInHistory.remove(id);
        }
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            final Node<Task> prev = node.prev;
            final Node<Task> next = node.next;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                node.prev = null;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }

            node.data = null;
        }
    }

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(task, oldTail, null);
        tail = newNode;

        tasksInHistory.put(task.getId(), newNode);

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    private static class Node<Task> {
        public Task data;
        public Node<Task> prev;
        public Node<Task> next;

        public Node(Task data, Node<Task> prev, Node<Task> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }
}
