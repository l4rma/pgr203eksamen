package no.eksamenPGR203.controllers.post;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.QueryString;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.ProjectMember;
import no.eksamenPGR203.database.ProjectMembersDao;
import no.eksamenPGR203.database.Task;
import no.eksamenPGR203.database.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class TaskSortByStatusPostController implements HttpController {
    private TaskDao taskDao;
    private ProjectMembersDao projectMembersDao;

    public TaskSortByStatusPostController(TaskDao taskDao, ProjectMembersDao projectMembersDao) {
        this.taskDao = taskDao;
        this.projectMembersDao = projectMembersDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        String rawReqBody = request.getBody();
        HashMap<String, String> statusmap = QueryString.queryString(rawReqBody);
        String status = statusmap.get("status");
        List<Task> liste = taskDao.getTasksFromStatus(status);
        StringBuilder sb = new StringBuilder();
        sb.append("<html lang='en'>");
        sb.append("<head><meta charset='UTF-8'>");
        sb.append("<title>KristianiaProject</title>");
        sb.append("<link rel='stylesheet' href='style.css'></head>");
        sb.append("<h2>Tasks with the status: "+status+"</h2><ul>");
        for (Task t : liste){
            sb.append("<li>Task Name: "+t.getTaskName()+"<br>" +
                    "Task Description: "+t.getTaskDescription()+"<br>"+
                    "Status: "+t.getStatus()+"<br>"+
                    "Projectmembers: ");
            for(ProjectMember p:projectMembersDao.listMembersInTask(t.getTaskId())){
                sb.append(p.getFirstName()+" ");
            }
            sb.append("<br>Due Date: "+t.getDueDate()+ "</li>");
        }
        sb.append("</ul><br><br><br>Return to <a href='/showTask.html'>Tasks</a></body></html>");

        HttpResponse response = new HttpResponse(sb.toString());
        response.write(clientSocket);
    }
}
