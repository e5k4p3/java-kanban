package interfaces;

import models.Task;

import java.util.List;

public interface HistoryManager<T extends Task> {

    void add(T task);

    void remove(int id);

    List<T> getHistory();
}
