package no.eksamenPGR203.controllers.post;


import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.QueryString;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.Task;
import no.eksamenPGR203.database.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

public class TaskUpdateStatusPostController implements HttpController {
    private TaskDao taskDao;

    public TaskUpdateStatusPostController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        HttpResponse response = handle(request);
        response.write(clientSocket);
    }

    public HttpResponse handle(HttpRequest request) throws IOException, SQLException, NoSuchAlgorithmException {
        HashMap<String, String> taskWithNewStatus = QueryString.queryString(request.getBody());

        Task task = taskDao.retrieve(Integer.parseInt(taskWithNewStatus.get("tasks")));
        String newStatus = taskWithNewStatus.get("newStatus");

        taskDao.updateStatusOnTask(task.getTaskId(), newStatus);

        HttpResponse response = new HttpResponse(
                "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<title>KristianiaProject</title>" +
                "<link rel='stylesheet' href='style.css'>" +
                "</head>" +
                "<body>" +
                "<h2>Task status updated!</h2>" +
                "Task name: " + task.getTaskName() + "<br>" +
                "New status: " + newStatus + "<br><br>" +
                "You will be redirected to <a href='/'>main</a> in 5 seconds" +
                "</body></html>"
        );
        response.setFirstLine("HTTP/1.1 201 Created");
        response.putHeader("Refresh", "5; /");
        return response;
    }
}
