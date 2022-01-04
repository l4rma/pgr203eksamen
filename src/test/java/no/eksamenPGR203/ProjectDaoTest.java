package no.eksamenPGR203;

import no.eksamenPGR203.database.Project;
import no.eksamenPGR203.database.ProjectDao;
import no.eksamenPGR203.database.ProjectMember;
import no.eksamenPGR203.database.ProjectMembersDao;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectDaoTest {
    private JdbcDataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
    }
    @Test
    public void shouldRemoveMemberFromProject() throws SQLException, NoSuchAlgorithmException, IOException {
        ProjectDao pd = new ProjectDao(dataSource);
        ProjectMembersDao pmd = new ProjectMembersDao(dataSource);
        Project project1 = new Project("Georgs prosjekt", "Spise mat", "22.04.2016");
        ProjectMember pm = new ProjectMember("Kårebjarne", "Tollefsen", "georgto@me.com", "12345678");
        ProjectMember pm2 = new ProjectMember("Lars", "Magelssen", "lars@me.com", "12345678");
        pd.insert(project1);
        pmd.insert(pm);
        pmd.insert(pm2);
        pd.addMemberToProject(pm.getId(), project1.getId());
        pd.addMemberToProject(pm2.getId(), project1.getId());
        pd.removeMemberFromProject(project1.getId(), pm.getId());
        List<ProjectMember> listen = pmd.listMembersInProject(project1.getId());
        StringBuilder sb = new StringBuilder();
        for(ProjectMember projm : listen){
            sb.append(projm.getFirstName());
        }

        assertTrue(!sb.toString().contains("Kårebjarne"));
    }

    @Test
    public void shouldRetrieveProject() throws SQLException, IOException, NoSuchAlgorithmException {
        ProjectDao pd = new ProjectDao(dataSource);
        Project p = new Project("Georgs prosjekt", "Spise mat", "22.04.2016");
        pd.insert(p);
        assertTrue(pd.retrieve(p.getId()).getProjectName().equals(p.getProjectName()));
    }

    @Test
    public void shouldListApersonsProjects() throws NoSuchAlgorithmException, SQLException, IOException {
        ProjectDao pd = new ProjectDao(dataSource);
        ProjectMembersDao pmd = new ProjectMembersDao(dataSource);
        Project project1 = new Project("Georgs prosjekt", "Spise mat", "22.04.2016");
        Project project2 = new Project("Georgs prosjekt", "Spise mat", "22.04.2016");
        ProjectMember pm = new ProjectMember("Georg", "Tollefsen", "georgto@me.com", "12345678");
        pd.insert(project1);
        pd.insert(project2);
        pmd.insert(pm);
        int personId = pm.getId();
        int projectID = project1.getId();
        pd.addMemberToProject(personId, projectID);
        List<Project> list = pd.listAMembersProject(personId);
        assertTrue(list.get(0).getId() == project1.getId());
    }

    @Test
    public void shouldListMembersInProject() throws SQLException, NoSuchAlgorithmException, IOException {
        ProjectDao pd = new ProjectDao(dataSource);
        ProjectMembersDao pmd = new ProjectMembersDao(dataSource);
        Project project1 = new Project("Georgs prosjekt", "Spise mat", "22.04.2016");
        Project project2 = new Project("Georgs prosjekt", "Spise mat", "22.04.2016");
        ProjectMember pm = new ProjectMember("Georg", "Tollefsen", "georgto@me.com", "12345678");
        ProjectMember pm2 = new ProjectMember("Lars", "Magelssen", "lars@me.com", "12345678");
        ProjectMember pm3 = new ProjectMember("Copper", "hunden", "hunden@me.com", "12345678");
        pd.insert(project1);
        pd.insert(project2);
        pmd.insert(pm);
        pmd.insert(pm2);
        pmd.insert(pm3);
        pd.addMemberToProject(pm.getId(), project1.getId());
        pd.addMemberToProject(pm2.getId(), project1.getId());
        pd.addMemberToProject(pm3.getId(), project1.getId());
        List<ProjectMember> list = pmd.listMembersInProject(project1.getId());
        String a = list.get(0).getFirstName();
        String b = list.get(1).getFirstName();
        String c = list.get(2).getFirstName();
        assertTrue(a.equals(pm.getFirstName()));
        assertTrue(b.equals(pm2.getFirstName()));
        assertTrue(c.equals(pm3.getFirstName()));
        pmd.insert(pm);
    }
    @Test
    public void shouldRemoveAllMembersFromProject() throws SQLException, NoSuchAlgorithmException, IOException {
        ProjectDao pd = new ProjectDao(dataSource);
        ProjectMembersDao pmd = new ProjectMembersDao(dataSource);
        Project project1 = new Project("Georgs prosjekt", "Spise mat", "22.04.2016");
        ProjectMember pm = new ProjectMember("Georg", "Tollefsen", "georgto@me.com", "12345678");
        ProjectMember pm2 = new ProjectMember("Lars", "Magelssen", "lars@me.com", "12345678");
        pd.insert(project1);
        pmd.insert(pm);
        pmd.insert(pm2);
        pd.addMemberToProject(pm.getId(), project1.getId());
        pd.addMemberToProject(pm2.getId(), project1.getId());
        pd.removeAllMembersFromProject(project1.getId());
        List<ProjectMember> list = pmd.listMembersInProject(project1.getId());
        assertTrue(list.isEmpty());
    }
    @Test
    public void shouldUpdateProject() throws SQLException, NoSuchAlgorithmException, IOException {
        ProjectDao pd = new ProjectDao(dataSource);
        Project project1 = new Project("Georgs prosjekt", "Spise mat", "22.04.2016");
        pd.insert(project1);
        pd.update(project1.getId(), "New project", "Nothing", "soon");

        assertTrue(pd.retrieve(project1.getId()).getProjectName().equals("New project"));
    }
}
