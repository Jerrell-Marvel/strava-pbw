package com.example.demo.Lomba;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LombaService {

  @Autowired
  private LombaRepository lombaRepository;

  public List<Lomba> getAllLomba() {
    return lombaRepository.findAll();
  }

  public List<Lomba> getLombaByPage(int page, int pageSize) {
    int offset = (page - 1) * pageSize;
    return lombaRepository.findLombaByPage(offset, pageSize);
  }

  public int getLombaCount() {
    return lombaRepository.getLombaCount();
  }

  public List<Leaderboard> getLeaderboardByLombaId(Integer idLomba) {
    return lombaRepository.findLeaderboardByLombaId(idLomba);
  }

  public void addLomba(Lomba lomba) {
    lombaRepository.insertLomba(lomba);
  }

}
