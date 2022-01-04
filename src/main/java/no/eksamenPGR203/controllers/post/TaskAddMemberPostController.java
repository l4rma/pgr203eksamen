package no.eksamenPGR203.controllers.post;
import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.QueryString;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.ProjectMembersDao;
import no.eksamenPGR203.database.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

public class TaskAddMemberPostController implements HttpController {
    private TaskDao taskDao;
    private ProjectMembersDao projectMembersDao;

    public TaskAddMemberPostController(TaskDao taskDao, ProjectMembersDao projectMembersDao) {
        this.taskDao = taskDao;
        this.projectMembersDao = projectMembersDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        HttpResponse response = handle(request);
        response.write(clientSocket);
    }

    public HttpResponse handle(HttpRequest request) throws IOException, SQLException, NoSuchAlgorithmException {
        HashMap<String, String> memberToPutInTask = QueryString.queryString(request.getBody());
        int memberId = Integer.parseInt(memberToPutInTask.get("member"));
        int taskId = Integer.parseInt(memberToPutInTask.get("task"));
        taskDao.addMemberToTask(memberId, taskId);

        HttpResponse response = new HttpResponse(
                "<!DOCTYPE html>" +
                        "<html lang='en'>" +
                        "<head>" +
                        "<meta charset='UTF-8'>" +
                        "<title>KristianiaProject</title>" +
                        "<link rel='stylesheet' href='style.css'>" +
                        "</head>" +
                        "<body>" +
                        "Member <strong>" + projectMembersDao.retrieve(memberId).getFirstName() + " " +
                        projectMembersDao.retrieve(memberId).getLastName() +
                        "</strong> has been added to task<br>" +
                        "You will be redirected to <a href='/'>main</a> in 5 seconds" +
                        "</body></html>"
        );
        response.setFirstLine("HTTP/1.1 201 Created");
        response.putHeader("Refresh", "5; /");
        return response;
    }

}
