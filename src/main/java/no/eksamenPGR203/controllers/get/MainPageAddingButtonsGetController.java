package no.eksamenPGR203.controllers.get;

import no.eksamenPGR203.HttpRequest;
import no.eksamenPGR203.HttpResponse;
import no.eksamenPGR203.controllers.HttpController;
import no.eksamenPGR203.database.ProjectDao;
import no.eksamenPGR203.database.ProjectMembersDao;
import no.eksamenPGR203.database.TaskDao;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class MainPageAddingButtonsGetController implements HttpController {
    private ProjectMembersDao projectMembersDao;
    private ProjectDao projectDao;
    private TaskDao taskDao;

    public MainPageAddingButtonsGetController(ProjectMembersDao projectMembersDao, ProjectDao projectDao, TaskDao taskDao) {
        this.projectMembersDao = projectMembersDao;
        this.projectDao = projectDao;
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpRequest request, Socket clientSocket) throws IOException, SQLException, NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder();
        String createNewMember = "<br><strong>Project Members:<strong><br><a href=\"newProjectMember.html\">Create a project member</a><br>";
        String editMember = "<a href=\"deleteProjectMember.html\">Delete a project member</a><br>";
        String showMembers = "<a href=\"showProjectMembers.html\">Show project members</a><br>";
        String createProject = "<strong>Projects:<strong><br><a href=\"newProject.html\">Create a project</a><br>";
        String editProject = "<a href=\"editProject.html\">Edit a project</a><br>";
        String showProjects = "<a href=\"showProjects.html\">Show projects</a><br>";
        String createTask = "<br><strong>Tasks:<strong><br><a href=\"newTask.html\">Create a task</a><br>";
        String editTask = "<a href=\"editTask.html\">Edit a Task</a><br>";
        String showTask = "<a href=\"showTask.html\">Show Tasks</a><br>";
        String login = "<br>" +
                "<form action='/login' method='post'><button>Sign in</button></form>" +
                "<br>";
        //morsom måte å gjøre det på!
        if(projectMembersDao.list().isEmpty()){
            sb.append(createNewMember).append(login);
        }else if(projectDao.list().isEmpty()){
            sb.append(createProject).append(createNewMember).append(editMember).append(showMembers).append(login);
        }else if(taskDao.list().isEmpty()){
            sb.append(createProject).append(editProject).append(showProjects);
            sb.append(createNewMember).append(editMember).append(showMembers).append(createTask).append(login);
        }else{
            sb.append(createProject).append(editProject).append(showProjects);
            sb.append(createTask).append(editTask).append(showTask);
            sb.append(createNewMember).append(editMember).append(showMembers).append(login);
        }
        HttpResponse response = new HttpResponse(sb.toString());
        response.write(clientSocket);
    }
}
