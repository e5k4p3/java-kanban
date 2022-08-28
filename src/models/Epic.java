package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

import static models.TaskStatus.*;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> listOfSubtasks;

    public Epic(int id, TaskType type, String name, String description) {
        super(id, type, name, description, TaskStatus.NEW, LocalDateTime.of(9999, 1, 1, 0, 0), 0L);
        this.listOfSubtasks = new HashMap<>();
    }

    @Override
    public void setStatus(TaskStatus status) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("У Epic-ов нельзя менять статус вручную!");
    }

    @Override
    public LocalDateTime getEndTime() {
        LocalDateTime endTime = startTime.plus(duration);
        if (listOfSubtasks.isEmpty()) {
            return endTime;
        } else {
            for (Subtask subtask : listOfSubtasks.values()) {
                if (endTime.isBefore(subtask.getEndTime())) {
                    endTime = subtask.getEndTime();
                }
            }
        }
        return endTime;
    }

    @Override
    public Duration getDuration() {
        return Duration.between(startTime, getEndTime());
    }

    public HashMap<Integer, Subtask> getListOfSubtasks() {
        return listOfSubtasks;
    }

    public void addToListOfSubtasks(Subtask subtask) {
        duration = duration.plus(subtask.getDuration());
        listOfSubtasks.put(subtask.getId(), subtask);
        updateStatus();
        updateStartTime();
    }

    public void removeFromListOfSubtasks(int id) {
        duration = duration.minus(listOfSubtasks.get(id).getDuration());
        listOfSubtasks.remove(id);
        updateStatus();
        updateStartTime();
    }

    public void clearListOfSubtasks() {
        listOfSubtasks.clear();
        updateStatus();
        updateStartTime();
    }

    private void updateStatus() {
        int newStatus = 0;
        int doneStatus = 0;

        if (listOfSubtasks != null) {
            for (Integer index : listOfSubtasks.keySet()) {
                if (listOfSubtasks.get(index).getStatus() == NEW) {
                    newStatus++;
                } else if (listOfSubtasks.get(index).getStatus() == DONE) {
                    doneStatus++;
                }
            }
            if (doneStatus == listOfSubtasks.size()) {
                status = DONE;
            } else if (newStatus == listOfSubtasks.size()) {
                status = NEW;
            } else {
                status = IN_PROGRESS;
            }
        } else {
            status = NEW;
        }
    }

    private void updateStartTime() {
        if (listOfSubtasks.isEmpty()) {
            startTime = LocalDateTime.of(9999, 1, 1, 0, 0);
            return;
        }
        for (Subtask subtask : listOfSubtasks.values()) {
            if (startTime.equals(LocalDateTime.of(9999, 1, 1, 0, 0))) {
                startTime = subtask.getStartTime();
            } else if (startTime.isAfter(subtask.getStartTime())) {
                startTime = subtask.getStartTime();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(listOfSubtasks, epic.listOfSubtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), listOfSubtasks);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", type=" + type + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", startTime'" + startTime + '\'' +
                ", duration'" + duration + '\'' +
                ", listOfSubtasks=" + listOfSubtasks +
                '}';
    }
}
