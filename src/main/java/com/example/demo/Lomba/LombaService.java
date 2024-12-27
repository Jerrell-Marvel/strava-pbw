package com.example.demo.Lomba;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Aktivitas.Aktivitas;

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

  public List<Lomba> getLombaBerlangsung(int page, int pageSize) {
    int offset = (page - 1) * pageSize;
    return lombaRepository.findLombaBerlangsung(offset, pageSize);
  }

  public int getLombaBerlangsungCount() {
    return lombaRepository.getLombaBerlangsungCount();
  }

  public List<Aktivitas> getAktivitasNotInLombaMember(Integer idUser, Integer idLomba) {
    return lombaRepository.findAktivitasNotInLombaMember(idUser, idLomba);
  }

  public void addLombaMember(Integer idLomba, Integer idUser, Integer idAktivitas) {
    lombaRepository.insertLombaMember(idLomba, idUser, idAktivitas);
  }

  public List<LombaMember> getLombaDiikuti(Integer idUser) {
    return lombaRepository.findLombaDiikutiByUser(idUser);
  }

  public List<LombaBerlangsung> getLombaBerlangsungWithStatus(Integer idUser, int page, int pageSize) {
    int offset = (page - 1) * pageSize;
    return lombaRepository.findLombaBerlangsungWithStatus(idUser, offset, pageSize);
  }

  public int getLombaBerlangsungWithStatusCount() {
    return lombaRepository.getLombaBerlangsungWithStatusCount();
  }

}
