package no.eksamenPGR203.controllers.get;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.Project;
import no.eksamenPGR203.database.ProjectDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class OptionTagProjectNamesGetController implements HttpController {


    private ProjectDao projectDao;

    public OptionTagProjectNamesGetController(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        StringBuilder body = new StringBuilder();
        for (Project project : projectDao.list()) {
            body.append("<option value='"+ project.getId() +"'>"+ project.getProjectName()+"</option>");
        }

        HttpResponse response = new HttpResponse(body.toString());
        response.write(clientSocket);
    }
}
