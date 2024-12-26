package com.example.demo.Aktivitas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Duration;
import java.util.List;

enum SatuanJarak {
  kilometer, meter, mil, yard;
}

@Repository
public class JdbcAktivitasRepository implements AktivitasRepository {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void insertAktivitas(Aktivitas aktivitas) {
    String sql = "INSERT INTO Aktivitas (tanggal_aktivitas, judul, deskripsi, waktu_tempuh, jarak_tempuh, satuan_jarak, id_user) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id_aktivitas";

    // Create a Time object using the hours, minutes, and seconds
    Time time = new Time((aktivitas.getWaktuTempuh().getSeconds()) * 1000); // Milliseconds

    Integer idAktivitas = jdbcTemplate.queryForObject(sql, Integer.class,
        aktivitas.getTanggalAktivitas(),
        aktivitas.getJudul(),
        aktivitas.getDeskripsi(),
        time,
        aktivitas.getJarakTempuh(),
        aktivitas.getSatuanJarak(),
        aktivitas.getIdUser());

    if (aktivitas.getUrlFoto() != null && !aktivitas.getUrlFoto().isEmpty()) {
      String sqlFoto = "INSERT INTO Foto_Aktivitas (id_aktivitas, url_foto) VALUES (?, ?)";
      for (String urlFoto : aktivitas.getUrlFoto()) {
        jdbcTemplate.update(sqlFoto, idAktivitas, urlFoto);
      }
    }
  }

  @Override
  public List<Aktivitas> findAktivitasByUserId(Integer idUser, Integer page) {
    int offset = (page - 1) * 10;
    String sql = "SELECT a.id_aktivitas, a.tanggal_aktivitas, a.judul, a.deskripsi, a.waktu_tempuh, " +
        "a.jarak_tempuh, a.satuan_jarak, a.id_user " +
        "FROM aktivitas a " + "WHERE a.id_user = ? AND a.is_active = true LIMIT 10 OFFSET ?";
    return jdbcTemplate.query(sql, this::mapRowToAktivitas, idUser, offset);
  }

  @Override
  public Aktivitas getAktivitasById(Integer idAktivitas, Integer idUser) {
    try {
      String sqlAktivitas = "SELECT * FROM aktivitas WHERE id_aktivitas = ? AND id_user = ?";
      Aktivitas aktivitas = jdbcTemplate.queryForObject(sqlAktivitas, this::mapRowToAktivitas, idAktivitas, idUser);

      String sqlFoto = "SELECT url_foto FROM foto_aktivitas WHERE id_aktivitas = ? AND is_active = TRUE";
      List<String> fotoList = jdbcTemplate.queryForList(sqlFoto, String.class, idAktivitas);
      aktivitas.setUrlFoto(fotoList);

      return aktivitas;
    } catch (EmptyResultDataAccessException e) {
      throw new IllegalStateException(
          "No aktivitas found for id_aktivitas: " + idAktivitas + " and id_user: " + idUser);
    }
  }

  @Override
  public void updateAktivitas(Aktivitas aktivitas, Integer idUser) {
    String updateAktivitasSql = "UPDATE aktivitas SET judul = ?, deskripsi = ? WHERE id_aktivitas = ? AND id_user = ?";
    int rowsAffected = jdbcTemplate.update(updateAktivitasSql, aktivitas.getJudul(), aktivitas.getDeskripsi(),
        aktivitas.getIdAktivitas(), idUser);

    if (rowsAffected == 0) {
      throw new IllegalStateException("No rows updated. Either the aktivitas or user does not exist.");
    }

  }

  @Override
  public void deleteAktivitas(Integer idAktivitas, Integer idUser) {
    String sqlAktivitas = "UPDATE aktivitas SET is_active = FALSE WHERE id_aktivitas = ? AND id_user = ?";
    int rowsAffected = jdbcTemplate.update(sqlAktivitas, idAktivitas, idUser);

    if (rowsAffected == 0) {
      throw new IllegalStateException(
          "No aktivitas found to delete for id_aktivitas: " + idAktivitas + " and id_user: " + idUser);
    }
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

  @Override
  public int getAktivitasCount(Integer idUser) {
    String queryText = "SELECT COUNT(*) FROM Aktivitas WHERE id_user = ? AND is_active = TRUE";
    int rowCount = jdbcTemplate.queryForObject(queryText, Integer.class, idUser);
    return rowCount;
  }

}
