package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

import static models.TaskStatus.*;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> listOfSubtasks;
    private LocalDateTime endTime;

    public Epic(int id, TaskType type, String name, String description) {
        super(id, type, name, description, TaskStatus.NEW, LocalDateTime.of(9999, 1, 1, 0, 0), 0L);
        this.listOfSubtasks = new HashMap<>();
        this.endTime = startTime.plus(duration);
    }

    @Override
    public void setStatus(TaskStatus status) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("У Epic-ов нельзя менять статус вручную!");
    }

    @Override
    public void setDuration(Long duration) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("У Epic-ов нельзя менять длительность вручную!");
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    public HashMap<Integer, Subtask> getListOfSubtasks() {
        return listOfSubtasks;
    }

    public void addToListOfSubtasks(Subtask subtask) {
        listOfSubtasks.put(subtask.getId(), subtask);
        updateStatus();
        updateStartTime();
        updateEndTime();
        updateDuration();
    }

    public void removeFromListOfSubtasks(int id) {
        listOfSubtasks.remove(id);
        updateStatus();
        updateStartTime();
        updateEndTime();
        updateDuration();
    }

    public void clearListOfSubtasks() {
        listOfSubtasks.clear();
        updateStatus();
        updateStartTime();
        updateEndTime();
        updateDuration();
    }

    private void updateEndTime() {
        if (listOfSubtasks.isEmpty()) {
            endTime = startTime;
        } else {
            endTime = listOfSubtasks.values().stream().max(Comparator.comparing(Subtask::getEndTime)).get().getEndTime();
        }
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
        } else {
            startTime = listOfSubtasks.values().stream().min(Comparator.comparing(Subtask::getStartTime)).get().getStartTime();
        }
    }

    private void updateDuration() {
        if (listOfSubtasks.isEmpty()) {
            duration = Duration.ofMinutes(0L);
        } else {
            duration = Duration.between(startTime, endTime);
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
