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

  @Override
  public List<User> findMembers(int page) {
    int offset = (page - 1) * 10;
    String sql = "SELECT * FROM Users WHERE role_user = 'member' LIMIT 10 OFFSET ?";
    return jdbcTemplate.query(sql, this::mapRowToUser, offset);
  }

  @Override
  public int getMemberCount() {
    String sql = "SELECT COUNT(*) FROM Users WHERE role_user = 'member'";
    return jdbcTemplate.queryForObject(sql, Integer.class);
  }

  @Override
  public Optional<User> findById(Integer idUser) {
    String sql = "SELECT * FROM Users WHERE id_user = ?";
    List<User> results = jdbcTemplate.query(sql, this::mapRowToUser, idUser);
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @Override
  public void update(User user) throws Exception {
    String sql = "UPDATE Users SET nama_user = ?, email = ? WHERE id_user = ?";
    int rowsAffected = jdbcTemplate.update(sql, user.getNamaUser(), user.getEmail(), user.getIdUser());
    if (rowsAffected == 0) {
      throw new Exception("Failed to update user.");
    }
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
