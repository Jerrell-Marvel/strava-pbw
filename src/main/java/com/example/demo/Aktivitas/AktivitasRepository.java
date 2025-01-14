package com.example.demo.Aktivitas;

import java.util.List;
import java.util.Optional;

public interface AktivitasRepository {
  void insertAktivitas(Aktivitas aktivitas);

  void updateAktivitas(Aktivitas aktivitas, Integer idUser);

  void deleteAktivitas(Integer idAktivitas, Integer idUser);

  List<Aktivitas> findAktivitasByUserId(Integer idUser, Integer page);

  // ga pake pagination (buat statistik)
  List<Aktivitas> findAktivitasByUserId(Integer idUser);

  Aktivitas getAktivitasById(Integer idAktivitas, Integer idUser);

  void deleteFotoByUrl(String urlFoto);

  void insertFoto(Integer idAktivitas, String urlFoto);

  int getAktivitasCount(Integer idUser);

  List<Aktivitas> findAktivitasByUserId(Integer idUser, String search, Integer page);

  int getAktivitasCount(Integer idUser, String search);

}
