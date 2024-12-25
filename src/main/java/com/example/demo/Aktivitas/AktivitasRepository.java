package com.example.demo.Aktivitas;

import java.util.List;
import java.util.Optional;

public interface AktivitasRepository {
  void insertAktivitas(Aktivitas aktivitas);

  void updateAktivitas(Aktivitas aktivitas);

  void deleteAktivitas(Aktivitas aktivitas);

  List<Aktivitas> findAktivitasByUserId(Integer idUser);

  Aktivitas getAktivitasById(Integer idAktivitas);
}
