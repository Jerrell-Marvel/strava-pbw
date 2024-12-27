package com.example.demo.Lomba;

import java.util.List;

import com.example.demo.Aktivitas.Aktivitas;

public interface LombaRepository {
  List<Lomba> findAll();

  List<Leaderboard> findLeaderboardByLombaId(Integer idLomba);

  void insertLomba(Lomba lomba);

  List<Lomba> findLombaByPage(int offset, int pageSize);

  int getLombaCount();

  List<Lomba> findLombaBerlangsung(int offset, int pageSize);

  int getLombaBerlangsungCount();

  List<Aktivitas> findAktivitasNotInLombaMember(Integer idUser, Integer idLomba);

  void insertLombaMember(Integer idLomba, Integer idUser, Integer idAktivitas);

  List<LombaMember> findLombaDiikutiByUser(Integer idUser);
}
