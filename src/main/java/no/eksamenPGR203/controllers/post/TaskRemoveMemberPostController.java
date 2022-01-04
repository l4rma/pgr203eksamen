package no.eksamenPGR203.controllers.post;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.QueryString;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.ProjectMember;
import no.eksamenPGR203.database.ProjectMembersDao;
import no.eksamenPGR203.database.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

public class TaskRemoveMemberPostController implements HttpController {
    private TaskDao taskDao;
    private ProjectMembersDao projectMembersDao;

    public TaskRemoveMemberPostController(TaskDao taskDao, ProjectMembersDao projectMembersDao) {

        this.taskDao = taskDao;
        this.projectMembersDao = projectMembersDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        String rawReqBody = request.getBody();
        HashMap<String, String> statusmap = QueryString.queryString(rawReqBody);
        int taskId = Integer.parseInt(statusmap.get("task"));
        int pmId = Integer.parseInt(statusmap.get("projectMember"));
        String body = "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<title>KristianiaProject</title>" +
                "<link rel='stylesheet' href='style.css'>" +
                "</head>" +
                "<body>";
        for(ProjectMember member : projectMembersDao.listMembersInTask(taskId)) {
            if(member.getId() == pmId) {
                taskDao.removeMemberFromTask(pmId, taskId);
                body += "<!DOCTYPE html>" +
                        "<html lang='en'>" +
                        "<head>" +
                        "<meta charset='UTF-8'>" +
                        "<title>KristianiaProject</title>" +
                        "<link rel='stylesheet' href='style.css'>" +
                        "</head>" +
                        "<body>" +
                        "Project member <strong>" +
                        projectMembersDao.retrieve(pmId).getFirstName() + " " +
                        projectMembersDao.retrieve(pmId).getLastName() +
                        "</strong> successfully deleted from task <strong>" + taskDao.retrieve(taskId).getTaskName() + "</strong><br>" +
                        "You will be redirected to <a href='/'>main</a> in 5 seconds";
                HttpResponse response = new HttpResponse(body);
                response.setFirstLine("HTTP/1.1 201 Created");
                response.putHeader("Refresh", "5; /");
                response.write(clientSocket);
                return;
            }
        }
        body += "No such member in that task<br>" +
                "<a href='/'>main</a>" +
                "</body></html>";
        HttpResponse response = new HttpResponse(body);
        response.setFirstLine("HTTP/1.1 404 Not found");
        String location = request.getHeaders().get("Referer");
        response.putHeader("Refresh", "1; "+ location);

        response.write(clientSocket);
    }
}
