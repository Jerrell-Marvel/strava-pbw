package com.example.demo.Aktivitas;

import org.springframework.beans.factory.annotation.Autowired;
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

    if (aktivitas.getUrlFoto() != null) {
      String sqlFoto = "INSERT INTO Foto_Aktivitas (id_aktivitas, url_foto) VALUES (?, ?)";
      jdbcTemplate.update(sqlFoto, idAktivitas, aktivitas.getUrlFoto());
    }
  }

  @Override
  public List<Aktivitas> findAktivitasByUserId(Integer idUser) {
    String sql = "SELECT a.id_aktivitas, a.tanggal_aktivitas, a.judul, a.deskripsi, a.waktu_tempuh, " +
        "a.jarak_tempuh, a.satuan_jarak, a.id_user, fa.url_foto " +
        "FROM aktivitas a " +
        "LEFT JOIN foto_aktivitas fa ON a.id_aktivitas = fa.id_aktivitas " +
        "WHERE a.id_user = ? AND a.is_active = true";
    return jdbcTemplate.query(sql, this::mapRowToAktivitas, idUser);
  }

  @Override
  public Aktivitas getAktivitasById(Integer idAktivitas) {
    String sql = "SELECT a.id_aktivitas, a.tanggal_aktivitas, a.judul, a.deskripsi, a.waktu_tempuh, " +
        "a.jarak_tempuh, a.satuan_jarak, a.id_user, fa.url_foto " +
        "FROM aktivitas a " +
        "LEFT JOIN foto_aktivitas fa ON a.id_aktivitas = fa.id_aktivitas " +
        "WHERE a.id_aktivitas = ?";
    return jdbcTemplate.queryForObject(sql, this::mapRowToAktivitas, idAktivitas);
  }

  @Override
  public void updateAktivitas(Aktivitas aktivitas) {
    String updateAktivitasSql = "UPDATE aktivitas SET judul = ?, deskripsi = ? WHERE id_aktivitas = ?";
    jdbcTemplate.update(updateAktivitasSql, aktivitas.getJudul(), aktivitas.getDeskripsi(), aktivitas.getIdAktivitas());

    if (aktivitas.getUrlFoto() != null) {
      String updateFotoSql = "UPDATE foto_aktivitas SET url_foto = ? WHERE id_aktivitas = ?";
      jdbcTemplate.update(updateFotoSql, aktivitas.getUrlFoto(), aktivitas.getIdAktivitas());
    }
  }

  @Override
  public void deleteAktivitas(Aktivitas aktivitas) {
    if (aktivitas.getUrlFoto() != null && !aktivitas.getUrlFoto().isEmpty()) {
      String sqlFoto = "UPDATE foto_aktivitas SET is_active = FALSE WHERE id_aktivitas = ?";
      jdbcTemplate.update(sqlFoto, aktivitas.getIdAktivitas());
    }

    String sqlAktivitas = "UPDATE aktivitas SET is_active = FALSE WHERE id_aktivitas = ?";
    jdbcTemplate.update(sqlAktivitas, aktivitas.getIdAktivitas());
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
        resultSet.getInt("id_user"),
        resultSet.getString("url_foto"));
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
