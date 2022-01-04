package no.eksamenPGR203.database;
import javax.sql.DataSource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

public class ProjectDao extends AbstractDao<Project> {

    public ProjectDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(Project project) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO projects (project_name, project_description, due_date) values (?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                            )) {
                statement.setString(1, project.getProjectName());
                statement.setString(2, project.getProjectDescription());
                statement.setString(3, project.getDueDate());
                statement.executeUpdate();
                try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    project.setId(generatedKeys.getInt("project_id"));
                }
            }
        }
    }

    public Project retrieve(Integer id) throws SQLException, IOException, NoSuchAlgorithmException {
        return super.retrieve(id, "SELECT * FROM projects WHERE project_id = (?)");
    }

    public List<Project> list() throws SQLException, IOException, NoSuchAlgorithmException {
        return super.list("projects");
    }

    public void addMemberToProject(int projectmemberId, int projectId) throws SQLException {
        super.createRelation(projectmemberId, projectId, "INSERT INTO member_project_relation (projectmember_id, project_id) values (?, ?)");
    }

    @Override
    Project mapRow(ResultSet rs) throws SQLException {
        Project project = new Project(
                rs.getString("project_name"),
                rs.getString("project_description"),
                rs.getString("due_date")
        );
        project.setId(rs.getInt("project_id"));
        return project;
    }

    public List<Project> listAMembersProject(int projectMemberId) throws SQLException, IOException, NoSuchAlgorithmException {
        return super.listObjectRelations(projectMemberId, "SELECT * FROM projects p " +
                "JOIN member_project_relation mpr ON p.project_id = mpr.project_id " +
                "JOIN projectmembers pm ON mpr.projectmember_id = pm.projectmember_id " +
                "WHERE pm.projectmember_id = ?");
    }

    public void removeMemberFromProject(int projectId, int projectMemberId) throws SQLException {
        super.createRelation(projectMemberId, projectId, "DELETE FROM member_project_relation WHERE project_id = ? AND projectmember_id = ?");
    }
    public void removeAllMembersFromProject(int projectId) throws SQLException {
        super.delete(projectId, "DELETE FROM member_project_relation WHERE project_id = ?");
    }
    public void removeAllTProjectsFromMember(int projectMemberId) throws SQLException {
        super.delete(projectMemberId, "DELETE FROM member_project_relation WHERE projectmember_id = ?");
    }

    public void delete(int id) throws SQLException {
        super.delete(id, "DELETE FROM projects WHERE project_id = ?");
    }

    public void update(int id, String projectName, String projectDescription, String dueDate) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection
                    .prepareStatement("UPDATE projects SET project_name = ?, project_description = ?, due_date = ? WHERE project_id = ?")) {
                statement.setString(1, projectName);
                statement.setString(2, projectDescription);
                statement.setString(3, dueDate);
                statement.setInt(4, id);
                statement.executeUpdate();
            }
        }
    }
}