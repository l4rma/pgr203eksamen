package no.eksamenPGR203;

import no.eksamenPGR203.database.*;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectMemebersDaoTest {
    private JdbcDataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Test
    void shouldInsertMemberInDatabase() throws SQLException, IOException, NoSuchAlgorithmException {
        ProjectMembersDao projectMembersDao = new ProjectMembersDao(dataSource);

        projectMembersDao.insert(testProjectMember());
        System.out.println(testProjectMember().getHashedPw());
        String pw= "password123";
        System.out.println(pw.hashCode());
        System.out.println(projectMembersDao.getPassword("mymail@mail.no"));

        StringBuilder asdf = new StringBuilder();
        List<ProjectMember> liste;
        liste = projectMembersDao.list();
        for(ProjectMember member : liste) {
            asdf.append(member.getFirstName() + " ");
        }
        assertThat(asdf.toString()).contains("Fornavn");

    }

    public ProjectMember testProjectMember() throws NoSuchAlgorithmException {
        ProjectMember projectMember = new ProjectMember("Fornavn", "Etternavn","mymail@mail.no", "password123");
        return projectMember;
    }

    @Test
    void shouldCreateProject() throws SQLException, IOException, NoSuchAlgorithmException {
        ProjectDao projectDao = new ProjectDao(dataSource);
        Project project = new Project("Test Prosjekt", "Lever ALLE gavene", "24.12.20");
        projectDao.insert(project);

        StringBuilder asdf = new StringBuilder();
        List<Project> liste;
        liste = projectDao.list();
        for(Project p : liste) {
            asdf.append(p.getProjectName() + " ");
        }
        assertThat(asdf.toString()).contains("Test Prosjekt");


    }

    @Test
    void shouldAddAndListMembersOnProject() throws SQLException, NoSuchAlgorithmException, IOException {
        ProjectDao projectDao = new ProjectDao(dataSource);
        TaskDao taskDao = new TaskDao(dataSource);
        Project project = new Project("Alle skal med", "Medlemmer skal bli added her", "01.01.21");
        projectDao.insert(project);
        ProjectMember member = new ProjectMember("Person", "Person", "test@person.no", "pAssword1");
        ProjectMember member2 = new ProjectMember("Person2", "Person", "test2@person.no", "pASsword1");
        ProjectMember member3 = new ProjectMember("Person3", "Person", "test3@person.no", "pASSword1");
        ProjectMembersDao projectMembersDao = new ProjectMembersDao(dataSource);
        projectMembersDao.insert(member);
        projectMembersDao.insert(member2);
        projectMembersDao.insert(member3);

        projectDao.addMemberToProject(member.getId(), project.getId());
        projectDao.addMemberToProject(member2.getId(), project.getId());
        projectDao.addMemberToProject(member3.getId(), project.getId());
        System.out.println(projectMembersDao.listMembersInProject(project.getId()));

    }
}
