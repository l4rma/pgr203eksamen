package no.eksamenPGR203.controllers.post;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.Task;
import no.eksamenPGR203.database.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class TaskPopulate implements HttpController {

    private TaskDao taskDao;

    public TaskPopulate(TaskDao taskDao) {

        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        String requestbody = request.getBody();
        int getTaskid = Integer.parseInt(requestbody.substring(1,requestbody.length()-1));
        Task t = taskDao.retrieve(getTaskid);

        String body = "taskName"+t.getTaskName()+"taskDesc"+t.getTaskDescription()+"taskStatus"+t.getStatus()+"tDate"+t.getDueDate();
        HttpResponse response = new HttpResponse(body);
        response.write(clientSocket);

    }
}
