package com.example.demo.Aktivitas;

import java.util.List;
import java.util.Optional;

public interface AktivitasRepository {
  void insertAktivitas(Aktivitas aktivitas);

  void updateAktivitas(Aktivitas aktivitas, Integer idUser);

  void deleteAktivitas(Integer idAktivitas, Integer idUser);

  List<Aktivitas> findAktivitasByUserId(Integer idUser, Integer page);

  Aktivitas getAktivitasById(Integer idAktivitas, Integer idUser);

  int getAktivitasCount(Integer idUser);
}
