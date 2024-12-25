package com.example.demo.Aktivitas;

import java.util.List;
import java.util.Optional;

public interface AktivitasRepository {
  void insertAktivitas(Aktivitas aktivitas);

  List<Aktivitas> findAktivitasByUserId(Integer idUser);
}
