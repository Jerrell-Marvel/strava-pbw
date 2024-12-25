package com.example.demo.Aktivitas;

import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AktivitasService {
  @Autowired
  private AktivitasRepository aktivitasRepository;

  public void tambahAktivitas(Aktivitas aktivitas) {
    aktivitasRepository.insertAktivitas(aktivitas);
  }

  public List<Aktivitas> getAktivitasByUserId(Integer userId) {
    return aktivitasRepository.findAktivitasByUserId(userId);
  }
}
