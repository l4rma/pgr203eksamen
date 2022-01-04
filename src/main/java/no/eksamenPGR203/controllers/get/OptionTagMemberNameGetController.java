package no.eksamenPGR203.controllers.get;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.ProjectDao;
import no.eksamenPGR203.database.ProjectMember;
import no.eksamenPGR203.database.ProjectMembersDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class OptionTagMemberNameGetController implements HttpController {
    private ProjectMembersDao projectMembersDao;

    public OptionTagMemberNameGetController(ProjectMembersDao projectMembersDao) {
        this.projectMembersDao = projectMembersDao;

    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        StringBuilder body = new StringBuilder();
        for (ProjectMember member : projectMembersDao.list()) {
            body.append("<option value='" + member.getId() + "'>"+ member.getFirstName() + " " + member.getLastName() + "</option>");
        }
        HttpResponse response = new HttpResponse(body.toString());
        response.write(clientSocket);
    }
}
