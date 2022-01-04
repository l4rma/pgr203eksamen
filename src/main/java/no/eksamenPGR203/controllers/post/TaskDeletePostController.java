package no.eksamenPGR203.controllers.post;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.ProjectMembersDao;
import no.eksamenPGR203.database.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class TaskDeletePostController implements HttpController {
    private TaskDao taskDao;
    private ProjectMembersDao projectMembersDao;

    public TaskDeletePostController(TaskDao taskDao, ProjectMembersDao projectMembersDao) {

        this.taskDao = taskDao;
        this.projectMembersDao = projectMembersDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        String rawReqBody = request.getBody();

        int idToDelete = Integer.parseInt(rawReqBody.substring(1,request.getBody().length()-1));
        String taskName = taskDao.retrieve(idToDelete).getTaskName();
        if(!projectMembersDao.listMembersInTask(idToDelete).isEmpty()){
           taskDao.removeAllMembersFromTask(idToDelete);
        }
        taskDao.delete(idToDelete);



        String body =
                "Task " + taskName + " deleted successfully";

        HttpResponse response = new HttpResponse(body);
        response.write(clientSocket);
    }
}
