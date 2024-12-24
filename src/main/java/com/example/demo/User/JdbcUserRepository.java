package com.example.demo.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcUserRepository implements UserRepository {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public void save(User user) throws Exception {
    String sql = "INSERT INTO Users (nama_user, email, password) VALUES (?, ?, ?)";
    jdbcTemplate.update(sql, user.getNamaUser(), user.getEmail(), user.getPassword());
  }

  public Optional<User> findByEmail(String email) {
    String sql = "SELECT * FROM users WHERE email = ?";
    List<User> results = jdbcTemplate.query(sql, this::mapRowToUser, email);
    return results.size() == 0 ? Optional.empty() : Optional.of(results.get(0));
  }

  private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
    return new User(
        resultSet.getInt("id_user"),
        resultSet.getString("nama_user"),
        resultSet.getString("email"),
        resultSet.getString("password"),
        resultSet.getString("password"),
        resultSet.getString("role_user"));
  }

}
