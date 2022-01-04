package no.eksamenPGR203.database;

import javax.sql.DataSource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDao extends AbstractDao<Task>{

    public TaskDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(Task task) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO task (project_id, task_name, task_description, due_date, status) values (?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    )) {
                statement.setInt(1, task.getProjectId());
                statement.setString(2, task.getTaskName());
                statement.setString(3, task.getTaskDescription());
                statement.setString(4, task.getDueDate());
                statement.setString(5, task.getStatus());
                statement.executeUpdate();
                try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    task.setTaskId(generatedKeys.getInt("task_id"));
                }
            }
        }
    }

    public Task retrieve(int taskId) throws SQLException, IOException, NoSuchAlgorithmException {
        return super.retrieve(taskId, "SELECT * FROM task WHERE task_id = ?");
    }

    public void delete(int id) throws SQLException {
        super.delete(id, "DELETE FROM task WHERE task_id = ?");
    }

    public List<Task> list() throws SQLException, IOException, NoSuchAlgorithmException {
        return super.list("task");
    }

    public void addMemberToTask(int projectmemberId, int taskId) throws SQLException {
        super.createRelation(projectmemberId, taskId, "INSERT INTO member_task_relation (projectmember_Id, task_id) values (?, ?)");
    }

    public void removeMemberFromTask(int projectMemberId, int taskId) throws SQLException {
        super.createRelation(projectMemberId, taskId, "DELETE FROM member_task_relation WHERE projectmember_id = ? AND task_id = ?");
    }
    public void removeAllMembersFromTask(int id) throws SQLException {
        super.delete(id, "DELETE FROM member_task_relation WHERE task_id = ?");
    }
    public void removeAllTasksFromMember(int projectMemberId) throws SQLException {
        super.delete(projectMemberId, "DELETE FROM member_task_relation WHERE projectmember_id = ?");
    }
    public void updateStatusOnTask(int taskId, String newStatus) throws SQLException {
        super.updateTable(taskId, newStatus, "UPDATE task SET status = (?) WHERE task_name = '" + taskId + "'");
    }

    public List<Task> listAMembersTasks(int projectmemberId) throws SQLException, IOException, NoSuchAlgorithmException {
        return super.listObjectRelations(projectmemberId,
                "SELECT * FROM task t " +
                "JOIN member_task_relation mtr ON t.task_id = mtr.task_id " +
                "JOIN projectmembers pm ON mtr.projectmember_id = pm.projectmember_id " +
                "WHERE pm.projectmember_id = ?");
    }

    public List<Task> listTasksInProject(int projectId) throws SQLException, IOException, NoSuchAlgorithmException {
        return super.listObjectRelations(projectId,
                "SELECT t.* FROM task t " +
                "JOIN projects p ON p.project_id = t.project_id " +
                "WHERE p.project_id = ?");
    }

    @Override
    Task mapRow(ResultSet rs) throws SQLException, IOException {
        Task task = new Task(
                rs.getString("task_name"),
                rs.getString("task_description"),
                rs.getString("due_date"),
                rs.getInt("project_id"),
                rs.getString("status")
        );
        task.setTaskId(rs.getInt("task_id"));
        return task;
    }

    public void update(int id, String taskName, String taskDescription, String dueDate, String status) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection
                    .prepareStatement("UPDATE task SET task_name = ?, task_description = ?, due_date = ?, status = ? WHERE task_id = ?")) {
                statement.setString(1, taskName);
                statement.setString(2, taskDescription);
                statement.setString(3, dueDate);
                statement.setString(4, status);
                statement.setInt(5, id);
                statement.executeUpdate();
            }
        }
    }

    public List<Task> getTasksFromStatus(String status) throws SQLException, IOException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM task WHERE status = ?")) {
                statement.setString(1, status);
                try (ResultSet rs = statement.executeQuery()) {
                    List<Task> task = new ArrayList<>();
                    while (rs.next()) {
                        task.add(mapRow(rs));
                    }
                    return task;
                }
            }
        }
    }
}
