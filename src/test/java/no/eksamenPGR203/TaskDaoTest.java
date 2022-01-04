package no.eksamenPGR203;

import no.eksamenPGR203.database.*;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

public class TaskDaoTest {
    private JdbcDataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Test
    void shouldInsertMembersToATaskAndListThem() throws SQLException, IOException, NoSuchAlgorithmException {
        ProjectDao projectDao = new ProjectDao(dataSource);
        Project project = new Project("Alle skal med", "Medlemmer skal bli added her", "01.01.21");
        projectDao.insert(project);
        ProjectMember member = new ProjectMember("Person", "Person", "test@person.no", "pAssword1");
        ProjectMember member2 = new ProjectMember("Person2", "Person", "test2@person.no", "pASsword1");
        ProjectMember member3 = new ProjectMember("Person3", "Person", "test3@person.no", "pASSword1");
        ProjectMembersDao projectMembersDao = new ProjectMembersDao(dataSource);
        projectMembersDao.insert(member);
        projectMembersDao.insert(member2);
        projectMembersDao.insert(member3);
        TaskDao taskDao = new TaskDao(dataSource);
        Task task = new Task("task name", "task desc", "02-02-22", 1, "Ready to start");
        taskDao.insert(task);
        taskDao.addMemberToTask(member.getId(), task.getTaskId());
        taskDao.addMemberToTask(member3.getId(), task.getTaskId());

        assertThat(projectMembersDao.listMembersInTask(task.getTaskId()).contains("Person"));
        assertThat(projectMembersDao.listMembersInTask(task.getTaskId()).contains("Person3"));
    }

    @Test
    void projectShouldListItsTasks() throws SQLException, IOException, NoSuchAlgorithmException {
        ProjectDao projectDao = new ProjectDao(dataSource);
        Project project = new Project("Alle skal med", "Medlemmer skal bli added her", "01.01.21");
        projectDao.insert(project);
        ProjectMember member = new ProjectMember("Person", "Person", "test@person.no", "pAssword1");
        ProjectMember member2 = new ProjectMember("Person2", "Person", "test2@person.no", "pASsword1");
        ProjectMember member3 = new ProjectMember("Person3", "Person", "test3@person.no", "pASSword1");
        ProjectMembersDao projectMembersDao = new ProjectMembersDao(dataSource);
        projectMembersDao.insert(member);
        projectMembersDao.insert(member2);
        projectMembersDao.insert(member3);
        TaskDao taskDao = new TaskDao(dataSource);
        Task task = new Task("task name", "task desc", "02-02-22", 1, "Ready to start");
        taskDao.insert(task);

        assertThat(taskDao.listTasksInProject(project.getId()).toString().contains(task.getTaskName()));
    }

    @Test
    void shouldListAllTaskFromMember() throws IOException, SQLException, NoSuchAlgorithmException {
        ProjectDao projectDao = new ProjectDao(dataSource);
        Project project = new Project("Alle skal med", "Medlemmer skal bli added her", "01.01.21");
        projectDao.insert(project);

        ProjectMembersDao projectMembersDao = new ProjectMembersDao(dataSource);
        ProjectMember member = new ProjectMember("Person", "Person", "test@person.no", "pAssword1");
        projectMembersDao.insert(member);

        projectDao.addMemberToProject(member.getId(), project.getId());

        TaskDao taskDao = new TaskDao(dataSource);
        Task task = new Task("task number 1", "task desc", "02-02-22", 1, "Ready to start");
        Task task2 = new Task("task number 2", "task desc", "02-02-22", 1, "Ready to start");
        Task task3 = new Task("task number 3", "task desc", "02-02-22", 1, "Ready to start");
        taskDao.insert(task);
        taskDao.insert(task2);
        taskDao.insert(task3);

        taskDao.addMemberToTask(member.getId(), task.getTaskId());
        taskDao.addMemberToTask(member.getId(), task3.getTaskId());

        List<Task> tasksOfMember;
        tasksOfMember = taskDao.listAMembersTasks(member.getId());
        for(Task t : tasksOfMember) {
            System.out.println(t.getTaskName());
        }

        assertThat(taskDao.listAMembersTasks(member.getId()).contains(task));
        assertThat(taskDao.listAMembersTasks(member.getId()).contains(task3));
    }

    @Test
    void shouldUpdateStatusOnTask() throws IOException, SQLException, NoSuchAlgorithmException {
        ProjectDao projectDao = new ProjectDao(dataSource);
        Project project = new Project("Alle skal med", "Medlemmer skal bli added her", "01.01.21");
        projectDao.insert(project);

        TaskDao taskDao = new TaskDao(dataSource);
        Task task = new Task("task number 1", "task desc", "02-02-22", 1, "Ready to start");
        taskDao.insert(task);

        List<Task> list = taskDao.list();
        for(Task t : list) {
            System.out.println(t.getStatus());
        }
        taskDao.updateStatusOnTask(task.getTaskId(), "almost done");
        list = taskDao.list();
        for(Task t : list) {
            System.out.println(t.getStatus());
        }
    }

    //TODO: this test works alone, but not with the rest
@Test
    public void shouldRemoveMemberFromTask() throws IOException, SQLException, NoSuchAlgorithmException {
        ProjectDao pd = new ProjectDao(dataSource);
        TaskDao td = new TaskDao(dataSource);
        ProjectMembersDao pmd = new ProjectMembersDao(dataSource);

        Project p = new Project("TestProject", "Testing", "22.04.2020");
        pd.insert(p);
        Task task = new Task("Test Task", "For testing only", "22.05.2020",1,"WIP");
        td.insert(task);
        td.removeAllMembersFromTask(task.getTaskId());
        ProjectMember pm = new ProjectMember("Georg", "Tollefsen", "georgto@me.com", "12345678");
        pmd.insert(pm);
        td.addMemberToTask(pm.getId(), task.getTaskId());
        assertTrue(!td.listAMembersTasks(pm.getId()).isEmpty());
        td.removeMemberFromTask(pm.getId(), task.getTaskId());
        assertTrue(td.listAMembersTasks(pm.getId()).isEmpty());

}
    @Test
    public void shouldRemoveAllMembersFromTask() throws SQLException, NoSuchAlgorithmException, IOException {
        ProjectDao pd = new ProjectDao(dataSource);
        TaskDao td = new TaskDao(dataSource);
        ProjectMembersDao pmd = new ProjectMembersDao(dataSource);
        Project p = new Project("TestProject", "Testing", "22.04.2020");
        pd.insert(p);
        Task task = new Task("Test Task", "For testing only", "22.05.2020",1,"WIP");
        td.insert(task);
        ProjectMember pm = new ProjectMember("Georg", "Tollefsen", "georgto@me.com", "12345678");
        ProjectMember pm2 = new ProjectMember("Lars", "Heisann", "lars@me.com", "12345678");
        ProjectMember pm3 = new ProjectMember("Muhammed", "Somthing", "muhammed@me.com", "12345678");
        pmd.insert(pm);
        pmd.insert(pm2);
        pmd.insert(pm3);
        td.addMemberToTask(pm.getId(), task.getTaskId());
        td.addMemberToTask(pm2.getId(), task.getTaskId());
        td.addMemberToTask(pm3.getId(), task.getTaskId());
        assertTrue(!td.listAMembersTasks(pm.getId()).isEmpty());
        assertTrue(!td.listAMembersTasks(pm2.getId()).isEmpty());
        assertTrue(!td.listAMembersTasks(pm3.getId()).isEmpty());
        td.removeAllMembersFromTask(task.getTaskId());
        assertTrue(td.listAMembersTasks(pm.getId()).isEmpty());
        assertTrue(td.listAMembersTasks(pm2.getId()).isEmpty());
        assertTrue(td.listAMembersTasks(pm3.getId()).isEmpty());
    }

    @Test
    public void shouldDeleteTask() throws SQLException, IOException, NoSuchAlgorithmException {
        ProjectDao pd = new ProjectDao(dataSource);
        TaskDao td = new TaskDao(dataSource);
        ProjectMembersDao pmd = new ProjectMembersDao(dataSource);
        Project p = new Project("TestProject", "Testing", "22.04.2020");
        pd.insert(p);
        Task t = new Task("Testing123", "Testing", "now", 1, "done");
        td.insert(t);
        assertFalse(td.list().isEmpty());
        td.delete(t.getTaskId());
        List<Task> list = td.list();
        StringBuilder sb = new StringBuilder();
        for(Task ta: list){
            sb.append(ta.getTaskName());
        }
        assertFalse(sb.toString().contains("Testing123"));
    }

    @Test
    public void shouldUpdateTask() throws SQLException, IOException, NoSuchAlgorithmException {
        ProjectDao pd = new ProjectDao(dataSource);
        TaskDao td = new TaskDao(dataSource);
        ProjectMembersDao pmd = new ProjectMembersDao(dataSource);
        Project p = new Project("TestProject", "Testing", "22.04.2020");
        pd.insert(p);
        Task t = new Task("Test", "Testing", "now", 1, "done");
        td.insert(t);
        td.update(t.getTaskId(),"ABC","123", "later", "not done");
        assertTrue(td.retrieve(t.getTaskId()).getTaskName().equals("ABC"));
    }

    @Test
    public void shouldGetTaskFromStatus() throws SQLException, IOException, NoSuchAlgorithmException {
        ProjectDao pd = new ProjectDao(dataSource);
        TaskDao td = new TaskDao(dataSource);

        Project p = new Project("TestProject", "Testing", "22.04.2020");
        pd.insert(p);

        Task t = new Task("Test", "Testing", "now", p.getId(), "done");
        td.insert(t);

        List<Task> list = td.getTasksFromStatus(t.getStatus());
        for(Task task : list) {
            if (task.getTaskId()==t.getTaskId()) {
                assertTrue(task.getTaskId()==t.getTaskId());
            }
        }
    }
}
