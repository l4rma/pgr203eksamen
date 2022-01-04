package no.eksamenPGR203.database;

import javax.sql.DataSource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<T> {
    protected DataSource dataSource;

    public AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public T retrieve(Integer id, String sql) throws SQLException, IOException, NoSuchAlgorithmException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return mapRow(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    public void delete(int id, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection
                    .prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        }
    }

    abstract T mapRow(ResultSet rs) throws SQLException, IOException, NoSuchAlgorithmException;

    public List<T> list(String table) throws SQLException, IOException, NoSuchAlgorithmException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table)) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<T> list = new ArrayList<>();
                    while (rs.next()) {
                        list.add(mapRow(rs));
                    }
                    return list;
                }
            }
        }
    }

    public void createRelation(int object1Id, int object2Id, String sqlStatement) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection
                    .prepareStatement(sqlStatement)) {
                statement.setInt(1, object1Id);
                statement.setInt(2, object2Id);
                statement.executeUpdate();
            }
        }
    }

    public List<T> listObjectRelations(int objectId, String sqlStatement) throws SQLException, IOException, NoSuchAlgorithmException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    sqlStatement)) {
                statement.setInt(1, objectId);
                try (ResultSet rs = statement.executeQuery()) {
                    List<T> list = new ArrayList<>();
                    while (rs.next()) {
                        list.add(mapRow(rs));
                    }
                    return list;
                }
            }
        }
    }

    public void updateTable(int objectId, String cellData, String sqlStatement) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection
                    .prepareStatement(sqlStatement)) {
                statement.setString(1, cellData);
                statement.executeUpdate();
            }
        }
    }

}
