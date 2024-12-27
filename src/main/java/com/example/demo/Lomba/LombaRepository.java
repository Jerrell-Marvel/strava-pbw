package com.example.demo.Lomba;

import java.util.List;

public interface LombaRepository {
  List<Lomba> findAll();

  List<Leaderboard> findLeaderboardByLombaId(Integer idLomba);

  void insertLomba(Lomba lomba);

  List<Lomba> findLombaByPage(int offset, int pageSize);

  int getLombaCount();
}
