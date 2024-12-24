package com.example.demo.Aktivitas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Time;

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
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Create a Time object using the hours, minutes, and seconds
        Time time = new Time((aktivitas.getWaktuTempuh().getSeconds()) * 1000); // Milliseconds

        jdbcTemplate.update(sql,
                aktivitas.getTanggalAktivitas(),
                aktivitas.getJudul(),
                aktivitas.getDeskripsi(),
                time,
                aktivitas.getJarakTempuh(),
                aktivitas.getSatuanJarak(),
                aktivitas.getIdUser());
    }
}
