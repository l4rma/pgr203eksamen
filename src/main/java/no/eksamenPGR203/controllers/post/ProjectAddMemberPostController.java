package no.eksamenPGR203.controllers.post;


import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.QueryString;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.ProjectDao;
import no.eksamenPGR203.database.ProjectMembersDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

public class ProjectAddMemberPostController implements HttpController {
    private ProjectDao projectDao;
    private ProjectMembersDao projectMembersDao;

    public ProjectAddMemberPostController(ProjectDao projectDao, ProjectMembersDao projectMembersDao) {
        this.projectDao = projectDao;
        this.projectMembersDao = projectMembersDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        HttpResponse respond = handle(request);
        respond.write(clientSocket);
    }

    public HttpResponse handle(HttpRequest request) throws IOException, SQLException, NoSuchAlgorithmException {
        HashMap<String, String> memberToPutInProject = QueryString.queryString(request.getBody());
        int memberId = Integer.parseInt(memberToPutInProject.get("member"));
        int projectId = Integer.parseInt(memberToPutInProject.get("project"));

        projectDao.addMemberToProject(memberId, projectId);

        String body = "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta http-equiv='refresh' content='5; URL=/'>" +
                "<title>KristianiaProject</title>" +
                "<link rel='stylesheet' href='style.css'>" +
                "</head>" +
                "<body>" +
                "Member <strong>" + projectMembersDao.retrieve(memberId).getFirstName() + " " +
                projectMembersDao.retrieve(memberId).getLastName() +
                "</strong> has been added to project <strong>" +
                projectDao.retrieve(projectId).getProjectName() +
                "</strong><br>" +
                "You will be redirected to <a href='/'>main</a> in 5 seconds" +
                "</body></html>";

        return new HttpResponse(body);
    }
}
