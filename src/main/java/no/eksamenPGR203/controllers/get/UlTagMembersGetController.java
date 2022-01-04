package no.eksamenPGR203.controllers.get;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.ProjectMember;
import no.eksamenPGR203.database.ProjectMembersDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class UlTagMembersGetController implements HttpController {
    private final ProjectMembersDao projectMembersDao;

    public UlTagMembersGetController(ProjectMembersDao projectMembersDao) {
        this.projectMembersDao = projectMembersDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        StringBuilder body = new StringBuilder();
        body.append("<ul>");
        for (ProjectMember member : projectMembersDao.list()) {
            body.append("<li>").append(member.getFirstName()).append(" ").append(member.getLastName()).append("</li>");
        }
        body.append("</ul>");

        HttpResponse response = new HttpResponse(body.toString());
        response.write(clientSocket);
    }
}
