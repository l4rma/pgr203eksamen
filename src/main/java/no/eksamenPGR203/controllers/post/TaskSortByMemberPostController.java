package no.eksamenPGR203.controllers.post;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.QueryString;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.*;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class TaskSortByMemberPostController implements HttpController {

    private TaskDao taskDao;
    private ProjectMembersDao projectMembersDao;

    public TaskSortByMemberPostController(TaskDao taskDao, ProjectMembersDao projectMembersDao) {
        this.projectMembersDao = projectMembersDao;
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        String rawReqBody = request.getBody();
        HashMap<String, String> statusmap = QueryString.queryString(rawReqBody);
        int member = Integer.parseInt(statusmap.get("name"));
        ProjectMember pm = projectMembersDao.retrieve(member);

        List<Task> liste = taskDao.listAMembersTasks(member);
        StringBuilder sb = new StringBuilder();
        sb.append("<html lang='en'>");
        sb.append("<head><meta charset='UTF-8'>");
        sb.append("<title>KristianiaProject</title>");
        sb.append("<link rel='stylesheet' href='style.css'></head>");
        sb.append("<body><h2>Tasks with the member: "+pm.getFirstName()+" "+pm.getLastName()+"</h2><ul>");
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
        sb.append("</ul><br><br><br>Return to <a href='/showTask.html'>tasks</a></body></html>");
        String body = sb.toString();

        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "Content-Length: " + body.getBytes().length + "\r\n" +
                "\r\n" +
                body + "\r";

        clientSocket.getOutputStream().write(response.getBytes());
    }
    }

