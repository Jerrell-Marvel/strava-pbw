package com.example.demo.Lomba;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Leaderboard {
  private Integer idUser;
  private String namaUser;
  private Double jarakTempuh;
  private LocalTime waktuTempuh;
  private Double skor;

  public String getFormattedWaktuTempuh() {
    return waktuTempuh != null ? waktuTempuh.format(DateTimeFormatter.ofPattern("HH:mm:ss")) : null;
  }
}
