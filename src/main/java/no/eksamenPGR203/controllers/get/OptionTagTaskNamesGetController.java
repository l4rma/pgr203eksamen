package no.eksamenPGR203.controllers.get;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.Task;
import no.eksamenPGR203.database.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class OptionTagTaskNamesGetController implements HttpController {
    private TaskDao taskDao;

    public OptionTagTaskNamesGetController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        StringBuilder body = new StringBuilder();
        for (Task task : taskDao.list()) {
            body.append("<option value='" + task.getTaskId() + "'>"+ task.getTaskName()+"</option>");
        }

        HttpResponse response = new HttpResponse(body.toString());
        response.write(clientSocket);
    }
}
