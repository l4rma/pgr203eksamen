package no.eksamenPGR203.controllers.post;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.QueryString;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

public class TaskEditPostController implements HttpController {
    private TaskDao taskDao;

    public TaskEditPostController(TaskDao taskDao) {

        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        String rawReqBody = request.getBody();
        HashMap<String, String> statusmap = QueryString.queryString(rawReqBody);
        int taskId = Integer.parseInt(statusmap.get("value"));
        String taskDescription = statusmap.get("description");
        String taskStatus = statusmap.get("status");
        String taskDate = statusmap.get("date");
        String taskName = statusmap.get("taskName");

        taskDao.update(taskId,taskName,taskDescription,taskDate,taskStatus);
        String body ="<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<title>KristianiaProject</title>" +
                "<link rel='stylesheet' href='style.css'>" +
                "</head>" +
                "<body>" +
                "Task <strong>" + taskName + "</strong> updated Successfully<br>" +
                "You will be redirected to <a href='/'>main</a> in 5 seconds" +
                "</body></html>";

        HttpResponse response = new HttpResponse(body);
        response.setFirstLine("HTTP/1.1 201 Created");
        response.putHeader("Refresh", "5; /");
        response.write(clientSocket);

    }
}
