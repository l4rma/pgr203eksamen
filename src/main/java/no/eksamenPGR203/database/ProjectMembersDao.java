package no.eksamenPGR203.database;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ProjectMembersDao extends AbstractDao<ProjectMember>{

    public ProjectMembersDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(ProjectMember projectMember) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO projectMembers (email_address, first_name, last_name, hashed_pw) values (?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    )) {
                statement.setString(1, projectMember.getEmailaddress());
                statement.setString(2, projectMember.getFirstName());
                statement.setString(3, projectMember.getLastName());
                statement.setString(4, projectMember.getHashedPw());
                statement.executeUpdate();
                try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    projectMember.setId(generatedKeys.getInt("projectmember_id"));
                }
            }
        }
    }

    public ProjectMember retrieve(int projectmemberId) throws SQLException, IOException, NoSuchAlgorithmException {
        return super.retrieve(projectmemberId, "SELECT * FROM projectmembers WHERE projectmember_id = ?");
    }

    public List<ProjectMember> list() throws SQLException, IOException, NoSuchAlgorithmException {
        return super.list("projectMembers");
    }

    public void deleteProjectMember(int id) throws SQLException {
        super.delete(id, "DELETE FROM projectmembers WHERE projectmember_id = ?");
    }

    public List<ProjectMember> listMembersInProject(int project_id) throws SQLException, IOException, NoSuchAlgorithmException {
        return super.listObjectRelations(project_id, "SELECT pm.* FROM projectmembers pm " +
                "JOIN member_project_relation mp ON pm.projectmember_id = mp.projectmember_id " +
                "JOIN projects p ON mp.project_id = p.project_id " +
                "WHERE p.project_id = ?");
    }

    public List<ProjectMember> listMembersInTask(int taskId) throws SQLException, IOException, NoSuchAlgorithmException {
        return super.listObjectRelations(taskId, "SELECT pm.* FROM projectmembers pm " +
                "JOIN member_task_relation mtr ON pm.projectmember_Id = mtr.projectmember_Id " +
                "JOIN task t ON mtr.task_id = t.task_id " +
                "WHERE t.task_id = ?");
    }

    @Override
    ProjectMember mapRow(ResultSet rs) throws SQLException, NoSuchAlgorithmException {
        ProjectMember projectMember = new ProjectMember(
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email_address"),
                rs.getString("hashed_pw")
        );
        projectMember.setId(rs.getInt("projectmember_id"));
        return projectMember;
    }

    public String getPassword(String emailAddress) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM projectmembers WHERE email_address = ?")) {
                statement.setString(1, emailAddress);
                try (ResultSet rs = statement.executeQuery()) {
                    String password = "";
                    while (rs.next()) {
                        password = rs.getString("hashed_pw");

                    }
                    return password;
                }
            }
        }
    }
}
