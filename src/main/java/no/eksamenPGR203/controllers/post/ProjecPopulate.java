package no.eksamenPGR203.controllers.post;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.Project;
import no.eksamenPGR203.database.ProjectDao;
import no.eksamenPGR203.database.Task;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class ProjecPopulate implements HttpController {
    private ProjectDao projectDao;

    public ProjecPopulate(ProjectDao projectDao) {

        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        String requestbody = request.getBody();
        int getProjectId = Integer.parseInt(requestbody.substring(1,requestbody.length()-1));
        Project p = projectDao.retrieve(getProjectId);


        String body = "pName"+p.getProjectName()+"pDesc"+p.getProjectDescription()+"pDate"+p.getDueDate();
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.getBytes().length + "\r\n" +
                "\r\n" +
                body + "\r";
        clientSocket.getOutputStream().write(response.getBytes());
    }
}
