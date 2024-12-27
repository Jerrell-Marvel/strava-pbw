package com.example.demo.Lomba;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcLombaRepository implements LombaRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<Lomba> findAll() {
    String sql = "SELECT * FROM Lomba";
    return jdbcTemplate.query(sql, this::mapRowToLomba);
  }

  public List<Lomba> findLombaByPage(int offset, int pageSize) {
    String sql = "SELECT * FROM Lomba LIMIT ? OFFSET ?";
    return jdbcTemplate.query(sql, this::mapRowToLomba, pageSize, offset);
  }

  public int getLombaCount() {
    String sql = "SELECT COUNT(*) FROM Lomba";
    return jdbcTemplate.queryForObject(sql, Integer.class);
  }

  private Lomba mapRowToLomba(ResultSet resultSet, int rowNum) throws SQLException {
    return new Lomba(
        resultSet.getInt("id_lomba"),
        resultSet.getString("nama_lomba"),
        resultSet.getString("deskripsi_lomba"),
        resultSet.getDate("tanggal_mulai").toLocalDate(),
        resultSet.getDate("tanggal_selesai").toLocalDate());
  }

  public List<Leaderboard> findLeaderboardByLombaId(Integer idLomba) {
    String sql = """
            SELECT lm.id_user, u.nama_user, a.jarak_tempuh, a.waktu_tempuh,
                   (a.jarak_tempuh / EXTRACT(EPOCH FROM a.waktu_tempuh)) AS skor
            FROM Lomba_Member lm
            JOIN Aktivitas a ON lm.id_aktivitas = a.id_aktivitas
            JOIN Users u ON lm.id_user = u.id_user
            WHERE lm.id_lomba = ?
            ORDER BY skor DESC
        """;

    return jdbcTemplate.query(sql, (rs, rowNum) -> new Leaderboard(
        rs.getInt("id_user"),
        rs.getString("nama_user"),
        rs.getDouble("jarak_tempuh"),
        rs.getTime("waktu_tempuh").toLocalTime(),
        rs.getDouble("skor")), idLomba);
  }

  public void insertLomba(Lomba lomba) {
    String sql = "INSERT INTO Lomba (nama_lomba, deskripsi_lomba, tanggal_mulai, tanggal_selesai) VALUES (?, ?, ?, ?)";
    jdbcTemplate.update(sql, lomba.getNamaLomba(), lomba.getDeskripsiLomba(), lomba.getTanggalMulai(),
        lomba.getTanggalSelesai());
  }

}
