package com.example.demo.Lomba;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.Aktivitas.Aktivitas;

@Repository
public class JdbcLombaRepository implements LombaRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<Lomba> findAll() {
    String sql = "SELECT * FROM Lomba";
    return jdbcTemplate.query(sql, this::mapRowToLomba);
  }

  public List<Lomba> findLombaByPage(int offset, int pageSize) {
    String sql = "SELECT * FROM Lomba ORDER BY Lomba.id_lomba DESC LIMIT ? OFFSET ?";
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

  // public List<Leaderboard> findLeaderboardByLombaId(Integer idLomba) {
  // String sql = """
  // SELECT lm.id_user, u.nama_user, a.jarak_tempuh, a.waktu_tempuh,
  // a.satuan_jarak,
  // (CASE
  // WHEN a.satuan_jarak = 'm' THEN a.jarak_tempuh / 1000
  // WHEN a.satuan_jarak = 'mil' THEN a.jarak_tempuh * 1.60934
  // WHEN a.satuan_jarak = 'yard' THEN a.jarak_tempuh * 0.0009144
  // ELSE a.jarak_tempuh -- Default assumed as kilometer
  // END) / (EXTRACT(EPOCH FROM a.waktu_tempuh) / 3600) AS skor
  // FROM Lomba_Member lm
  // JOIN Aktivitas a ON lm.id_aktivitas = a.id_aktivitas
  // JOIN Users u ON lm.id_user = u.id_user
  // WHERE lm.id_lomba = ?
  // ORDER BY skor DESC
  // """;

  // return jdbcTemplate.query(sql, (rs, rowNum) -> new Leaderboard(
  // rs.getInt("id_user"),
  // rs.getString("nama_user"),
  // rs.getDouble("jarak_tempuh"), // Jarak tempuh asli
  // rs.getTime("waktu_tempuh").toLocalTime(), // Waktu tempuh asli
  // rs.getDouble("skor") // Skor dalam km/h
  // ), idLomba);
  // }

  public List<Leaderboard> findLeaderboardByLombaId(Integer idLomba, int offset) {
    String sql = """
            SELECT lm.id_user, u.nama_user, a.jarak_tempuh, a.waktu_tempuh, a.satuan_jarak,
                   (EXTRACT(EPOCH FROM a.waktu_tempuh) /
                   CASE
                       WHEN a.satuan_jarak = 'm' THEN a.jarak_tempuh / 1000
                       WHEN a.satuan_jarak = 'mil' THEN a.jarak_tempuh * 1.60934
                       WHEN a.satuan_jarak = 'yard' THEN a.jarak_tempuh * 0.0009144
                       ELSE a.jarak_tempuh -- Default assumed as kilometer
                   END) AS avg_pace
            FROM Lomba_Member lm
            JOIN Aktivitas a ON lm.id_aktivitas = a.id_aktivitas
            JOIN Users u ON lm.id_user = u.id_user
            WHERE lm.id_lomba = ?
            ORDER BY avg_pace ASC LIMIT 10 OFFSET ?
        """;

    return jdbcTemplate.query(sql, (rs, rowNum) -> new Leaderboard(
        rs.getInt("id_user"),
        rs.getString("nama_user"),
        rs.getDouble("jarak_tempuh"), // Jarak tempuh asli
        rs.getTime("waktu_tempuh").toLocalTime(), // Waktu tempuh asli
        rs.getDouble("avg_pace"), // Avg pace dalam detik per kilometer
        rs.getString("satuan_jarak")), idLomba, offset);
  }

  @Override
  public int getLeaderboardCountByLombaId(Integer idLomba) {
    String sql = """
            SELECT COUNT(*)
            FROM Lomba_Member lm
            JOIN Aktivitas a ON lm.id_aktivitas = a.id_aktivitas
            JOIN Users u ON lm.id_user = u.id_user
            WHERE lm.id_lomba = ?
        """;

    return jdbcTemplate.queryForObject(sql, Integer.class, idLomba);
  }

  public void insertLomba(Lomba lomba) {
    String sql = "INSERT INTO Lomba (nama_lomba, deskripsi_lomba, tanggal_mulai, tanggal_selesai) VALUES (?, ?, ?, ?)";
    jdbcTemplate.update(sql, lomba.getNamaLomba(), lomba.getDeskripsiLomba(), lomba.getTanggalMulai(),
        lomba.getTanggalSelesai());
  }

  public List<Lomba> findLombaBerlangsung(int offset, int pageSize) {
    String sql = """
            SELECT * FROM Lomba
            WHERE CURRENT_DATE BETWEEN tanggal_mulai AND tanggal_selesai
            ORDER BY Lomba.id_lomba DESC
            LIMIT ? OFFSET ?
        """;
    return jdbcTemplate.query(sql, this::mapRowToLomba, pageSize, offset);
  }

  public int getLombaBerlangsungCount() {
    String sql = """
            SELECT COUNT(*) FROM Lomba
            WHERE CURRENT_DATE BETWEEN tanggal_mulai AND tanggal_selesai
        """;
    return jdbcTemplate.queryForObject(sql, Integer.class);
  }

  public List<Aktivitas> findAktivitasNotInLombaMember(Integer idUser, Integer idLomba, int offset) {
    String sql = """
            SELECT a.*
            FROM Aktivitas a
            LEFT JOIN Lomba_Member lm ON a.id_aktivitas = lm.id_aktivitas
            JOIN Lomba l ON l.id_lomba = ?
            WHERE a.id_user = ?
              AND lm.id_aktivitas IS NULL
              AND a.tanggal_aktivitas BETWEEN l.tanggal_mulai AND l.tanggal_selesai
            ORDER BY a.id_aktivitas DESC
            LIMIT 10 OFFSET ?
        """;
    return jdbcTemplate.query(sql, this::mapRowToAktivitas, idLomba, idUser, offset);
  }

  public int getAktivitasNotInLombaMemberCount(Integer idUser, Integer idLomba) {

    String sql = """
            SELECT COUNT(*)
            FROM Aktivitas a
            LEFT JOIN Lomba_Member lm ON a.id_aktivitas = lm.id_aktivitas
            JOIN Lomba l ON l.id_lomba = ?
            WHERE a.id_user = ?
              AND lm.id_aktivitas IS NULL
              AND a.tanggal_aktivitas BETWEEN l.tanggal_mulai AND l.tanggal_selesai

        """;
    return jdbcTemplate.queryForObject(sql, Integer.class, idLomba, idUser);
  }

  public void insertLombaMember(Integer idLomba, Integer idUser, Integer idAktivitas) {
    String sql = "INSERT INTO Lomba_Member (id_lomba, id_user, id_aktivitas) VALUES (?, ?, ?)";
    jdbcTemplate.update(sql, idLomba, idUser, idAktivitas);
  }

  public List<LombaMember> findLombaDiikutiByUser(Integer idUser, int offset) {
    String sql = """
            SELECT l.id_lomba, l.nama_lomba, l.deskripsi_lomba,
                   lm.id_aktivitas, a.judul, l.tanggal_mulai, l.tanggal_selesai
            FROM Lomba l
            JOIN Lomba_Member lm ON l.id_lomba = lm.id_lomba
            JOIN Aktivitas a ON lm.id_aktivitas = a.id_aktivitas
            WHERE lm.id_user = ?
            ORDER BY l.id_lomba DESC
            LIMIT 10 OFFSET ?
        """;
    return jdbcTemplate.query(sql, (rs, rowNum) -> new LombaMember(
        rs.getInt("id_lomba"),
        rs.getString("nama_lomba"),
        rs.getString("deskripsi_lomba"),
        rs.getInt("id_aktivitas"),
        rs.getString("judul"),
        rs.getDate("tanggal_mulai").toLocalDate(),
        rs.getDate("tanggal_selesai").toLocalDate()), idUser, offset);
  }

  @Override
  public int countLombaDiikutiByUser(Integer idUser) {
    String sql = """
            SELECT COUNT(*) FROM Lomba l JOIN Lomba_Member lm ON l.id_lomba = lm.id_lomba JOIN Aktivitas a ON lm.id_aktivitas = a.id_aktivitas WHERE lm.id_user = ?
        """;

    return jdbcTemplate.queryForObject(sql, Integer.class, idUser);
  }

  public List<LombaBerlangsung> findLombaBerlangsungWithStatus(Integer idUser, int offset, int pageSize) {
    String sql = """
                    SELECT l.id_lomba, l.nama_lomba, l.deskripsi_lomba, l.tanggal_mulai, l.tanggal_selesai,
               CASE WHEN lm.id_user IS NOT NULL THEN TRUE ELSE FALSE END AS status_mengikuti
        FROM Lomba l
        LEFT JOIN Lomba_Member lm ON l.id_lomba = lm.id_lomba AND lm.id_user = ?
        WHERE CURRENT_DATE BETWEEN l.tanggal_mulai AND l.tanggal_selesai
        ORDER BY l.id_lomba DESC
        LIMIT ? OFFSET ?;
                """;
    return jdbcTemplate.query(sql, (rs, rowNum) -> new LombaBerlangsung(
        rs.getInt("id_lomba"),
        rs.getString("nama_lomba"),
        rs.getString("deskripsi_lomba"),
        rs.getDate("tanggal_mulai").toLocalDate(),
        rs.getDate("tanggal_selesai").toLocalDate(),
        rs.getBoolean("status_mengikuti")), idUser, pageSize, offset);
  }

  public int getLombaBerlangsungWithStatusCount() {
    String sql = """
            SELECT COUNT(*)
            FROM Lomba l
            WHERE CURRENT_DATE BETWEEN l.tanggal_mulai AND l.tanggal_selesai
        """;
    return jdbcTemplate.queryForObject(sql, Integer.class);
  }

  @Override
  public List<Lomba> findLombaBySearch(String search, int offset, int pageSize) {
    String sql = """
            SELECT * FROM Lomba
            WHERE LOWER(nama_lomba) LIKE LOWER(?)
            ORDER BY id_lomba DESC LIMIT ? OFFSET ?
        """;
    return jdbcTemplate.query(sql, this::mapRowToLomba, "%" + search + "%", pageSize, offset);
  }

  @Override
  public int getLombaCount(String search) {
    String sql = """
            SELECT COUNT(*) FROM Lomba
            WHERE LOWER(nama_lomba) LIKE LOWER(?)
        """;
    return jdbcTemplate.queryForObject(sql, Integer.class, "%" + search + "%");
  }

  @Override
  public List<LombaBerlangsung> findLombaBerlangsungWithSearch(Integer idUser, String search, int offset,
      int pageSize) {
    String sql = """
            SELECT l.id_lomba, l.nama_lomba, l.deskripsi_lomba, l.tanggal_mulai, l.tanggal_selesai,
                   CASE WHEN lm.id_user IS NOT NULL THEN TRUE ELSE FALSE END AS status_mengikuti
            FROM Lomba l
            LEFT JOIN Lomba_Member lm ON l.id_lomba = lm.id_lomba AND lm.id_user = ?
            WHERE CURRENT_DATE BETWEEN l.tanggal_mulai AND l.tanggal_selesai
              AND LOWER(l.nama_lomba) LIKE LOWER(?)
            ORDER BY l.id_lomba DESC
            LIMIT ? OFFSET ?
        """;
    return jdbcTemplate.query(sql, (rs, rowNum) -> new LombaBerlangsung(
        rs.getInt("id_lomba"),
        rs.getString("nama_lomba"),
        rs.getString("deskripsi_lomba"),
        rs.getDate("tanggal_mulai").toLocalDate(),
        rs.getDate("tanggal_selesai").toLocalDate(),
        rs.getBoolean("status_mengikuti")), idUser, "%" + search + "%", pageSize, offset);
  }

  @Override
  public int getLombaBerlangsungWithSearchCount(Integer idUser, String search) {
    String sql = """
            SELECT COUNT(*)
            FROM Lomba l
            LEFT JOIN Lomba_Member lm ON l.id_lomba = lm.id_lomba AND lm.id_user = ?
            WHERE CURRENT_DATE BETWEEN l.tanggal_mulai AND l.tanggal_selesai
              AND LOWER(l.nama_lomba) LIKE LOWER(?)
        """;
    return jdbcTemplate.queryForObject(sql, Integer.class, idUser, "%" + search + "%");
  }

  @Override
  public List<LombaMember> findLombaDiikutiWithSearch(Integer idUser, String search, int offset, int pageSize) {
    String sql = """
            SELECT l.id_lomba, l.nama_lomba, l.deskripsi_lomba,
                   lm.id_aktivitas, a.judul, l.tanggal_mulai, l.tanggal_selesai
            FROM Lomba l
            JOIN Lomba_Member lm ON l.id_lomba = lm.id_lomba
            JOIN Aktivitas a ON lm.id_aktivitas = a.id_aktivitas
            WHERE lm.id_user = ?
              AND LOWER(l.nama_lomba) LIKE LOWER(?)
            ORDER BY l.id_lomba DESC
            LIMIT ? OFFSET ?
        """;
    return jdbcTemplate.query(sql, (rs, rowNum) -> new LombaMember(
        rs.getInt("id_lomba"),
        rs.getString("nama_lomba"),
        rs.getString("deskripsi_lomba"),
        rs.getInt("id_aktivitas"),
        rs.getString("judul"),
        rs.getDate("tanggal_mulai").toLocalDate(),
        rs.getDate("tanggal_selesai").toLocalDate()), idUser, "%" + search + "%", pageSize, offset);
  }

  @Override
  public int getLombaDiikutiWithSearchCount(Integer idUser, String search) {
    String sql = """
            SELECT COUNT(*)
            FROM Lomba l
            JOIN Lomba_Member lm ON l.id_lomba = lm.id_lomba
            JOIN Aktivitas a ON lm.id_aktivitas = a.id_aktivitas
            WHERE lm.id_user = ?
              AND LOWER(l.nama_lomba) LIKE LOWER(?)
        """;
    return jdbcTemplate.queryForObject(sql, Integer.class, idUser, "%" + search + "%");
  }

  private Aktivitas mapRowToAktivitas(ResultSet resultSet, int rowNum) throws SQLException {
    return new Aktivitas(
        resultSet.getInt("id_aktivitas"),
        resultSet.getDate("tanggal_aktivitas").toLocalDate(),
        resultSet.getString("judul"),
        resultSet.getString("deskripsi"),
        convertTimeToDuration(resultSet.getTime("waktu_tempuh")),
        resultSet.getDouble("jarak_tempuh"),
        resultSet.getString("satuan_jarak"),
        resultSet.getInt("id_user"));
  }

  private Duration convertTimeToDuration(java.sql.Time sqlTime) {
    if (sqlTime == null) {
      return null;
    }
    return Duration.ofHours(sqlTime.toLocalTime().getHour())
        .plusMinutes(sqlTime.toLocalTime().getMinute())
        .plusSeconds(sqlTime.toLocalTime().getSecond());
  }

}
