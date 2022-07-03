package models;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> listOfSubtasksId;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.listOfSubtasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getListOfSubtasksId() {
        return listOfSubtasksId;
    }

    public void setListOfSubtasksId(ArrayList<Integer> listOfSubtasksId) {
        this.listOfSubtasksId = listOfSubtasksId;
    }

    public void clearListOfSubtasksId() {
        listOfSubtasksId.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(listOfSubtasksId, epic.listOfSubtasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), listOfSubtasksId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                "listOfSubtasksId=" + listOfSubtasksId +
                '}';
    }
}
