package no.eksamenPGR203.controllers.get;


import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.*;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class UlTagProjectsGetController implements HttpController {

    private ProjectDao projectDao;
    private TaskDao taskDao;
    private ProjectMembersDao projectMembersDao;

    public UlTagProjectsGetController(ProjectDao projectDao, TaskDao taskDao, ProjectMembersDao projectMembersDao) {

        this.projectDao = projectDao;
        this.taskDao = taskDao;
        this.projectMembersDao = projectMembersDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        StringBuilder body = new StringBuilder();
        body.append("<ul>");
        for (Project p : projectDao.list()) {
            body.append("<li><h2>" + p.getProjectName() + "</h2>" +
                    "Description: " + p.getProjectDescription() +
                    "<br>Due date: " + p.getDueDate() + "</li>");

            body.append("Members: ");
            if (projectMembersDao.listMembersInProject(p.getId()).size() > 0) {
                for (ProjectMember pmember : projectMembersDao.listMembersInProject(p.getId())) {
                    body.append(pmember.getFirstName() + " " + pmember.getLastName() + ", ");
                }
                body.setLength(body.length() - 2); // removes last ", "
            } else {
                body.append("No members");
            }
            body.append("<br>Tasks: ");
            if (taskDao.listTasksInProject(p.getId()).size() > 0) {
                for (Task task : taskDao.listTasksInProject(p.getId())) {
                    body.append("<ul><li>" + task.getTaskName());
                    if (projectMembersDao.listMembersInTask(task.getTaskId()).size() > 0) {
                        body.append(" (");
                        for (ProjectMember prmember : projectMembersDao.listMembersInTask(task.getTaskId())) {
                            body.append(prmember.getFirstName() + " " + prmember.getLastName() + ", ");
                        }
                        body.setLength(body.length() - 2); // removes last ", "
                        body.append(")</li>");
                    }
                    body.append("</ul>");
                }
            } else {
                body.append("No Tasks");
            }
        }
        body.append("</ul>");


        HttpResponse response = new HttpResponse(body.toString());
        response.write(clientSocket);

    }
}
