package com.example.demo.Lomba;

import java.util.List;

import com.example.demo.Aktivitas.Aktivitas;

public interface LombaRepository {
  List<Lomba> findAll();

  List<Leaderboard> findLeaderboardByLombaId(Integer idLomba, int offset);

  int getLeaderboardCountByLombaId(Integer idLomba);

  void insertLomba(Lomba lomba);

  List<Lomba> findLombaByPage(int offset, int pageSize);

  int getLombaCount();

  List<Lomba> findLombaBerlangsung(int offset, int pageSize);

  int getLombaBerlangsungCount();

  List<Aktivitas> findAktivitasNotInLombaMember(Integer idUser, Integer idLomba, int offset);

  int getAktivitasNotInLombaMemberCount(Integer idUser, Integer idLomba);

  void insertLombaMember(Integer idLomba, Integer idUser, Integer idAktivitas);

  List<LombaMember> findLombaDiikutiByUser(Integer idUser, int offset);

  int countLombaDiikutiByUser(Integer idUser);

  List<LombaBerlangsung> findLombaBerlangsungWithStatus(Integer idUser, int offset, int pageSize);

  int getLombaBerlangsungWithStatusCount();

  List<Lomba> findLombaBySearch(String search, int offset, int pageSize);

  int getLombaCount(String search);

  List<LombaBerlangsung> findLombaBerlangsungWithSearch(Integer idUser, String search, int offset,
      int pageSize);

  int getLombaBerlangsungWithSearchCount(Integer idUser, String search);

  List<LombaMember> findLombaDiikutiWithSearch(Integer idUser, String search, int offset, int pageSize);

  int getLombaDiikutiWithSearchCount(Integer idUser, String search);
}
