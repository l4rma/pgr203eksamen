package no.eksamenPGR203.controllers.post;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.ProjectMembersDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class LogoutPostController implements HttpController {
    private ProjectMembersDao projectMembersDao;

    public LogoutPostController(ProjectMembersDao projectMembersDao) {
        this.projectMembersDao = projectMembersDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        HttpResponse response = new HttpResponse();
        response.setFirstLine("HTTP/1.1 302 Redirect");
        response.putHeader("Set-Cookie", "userId=LoggedOut; Expires=01.01.2001");
        response.putHeader("Location", "/");
        response.write(clientSocket);
    }
}
