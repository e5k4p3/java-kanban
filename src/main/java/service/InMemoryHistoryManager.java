package service;

import models.Task;
import interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager<T> {

    private final HashMap<Integer, Node<T>> tasksInHistory;
    private Node<T> head;
    private Node<T> tail;

    public InMemoryHistoryManager() {
        tasksInHistory = new HashMap<>();
    }

    @Override
    public List<T> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (tasksInHistory.containsKey(id)) {
            removeNode(tasksInHistory.get(id));
            tasksInHistory.remove(id);
        }
    }

    @Override
    public void add(T task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    private void removeNode(Node<T> node) {
        if (node != null) {
            final Node<T> prev = node.prev;
            final Node<T> next = node.next;

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

    private void linkLast(T task) {
        final Node<T> oldTail = tail;
        final Node<T> newNode = new Node<>(task, oldTail, null);
        tail = newNode;

        tasksInHistory.put(task.getId(), newNode);

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    private List<T> getTasks() {
        List<T> tasksInHistoryList = new ArrayList<>();
        Node<T> curHead = head;

        while (curHead != null) {
            tasksInHistoryList.add(curHead.data);
            curHead = curHead.next;
        }

        return tasksInHistoryList;
    }

    private static class Node<T> {
        public T data;
        public Node<T> prev;
        public Node<T> next;

        public Node(T data, Node<T> prev, Node<T> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }
}
