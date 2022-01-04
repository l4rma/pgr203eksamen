package no.eksamenPGR203.controllers.post;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.QueryString;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.*;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

public class ProjectDeletePostController implements HttpController {
    private ProjectDao projectDao;
    private ProjectMembersDao projectMembersDao;
    private TaskDao taskDao;

    public ProjectDeletePostController(ProjectDao projectDao, ProjectMembersDao projectMembersDao, TaskDao taskDao) {

        this.projectDao = projectDao;
        this.projectMembersDao = projectMembersDao;
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        String rawReqBody = request.getBody();
//todo: sjekk om den har kobling for så å slette koblingene
        HashMap<String, String> statusmap = QueryString.queryString(rawReqBody);
        int idToDelete = Integer.parseInt(statusmap.get("value"));
        String projectName = projectDao.retrieve(idToDelete).getProjectName();
        if(projectMembersDao.listMembersInProject(idToDelete).isEmpty()){
            for(Task t : taskDao.listTasksInProject(idToDelete)){
                if (!projectMembersDao.listMembersInTask(t.getTaskId()).isEmpty()) {
                    taskDao.removeAllMembersFromTask(t.getTaskId());
                }
                taskDao.delete(t.getTaskId());
            }
        } else {
        projectDao.removeAllMembersFromProject(idToDelete);
            for(Task t : taskDao.listTasksInProject(idToDelete)){
                if (!projectMembersDao.listMembersInTask(t.getTaskId()).isEmpty()) {
                    taskDao.removeAllMembersFromTask(t.getTaskId());
                }
                taskDao.delete(t.getTaskId());
            }
        }
        projectDao.delete(idToDelete);

        String body =
                "<!DOCTYPE html>" +
                        "<html lang='en'>" +
                        "<head>" +
                        "<meta charset='UTF-8'>" +
                        "<title>KristianiaProject</title>" +
                        "<link rel='stylesheet' href='style.css'>" +
                        "</head>" +
                        "<body>" +
                        "Project <strong>" + projectName + "</strong> deleted successfully<br>" +
                        "You will be redirected to <a href='/'>main</a> in 5 seconds" +
                        "</body></html>";
        HttpResponse response = new HttpResponse(body);
        response.setFirstLine("HTTP/1.1 201 Created");
        response.putHeader("Refresh", "5; /");
        response.write(clientSocket);
    }
}
