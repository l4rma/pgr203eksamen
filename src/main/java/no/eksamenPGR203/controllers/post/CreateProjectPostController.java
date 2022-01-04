package no.eksamenPGR203.controllers.post;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.QueryString;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.Project;
import no.eksamenPGR203.database.ProjectDao;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;

public class CreateProjectPostController implements HttpController {

    private ProjectDao projectDao;

    public CreateProjectPostController(ProjectDao projectDao) {

        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException {
        HttpResponse response = handle(request);
        response.write(clientSocket);
    }

    public HttpResponse handle(HttpRequest request) throws IOException, SQLException {
        HashMap<String, String> newProject = QueryString.queryString(request.getBody());
        Project project = new Project(newProject.get("projectName"), newProject.get("projectDescription"), newProject.get("date"));

        projectDao.insert(project);

        HttpResponse response = new HttpResponse(
                "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<title>KristianiaProject</title>" +
                "<link rel='stylesheet' href='style.css'>" +
                "</head>" +
                "<body>" +
                "Project <strong>" +
                project.getProjectName() +
                "</strong> has been created<br>" +
                "You will be redirected to <a href='/'>main</a> in 5 seconds" +
                "</body></html>"
        );
        response.setFirstLine("HTTP/1.1 201 Created");
        response.putHeader("Refresh", "5; /");
        return response;
    }
}
