package no.eksamenPGR203.controllers.post;


import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.QueryString;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.ProjectDao;
import no.eksamenPGR203.database.Task;
import no.eksamenPGR203.database.TaskDao;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

public class CreateTaskPostController implements HttpController {
    private TaskDao taskDao;
    private ProjectDao projectDao;

    public CreateTaskPostController(TaskDao taskDao, ProjectDao projectDao) {
        this.taskDao = taskDao;
        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        HttpResponse response = handle(request);
        response.write(clientSocket);
    }

    public HttpResponse handle(HttpRequest request) throws IOException, SQLException, NoSuchAlgorithmException {
        HashMap<String, String> newTask = QueryString.queryString(request.getBody());

        Task task = new Task(
                newTask.get("taskName"),
                newTask.get("taskDescription"),
                newTask.get("dueDate"),
                Integer.parseInt(newTask.get("projectId")),
                newTask.get("status")
        );

        taskDao.insert(task);

        HttpResponse response = new HttpResponse("<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<title>KristianiaProject</title>" +
                "<link rel='stylesheet' href='style.css'>" +
                "</head>" +
                "<body>" +
                "Task <strong>" +
                task.getTaskName() +
                "</strong> has been added to project <strong>" +
                projectDao.retrieve(task.getProjectId()).getProjectName() + "</strong><br>" +
                "You will be redirected to <a href='/'>main</a> in 5 seconds" +
                "</body></html>");
        response.setFirstLine("HTTP/1.1 201 Created");
        response.putHeader("Refresh", "5; /");
        return response;
    }
}
