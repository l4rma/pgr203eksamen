package no.eksamenPGR203.database;

import java.io.IOException;
import java.sql.SQLException;

public class Task {
    private String taskName;
    private String taskDescription;
    private String dueDate;
    private int projectId;
    private String status;
    private int taskId;

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Task(String taskName, String taskDescription, String dueDate, int projectId, String status) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.dueDate = dueDate;
        this.projectId = projectId;
        this.status = status;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }
}
