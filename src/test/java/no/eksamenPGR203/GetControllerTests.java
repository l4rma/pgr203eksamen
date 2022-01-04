package no.eksamenPGR203;

import no.eksamenPGR203.database.*;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetControllerTests {
    private JdbcDataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Test
    public void taskGet() throws IOException, SQLException {
        new HttpServer(30001, dataSource);
        ProjectDao pd = new ProjectDao(dataSource);
        Project pro = new Project("Eksamen", "make it great", "soon");
        pd.insert(pro);
        TaskDao td = new TaskDao(dataSource);
        Task task = new Task("Test", "Teste tester", "soon", pro.getId(), "Soon done" );
        td.insert(task);

        HttpClient client = new HttpClient("localhost", 30001, "/tasks");
        assertTrue(client.getResponseBody().contains("Task name: "+task.getTaskName()+"<br>Description: "+task.getTaskDescription()));
    }

    @Test
    public void getMembers() throws SQLException, IOException, NoSuchAlgorithmException {
        new HttpServer(30002, dataSource);
        ProjectDao pd = new ProjectDao(dataSource);
        TaskDao td = new TaskDao(dataSource);
        ProjectMembersDao pmd = new ProjectMembersDao(dataSource);
        ProjectMember pm = new ProjectMember("Test", "testesen", "georgto@me.com", "12345678");
        pmd.insert(pm);
        Project pro = new Project("Eksamen", "make it great", "soon");
        pd.insert(pro);
        Task task = new Task("Test", "Teste tester", "soon", pro.getId(), "Soon done" );

        td.insert(task);
        HttpClient client = new HttpClient("localhost", 30002, "/projectMembers");
        System.out.println(client.getResponseBody());
        assertTrue(client.getResponseBody().contains("<li>"+pm.getFirstName()+" "+ pm.getLastName()+"</li>"));
    }
    @Test
    public void optionTagCurrentStatus() throws IOException, NoSuchAlgorithmException, SQLException {
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

        HttpClient client = new HttpClient("localhost", 30003, "/tasksWithCurrentStatus");
        System.out.println(client.getResponseBody());
        assertTrue(client.getResponseBody().contains(task.getTaskName()+": "+task.getStatus()));
    }
    @Test
    public void  optionTagTaskStatus() throws IOException, SQLException, NoSuchAlgorithmException {
        new HttpServer(30004, dataSource);
        ProjectDao pd = new ProjectDao(dataSource);
        TaskDao td = new TaskDao(dataSource);
        ProjectMembersDao pmd = new ProjectMembersDao(dataSource);
        ProjectMember pm = new ProjectMember("Test", "testesen", "georgto@me.com", "12345678");
        pmd.insert(pm);
        Project pro = new Project("Eksamen", "make it great", "soon");
        pd.insert(pro);
        Task task = new Task("Test", "Teste tester", "soon", pro.getId(), "Soon done" );
        td.insert(task);

        HttpClient client = new HttpClient("localhost", 30004, "/taskStatus");
        assertTrue(client.getResponseBody().contains("<option>"+task.getStatus() +"</option>"));
    }
    @Test
    public void optionTagTaskNames() throws SQLException, IOException, NoSuchAlgorithmException {
        new HttpServer(30005, dataSource);
        ProjectDao pd = new ProjectDao(dataSource);
        TaskDao td = new TaskDao(dataSource);
        ProjectMembersDao pmd = new ProjectMembersDao(dataSource);
        ProjectMember pm = new ProjectMember("Test", "testesen", "georgto@me.com", "12345678");
        pmd.insert(pm);
        Project pro = new Project("Eksamen", "make it great", "soon");
        pd.insert(pro);
        Task task = new Task("Test", "Teste tester", "soon", pro.getId(), "Soon done" );
        td.insert(task);

        HttpClient client = new HttpClient("localhost", 30005, "/taskNames");
        System.out.println(client.getResponseBody());
        assertTrue(client.getResponseBody().contains(task.getTaskName() +"</option>"));
    }
    @Test
    public void optionProjectNames() throws SQLException, IOException, NoSuchAlgorithmException {
        new HttpServer(30006, dataSource);
        ProjectDao pd = new ProjectDao(dataSource);
        TaskDao td = new TaskDao(dataSource);
        ProjectMembersDao pmd = new ProjectMembersDao(dataSource);
        ProjectMember pm = new ProjectMember("Test", "testesen", "georgto@me.com", "12345678");
        pmd.insert(pm);
        Project pro = new Project("Eksamen", "make it great", "soon");
        pd.insert(pro);
        Task task = new Task("Test", "Teste tester", "soon", pro.getId(), "Soon done" );
        td.insert(task);

        HttpClient client = new HttpClient("localhost", 30006, "/projectNames");
        System.out.println(client.getResponseBody());
        assertTrue(client.getResponseBody().contains(pro.getProjectName()+"</option>"));

    }
    @Test
    public void optionProjectMemberNames() throws SQLException, IOException, NoSuchAlgorithmException {
        new HttpServer(30007, dataSource);
        ProjectDao pd = new ProjectDao(dataSource);
        TaskDao td = new TaskDao(dataSource);
        ProjectMembersDao pmd = new ProjectMembersDao(dataSource);
        ProjectMember pm = new ProjectMember("Test", "testesen", "georgto@me.com", "12345678");
        pmd.insert(pm);
        Project pro = new Project("Eksamen", "make it great", "soon");
        pd.insert(pro);
        Task task = new Task("Test", "Teste tester", "soon", pro.getId(), "Soon done" );
        td.insert(task);

        HttpClient client = new HttpClient("localhost", 30007, "/projectMemberNames");
        System.out.println(client.getResponseBody());
        assertTrue(client.getResponseBody().contains(pm.getFirstName()+" "+pm.getLastName()+"</option>"));
    }
    @Test
    public void shouldAddButtonsToMainPage() throws IOException, SQLException, NoSuchAlgorithmException {
        new HttpServer(20056, dataSource);
        ProjectDao pd = new ProjectDao(dataSource);
        ProjectMembersDao pmda = new ProjectMembersDao(dataSource);
        TaskDao td = new TaskDao(dataSource);

        ProjectMember pm = new ProjectMember("Test", "Testesen", "georgto@me.com", "12345678");
        Project project = new Project("ABC", "Bygge hus", "213");
        pd.insert(project);
        Task task = new Task("Kjede meg", "ikke gjøre noe", "i morgen", project.getId(), "Done");

        pmda.insert(pm);
        td.insert(task);

        HttpClient client = new HttpClient("localhost", 20056, "/mainpageAddingButtons");

        assertTrue(client.getResponseBody().contains("Create a task"));

    }

    @Test
    public void shouldListAllProjects() throws IOException, NoSuchAlgorithmException, SQLException {
        HttpServer server = new HttpServer(20054, dataSource);

        ProjectDao pd = new ProjectDao(dataSource);
        ProjectMembersDao pmda = new ProjectMembersDao(dataSource);
        TaskDao td = new TaskDao(dataSource);

        ProjectMember pm = new ProjectMember("Test", "Testesen", "georgto@me.com", "12345678");
        Project project = new Project("ABC", "Bygge hus", "213");
        pd.insert(project);
        Task task = new Task("Kjede meg", "ikke gjøre noe", "i morgen", project.getId(), "Done");


        pmda.insert(pm);
        td.insert(task);

        HttpClient client = new HttpClient("localhost", 20054, "/project");

        assertTrue(client.getStatusCode().equals("200"));
    }
    @Test
    public void shouldNotAddButtonsToMainPage() throws IOException, SQLException, NoSuchAlgorithmException {
        new HttpServer(20055, dataSource);
        HttpClient client = new HttpClient("localhost", 20055, "/");
        assertTrue(!client.getResponseBody().contains("Create a task"));
    }
}
