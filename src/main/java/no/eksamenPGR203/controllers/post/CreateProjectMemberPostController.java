package no.eksamenPGR203.controllers.post;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.QueryString;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.ProjectMember;
import no.eksamenPGR203.database.ProjectMembersDao;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

public class CreateProjectMemberPostController implements HttpController {
    private ProjectMembersDao projectMembersDao;

    public CreateProjectMemberPostController(ProjectMembersDao projectMembersDao) {
        this.projectMembersDao = projectMembersDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        HttpResponse response = handle(request);
        response.write(clientSocket);
    }

    public HttpResponse handle(HttpRequest request) throws IOException, SQLException, NoSuchAlgorithmException {
        HashMap<String, String> newMember = QueryString.queryString(request.getBody());
        ProjectMember projectMember = new ProjectMember(newMember.get("firstName"), newMember.get("lastName"), newMember.get("emailAddress"), newMember.get("password"));

        projectMembersDao.insert(projectMember);
        HttpResponse response = new HttpResponse(
                "<!DOCTYPE html>" +
                        "<html lang='en'>" +
                        "<head>" +
                        "<meta charset='UTF-8'>" +
                        "<title>KristianiaProject</title>" +
                        "<link rel='stylesheet' href='style.css'>" +
                        "</head>" +
                        "<body>" +
                        "Project Member <strong>" +
                        projectMember.getFirstName() + " " + projectMember.getLastName() +
                        "</strong> has been created<br>" +
                        "You will be redirected to <a href='/'>main</a> in 5 seconds" +
                        "</body></html>");
        response.setFirstLine("HTTP/1.1 201 Created");
        response.putHeader("Refresh", "5; /");
        return response;
    }
}
