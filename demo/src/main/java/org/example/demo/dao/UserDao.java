package org.example.demo.dao;

import org.example.demo.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// DAO (User Access Object, DB의 데이터에 접근하기 위한 객체)
public class UserDao {
    private String jdbcURL = "jdbc:mysql://localhost:3306/demo_db";
    private String jdbcUsername = "root";
    private String jdbcpassword = "Cielo981011:)";

    private static final String INSERT_USER_SQL = "INSERT INTO users (name, email, country) VALUES(?, ?, ?)";

    private static final String SELECT_USER_BY_ID_SQL = "SELECT id, name, email, country FROM users WHERE id = ?";
    private static final String SELECT_ALL_USERS_SQL = "SELECT * FROM users";

    private static final String DELETE_USERS_SQL = "DELETE FROM users WHERE id = ? ";

    private static final String UPDATE_USERS_SQL = "UPDATE users SET name = ?, email = ?, country = ? WHERE id = ?";

    public UserDao() {}

    // DB 연결
    protected Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcpassword);
    }

    // CRUD 실행
    // 1. Create
    public void insertUser(User user) throws SQLException {
        try (Connection connection = getConnection();
             // PreparedStatement 객체를 통해 SQL에 데이터를 바인딩
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setString(3, user.getCountry());

                preparedStatement.executeUpdate(); // SQL 실행

        }
    }

    // 2-1. Read
    public User selectUser(int id) throws SQLException {
        User user = null; // 조회한 사용자 정보를 저장할 객체

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID_SQL)) {
                preparedStatement.setInt(1, id);
                ResultSet resultSet = preparedStatement.executeQuery(); // 쿼리 실행 후 결과 집합을 반환

            // 결과 집합에서 데이터가 존재하면 사용자 객체를 생성
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String country = resultSet.getString("country");
                user = new User(id, name, email, country);
            }
        }
        return user; // 조회된 사용자 반환(없으면 null)
    }

    // 2-2. ReadAll
    public List<User> selectAllUsers() throws SQLException {
        List<User> users = new ArrayList<>(); // 사용자 정보를 저장할 list

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery(); // 쿼리 실행 후 결과 집합을 반환

            // 결과 집합에서 데이터가 존재하면 사용자 객체를 생성
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String country = resultSet.getString("country");
               users.add(new User(id, name, email, country));
            }
        }
        return users; // 조회된 사용자 반환(없으면 null)
    }

    // 3. Update
    public boolean updateUser(User user) throws SQLException {
        boolean rewUpdated; // 업데이트 성공 여부를 저장할 변수

        try (Connection connection = getConnection();
             // PreparedStatement 객체를 통해 SQL에 데이터를 바인딩
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USERS_SQL)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getCountry());
            preparedStatement.setInt(4, user.getId());

            // executeupdate() 메서드를 사용해 SQL 실행 및 수정된 행 수 반환
            rewUpdated = preparedStatement.executeUpdate() > 0; // 행이 업데이트 된 경우 true를 반환
        }
        return rewUpdated;
    }

    // 4. Delete
    public boolean deleteUser(int id) throws SQLException {
        boolean rowDeleted;

        try (Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USERS_SQL)) {
            preparedStatement.setInt(1, id);

            rowDeleted = preparedStatement.executeUpdate() > 0;
        }

        return rowDeleted;
    }
}
