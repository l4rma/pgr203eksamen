package no.eksamenPGR203.database;

import java.util.ArrayList;
import java.util.List;

public class
Project {
    private String projectName;
    private String projectDescription;
    private String dueDate;
    private int id;



    public Project(String projectName, String projectDescription, String dueDate) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.dueDate = dueDate;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public String getDueDate() {
        return dueDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
