package no.eksamenPGR203;

import no.eksamenPGR203.database.*;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostControllerTests {
    private JdbcDataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Test
    public void shouldSetCookie() throws IOException, NoSuchAlgorithmException, SQLException {
        new HttpServer(20050, dataSource);
        ProjectMember pm = new ProjectMember("Test", "Testesen", "georgto@me.com", "12345678");
        ProjectMembersDao pmda = new ProjectMembersDao(dataSource);
        pmda.insert(pm);
        HttpClient client = new HttpClient("localhost", 20050, "/login", "POST", "emailAddress=georgto%40me.com&password=12345678");
        System.out.println(client.getResponseHeader("Set-Cookie"));
        assertTrue(client.getResponseHeader("Set-Cookie").contains("userId"));
    }
    @Test
    public void shouldRefreshAfterCreatingProject201() throws IOException {
        new HttpServer(20051, dataSource);
        new ProjectDao(dataSource);
        HttpClient client = new HttpClient("localhost", 20051, "/createProject", "POST", "projectName=Mitt+dr%C3%B8mmeprosjekt&projectDescription=Kode+masse+java&date=2020-11-14");
        assertTrue(client.getResponseHeader("Refresh").contains("5; /"));
    }

    @Test
    public void shouldNotRemoveNonExistingMember404() throws IOException, NoSuchAlgorithmException, SQLException {
        new HttpServer(20052, dataSource);

        ProjectMember pm = new ProjectMember("Test", "Testesen", "georgto@me.com", "12345678");
        ProjectMembersDao pmda = new ProjectMembersDao(dataSource);
        ProjectDao pd = new ProjectDao(dataSource);
        Project project = new Project("ABC", "Bygge hus", "213");
        pd.insert(project);
        TaskDao td = new TaskDao(dataSource);
        Task task = new Task("Kjede meg", "ikke gjøre noe", "i morgen", project.getId(), "Done");
        td.insert(task);
        pmda.insert(pm);
        HttpClient client = new HttpClient("localhost", 20052, "/removeMemberFromTask", "POST", "task="+task.getTaskId()+"&projectMember="+pm.getId());
        System.out.println(client.getStatusCode());
        assertTrue(client.getStatusCode().contains("404"));

        td.addMemberToTask(pm.getId(), task.getTaskId());

    }
    @Test
    public void shouldRemoveExistingMember201() throws IOException, NoSuchAlgorithmException, SQLException {
        new HttpServer(20053, dataSource);

        ProjectMember pm = new ProjectMember("Test", "Testesen", "georgto@me.com", "12345678");
        ProjectMembersDao pmda = new ProjectMembersDao(dataSource);
        ProjectDao pd = new ProjectDao(dataSource);
        Project project = new Project("ABC", "Bygge hus", "213");
        pd.insert(project);
        TaskDao td = new TaskDao(dataSource);
        Task task = new Task("Kjede meg", "ikke gjøre noe", "i morgen", project.getId(), "Done");
        td.insert(task);
        pmda.insert(pm);

        td.addMemberToTask(pm.getId(), task.getTaskId());
        HttpClient client = new HttpClient("localhost", 20053, "/removeMemberFromTask", "POST", "task="+task.getTaskId()+"&projectMember="+pm.getId());
        System.out.println(client.getStatusCode());
        assertTrue(client.getStatusCode().contains("201"));
    }


    @Test
    public void shouldSortTaskByMembers() throws SQLException, NoSuchAlgorithmException, IOException {
        new HttpServer(20057, dataSource);
        ProjectDao pd = new ProjectDao(dataSource);
        ProjectMembersDao pmda = new ProjectMembersDao(dataSource);
        TaskDao td = new TaskDao(dataSource);

        ProjectMember pm = new ProjectMember("Test", "Testesen", "georgto@me.com", "12345678");
        Project project = new Project("ABC", "Bygge hus", "213");
        pd.insert(project);
        Task task = new Task("Kjede meg", "ikke gjøre noe", "i morgen", project.getId(), "Done");

        pmda.insert(pm);
        td.insert(task);
        td.addMemberToTask(pm.getId(), task.getTaskId());

        HttpClient client = new HttpClient("localhost", 20057, "/taskSortMember", "post", "name="+pm.getId());
        System.out.println(client.getResponseBody());
        assertTrue(client.getResponseBody().contains("Tasks with the member: "+pm.getFirstName()));

    }

    @Test
    public void shouldEditTaskWithController() throws SQLException, NoSuchAlgorithmException, IOException {
        new HttpServer(20058, dataSource);
        ProjectDao pd = new ProjectDao(dataSource);
        ProjectMembersDao pmda = new ProjectMembersDao(dataSource);
        TaskDao td = new TaskDao(dataSource);

        ProjectMember pm = new ProjectMember("Test", "Testesen", "georgto@me.com", "12345678");
        Project project = new Project("ABC", "Bygge hus", "213");
        pd.insert(project);
        Task task = new Task("Kjede meg", "ikke gjøre noe", "2020-10-31", project.getId(), "Done");

        pmda.insert(pm);
        td.insert(task);
        td.addMemberToTask(pm.getId(), task.getTaskId());
        String newName = "Test";
        HttpClient client = new HttpClient("localhost", 20058, "/editTask", "post", "value="+task.getTaskId()+"&taskName="+newName+"&description=ikke+gj%C3%B8re+noe&status=done&date=2020-10-31");
        assertTrue(client.getResponseBody().contains("Task <strong>"+newName+"</strong> updated Successfully"));

    }

    @Test
    public void controllerShouldCreateProjectMember() throws IOException, NoSuchAlgorithmException, SQLException {
        new HttpServer(11000, dataSource);
        ProjectMembersDao projectMembersDao = new ProjectMembersDao(dataSource);
        new HttpClient("localhost", 11000, "/createProjectMember", "post", "firstName=Person&lastName=Etternavnsen&emailAddress=person%40robot.no&password=passord123");
        boolean present = false;
        for(ProjectMember m : projectMembersDao.list()) {
            if (m.getFirstName().equals("Person")) {
                present = true;
                break;
            }
        }
        assertTrue(present);
    }
    @Test
    public void controllerShouldCreateTask() throws IOException, NoSuchAlgorithmException, SQLException {
        new HttpServer(11001, dataSource);
        ProjectDao projectDao = new ProjectDao(dataSource);
        TaskDao taskDao = new TaskDao(dataSource);
        Project project = new Project("Unit testing", "teste alle kontrollere", "2021-02-01");
        projectDao.insert(project);
        Project p = projectDao.retrieve(project.getId());
        new HttpClient("localhost", 11001, "/createTask", "post", "projectId="+p.getId()+"&taskName=Min+Task&taskDescription=Kode+mer+java&dueDate=2020-11-13&status=to+be+started");

        boolean present = false;
        for(Task t : taskDao.list()) {
            if (t.getTaskName().equals("Min Task")) {
                present = true;
                break;
            }
        }
        assertTrue(present);
    }

    @Test
    public void controllerShouldAddMemberToProject() throws IOException, NoSuchAlgorithmException, SQLException {
        new HttpServer(11002, dataSource);
        ProjectDao projectDao = new ProjectDao(dataSource);
        ProjectMembersDao projectMembersDao = new ProjectMembersDao(dataSource);
        Project project = new Project("Unit testing", "teste alle kontrollere", "2021-02-01");
        projectDao.insert(project);
        Project p = projectDao.retrieve(project.getId());

        ProjectMember projectMember = new ProjectMember("Person", "Etternavnsen", "person@robot.no", "passord123");
        projectMembersDao.insert(projectMember);
        ProjectMember m = projectMembersDao.retrieve(projectMember.getId());

        new HttpClient("localhost", 11002, "/projectAddMember", "post", "project="+p.getId()+"&member="+m.getId());

        boolean present = false;
        for(ProjectMember member : projectMembersDao.listMembersInProject(p.getId())) {
            if (member.getEmailaddress().equals("person@robot.no")) {
                present = true;
                break;
            }
        }
        assertTrue(present);

    }

    @Test
    public void controllerShouldDeleteProject() throws SQLException, IOException, NoSuchAlgorithmException {
        new HttpServer(11003, dataSource);

        ProjectDao projectDao = new ProjectDao(dataSource);
        Project project = new Project("Unit testing", "teste alle kontrollere", "2021-02-01");
        Project project2 = new Project("Unit testing2", "teste alle kontrollere", "2021-02-01");
        projectDao.insert(project);
        projectDao.insert(project2);
        Project p = projectDao.retrieve(project.getId());

        TaskDao taskDao = new TaskDao(dataSource);
        Task task = new Task("Kjede meg", "ikke gjøre noe", "2020-10-31", p.getId(), "Done");
        taskDao.insert(task);

        new HttpClient("localhost", 11003, "/projectDeletion", "post", "value="+p.getId());

        projectDao.insert(project);
        p = projectDao.retrieve(project.getId());

        ProjectMembersDao projectMembersDao = new ProjectMembersDao(dataSource);
        ProjectMember projectMember = new ProjectMember("Person", "Etternavnsen", "person@robot.no", "passord123");
        projectMembersDao.insert(projectMember);
        ProjectMember member = projectMembersDao.retrieve(projectMember.getId());

        new HttpClient("localhost", 11003, "/projectAddMember", "post", "project="+p.getId()+"&member="+projectMember.getId());
        new HttpClient("localhost", 11003, "/deleteProjectMember", "post", "projectMemberId="+member.getId());

        projectMembersDao.insert(projectMember);
        member = projectMembersDao.retrieve(projectMember.getId());
        Task task2 = new Task("Kjede meg", "ikke gjøre noe", "2020-10-31", p.getId(), "Done");
        taskDao.insert(task2);
        Task t = taskDao.retrieve(task2.getTaskId());
        projectDao.addMemberToProject(member.getId(), p.getId());
        taskDao.addMemberToTask(member.getId(), t.getTaskId());

        int antallProsjekter = projectDao.list().size();
        assertEquals(projectDao.list().size(), antallProsjekter);
        new HttpClient("localhost", 11003, "/projectDeletion", "post", "value="+p.getId());
        assertTrue(antallProsjekter>projectDao.list().size());
    }

    @Test
    public void controllerShouldDeleteTask() throws SQLException, IOException, NoSuchAlgorithmException {
        new HttpServer(11004, dataSource);
        // Delete task with no members
        ProjectDao projectDao = new ProjectDao(dataSource);
        Project project = new Project("Unit testing", "teste alle kontrollere", "2021-02-01");
        projectDao.insert(project);
        Project p = projectDao.retrieve(project.getId());

        TaskDao taskDao = new TaskDao(dataSource);
        Task task = new Task("Kjede meg", "ikke gjøre noe", "2020-10-31", p.getId(), "Done");
        taskDao.insert(task);
        Task t = taskDao.retrieve(task.getTaskId());
        new HttpClient("localhost", 11004, "/deleteTask", "post", "'"+t.getTaskId()+"'");

        // Delete task with members

        project = new Project("Unit testing", "teste alle kontrollere", "2021-02-01");
        projectDao.insert(project);
        p = projectDao.retrieve(project.getId());

        taskDao = new TaskDao(dataSource);
        task = new Task("Kjede meg", "ikke gjøre noe", "2020-10-31", p.getId(), "Done");
        taskDao.insert(task);
        t = taskDao.retrieve(task.getTaskId());

        ProjectMembersDao projectMembersDao = new ProjectMembersDao(dataSource);
        ProjectMember projectMember = new ProjectMember("Person", "Etternavnsen", "person@robot.no", "passord123");
        projectMembersDao.insert(projectMember);
        ProjectMember member = projectMembersDao.retrieve(projectMember.getId());

        taskDao.addMemberToTask(member.getId(), t.getTaskId());

        new HttpClient("localhost", 11004, "/deleteTask", "post", "'"+t.getTaskId()+"'");

        boolean present = false;
        for(Task tsk : taskDao.list()) {
            if (tsk.getTaskName().equals("Kjede meg")) {
                present = true;
                break;
            }
        }
        assertTrue(!present);

    }

    @Test
    public void projectSortMembers() throws IOException, NoSuchAlgorithmException, SQLException {
        new HttpServer(30003, dataSource);
        ProjectDao pd = new ProjectDao(dataSource);
        TaskDao td = new TaskDao(dataSource);
        ProjectMembersDao pmd = new ProjectMembersDao(dataSource);
        ProjectMember pm = new ProjectMember("Test", "testesen", "georgto@me.com", "12345678");
        pmd.insert(pm);
        Project pro = new Project("Eksamen", "make it great", "soon");
        pd.insert(pro);
        Task task = new Task("Test", "Teste tester", "soon", pro.getId(), "Soon done" );
        td.insert(task);
        pd.addMemberToProject(pm.getId(), pro.getId());
        td.addMemberToTask(pm.getId(), task.getTaskId());

        HttpClient client = new HttpClient("localhost", 30003, "/projectSortMembers", "POST", "\""+pm.getId()+"\"");

        assertEquals(client.getStatusCode(), "200");

    }
}
