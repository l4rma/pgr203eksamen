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
import java.util.List;

public class ProjectSortMembers implements HttpController {
    private ProjectDao projectDao;
    private ProjectMembersDao projectMembersDao;
    private TaskDao taskDao;

    public ProjectSortMembers(ProjectDao projectDao, ProjectMembersDao projectMembersDao, TaskDao taskDao) {
        this.projectDao = projectDao;
        this.projectMembersDao = projectMembersDao;
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        String rawReqBody = request.getBody();
        int member = Integer.parseInt(rawReqBody.substring(1, 2));

        List<Project> liste = projectDao.listAMembersProject(member);
        StringBuilder sb = new StringBuilder();
        for(Project p : liste){
            sb.append("<ul>");
            sb.append("<li><h2>" + p.getProjectName() + "</h2>" +
                    "Description: " + p.getProjectDescription() +
                    "<br>Due date: " + p.getDueDate() + "</li>");

            sb.append("Members: ");
            if (projectMembersDao.listMembersInProject(p.getId()).size() > 0) {
                for (ProjectMember pmember : projectMembersDao.listMembersInProject(p.getId())) {
                    sb.append(pmember.getFirstName() + " " + pmember.getLastName() + ", ");
                }
                sb.setLength(sb.length() - 2); // removes last ", "
            } else {
                sb.append("No members");
            }
            sb.append("<br>Tasks: ");
            if (taskDao.listTasksInProject(p.getId()).size() > 0) {
                for (Task task : taskDao.listTasksInProject(p.getId())) {
                    sb.append("<ul><li>" + task.getTaskName());
                    if(projectMembersDao.listMembersInTask(task.getTaskId()).size() > 0) {
                        sb.append(" (");
                        for (ProjectMember prmember : projectMembersDao.listMembersInTask(task.getTaskId())) {
                            sb.append(prmember.getFirstName() + " " + prmember.getLastName() + ", ");
                        }
                        sb.setLength(sb.length() - 2); // removes last ", "
                        sb.append(")</li></ul>");
                    }
                }
            } else {
                sb.append("No Tasks");
            }
            sb.append("</ul>");
        }

        HttpResponse response = new HttpResponse(sb.toString());
        response.write(clientSocket);
    }
}
