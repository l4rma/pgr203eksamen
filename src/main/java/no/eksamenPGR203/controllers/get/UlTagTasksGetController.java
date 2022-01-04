package no.eksamenPGR203.controllers.get;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.ProjectMember;
import no.eksamenPGR203.database.ProjectMembersDao;
import no.eksamenPGR203.database.Task;
import no.eksamenPGR203.database.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class UlTagTasksGetController implements HttpController {
    private TaskDao taskDao;
    private ProjectMembersDao projectMembersDao;

    public UlTagTasksGetController(TaskDao taskDao, ProjectMembersDao projectMembersDao) {
        this.taskDao = taskDao;
        this.projectMembersDao = projectMembersDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        StringBuilder body = new StringBuilder();
        body.append("<ul>");
        for (Task task : taskDao.list()) {
            body.append("<li>Task name: " + task.getTaskName() + "<br>" +
                    "Description: " + task.getTaskDescription() + "<br>" +
                    "Due date: " + task.getDueDate() + "<br>" +
                    "Status: " + task.getStatus() + "</li>");

            body.append("Members in task:<ul>");
            for(ProjectMember member : projectMembersDao.listMembersInTask(task.getTaskId())) {
                body.append("<li>"+ member.getFirstName() + " " + member.getLastName() + "</li>");
            }
            body.append("</ul><br>");
        }

        body.append("</ul><br>");

        HttpResponse response = new HttpResponse(body.toString());
        response.write(clientSocket);
    }
}
