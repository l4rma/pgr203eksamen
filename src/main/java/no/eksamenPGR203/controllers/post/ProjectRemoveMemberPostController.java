package no.eksamenPGR203.controllers.post;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.QueryString;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.ProjectDao;
import no.eksamenPGR203.database.ProjectMember;
import no.eksamenPGR203.database.ProjectMembersDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

public class ProjectRemoveMemberPostController implements HttpController {
    private ProjectDao projectDao;
    private ProjectMembersDao projectMembersDao;

    public ProjectRemoveMemberPostController(ProjectDao projectDao, ProjectMembersDao projectMembersDao) {

        this.projectDao = projectDao;
        this.projectMembersDao = projectMembersDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        HashMap<String, String> statusmap = QueryString.queryString(request.getBody());
        int projectId = Integer.parseInt(statusmap.get("project"));
        int pmId = Integer.parseInt(statusmap.get("projectMember"));
        ProjectMember member = projectMembersDao.retrieve(pmId);
        String body = "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<title>KristianiaProject</title>" +
                "<link rel='stylesheet' href='style.css'>" +
                "</head>" +
                "<body>";

        for(ProjectMember m : projectMembersDao.listMembersInProject(projectId)) {
            if (m.getId() == member.getId()) {
                projectDao.removeMemberFromProject(pmId, projectId);
                body += "Project member <strong>"+projectMembersDao.retrieve(pmId).getFirstName()+ " " +
                        projectMembersDao.retrieve(pmId).getLastName() +
                        "</strong> successfully deleted from project <strong>"+projectDao.retrieve(projectId).getProjectName()+"</strong><br>" +
                        "You will be redirected to <a href='/'>main</a> in 5 seconds" +
                        "</body></html>";
                HttpResponse response = new HttpResponse(body);
                response.setFirstLine("HTTP/1.1 201 Created");
                response.putHeader("Refresh", "5; /");
                response.write(clientSocket);
                return;
            }
        }

        body += "No such member in that project<br>" +
                "<a href='/'>main</a>" +
                "</body></html>";
        HttpResponse response = new HttpResponse(body);
        response.setFirstLine("HTTP/1.1 404 Not found");
        String location = request.getHeaders().get("Referer");
        response.putHeader("Refresh", "1; "+ location);
        response.write(clientSocket);
    }
}
