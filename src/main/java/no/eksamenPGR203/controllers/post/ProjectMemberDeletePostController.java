package no.eksamenPGR203.controllers.post;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.QueryString;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.ProjectDao;
import no.eksamenPGR203.database.ProjectMember;
import no.eksamenPGR203.database.ProjectMembersDao;
import no.eksamenPGR203.database.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

public class ProjectMemberDeletePostController implements HttpController {
    private ProjectDao projectDao;
    private ProjectMembersDao projectMembersDao;
    private TaskDao taskDao;

    public ProjectMemberDeletePostController(ProjectDao projectDao, ProjectMembersDao projectMembersDao, TaskDao taskDao) {
        this.projectDao = projectDao;
        this.projectMembersDao = projectMembersDao;
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        HttpResponse response = handle(request);
        response.write(clientSocket);
    }
    public HttpResponse handle(HttpRequest request) throws IOException, SQLException, NoSuchAlgorithmException {
        // Get memberId from the request
        HashMap<String, String> memberToDelete = QueryString.queryString(request.getBody());
        // Use id to make a member object
        ProjectMember member = projectMembersDao.retrieve(Integer.parseInt(memberToDelete.get("projectMemberId")));
        // Delete from relation tables where memberId appares
        taskDao.removeAllTasksFromMember(member.getId());
        projectDao.removeAllTProjectsFromMember(member.getId());

        // Delete from member table, member with same ID
        projectMembersDao.deleteProjectMember(member.getId());

        // Create response with feedback
        HttpResponse response = new HttpResponse(
                "<!DOCTYPE html>" +
                        "<html lang='en'>" +
                        "<head>" +
                        "<meta charset='UTF-8'>" +
                        "<title>KristianiaProject</title>" +
                        "<link rel='stylesheet' href='style.css'>" +
                        "</head>" +
                        "<body>" +
                        "Member <strong>" +
                        member.getFirstName() + " " + member.getLastName() +
                        "</strong> has been deleted<br>" +
                        "You will be redirected to <a href='/'>main</a> in 5 seconds" +
                        "</body></html>"
        );
        response.setFirstLine("HTTP/1.1 201 Created");
        response.putHeader("Refresh", "5; /");

        return response;
    }
}
