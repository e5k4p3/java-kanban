package models;

import java.util.HashMap;
import java.util.Objects;

import static models.TaskStatus.*;

public class Epic extends Task {
    private HashMap<Integer, Subtask> listOfSubtasks;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.listOfSubtasks = new HashMap<>();
    }


    @Override
    public void setStatus(TaskStatus status) {
    }

    public void updateStatus() {
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

    public HashMap<Integer, Subtask> getListOfSubtasks() {
        return listOfSubtasks;
    }

    public void setListOfSubtasks(HashMap<Integer, Subtask> listOfSubtasks) {
        this.listOfSubtasks = listOfSubtasks;
    }

    public void addToListOfSubtasks(Subtask subtask) {
        listOfSubtasks.put(subtask.getId(), subtask);
    }

    public void removeFromListOfSubtasks(int id) {
        listOfSubtasks.remove(id);
    }

    public void clearListOfSubtasks() {
        listOfSubtasks.clear();
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
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", listOfSubtasks=" + listOfSubtasks +
                '}';
    }
}
