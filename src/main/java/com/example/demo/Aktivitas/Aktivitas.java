package com.example.demo.Aktivitas;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.Duration;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Aktivitas {
  private Integer idAktivitas;

  @NotNull
  private LocalDate tanggalAktivitas;

  @NotNull
  @Size(max = 255)
  private String judul;

  private String deskripsi;

  private Duration waktuTempuh;

  private Double jarakTempuh;

  @NotNull
  private String satuanJarak;

  private Integer idUser;

  private List<String> urlFoto = new ArrayList<>();

  private String formattedWaktuTempuh;

  public Aktivitas(Integer idAktivitas,
      @NotNull LocalDate tanggalAktivitas,
      @NotNull @Size(max = 255) String judul,
      String deskripsi,
      Duration waktuTempuh,
      Double jarakTempuh,
      @NotNull String satuanJarak,
      Integer idUser) {
    this.idAktivitas = idAktivitas;
    this.tanggalAktivitas = tanggalAktivitas;
    this.judul = judul;
    this.deskripsi = deskripsi;
    this.waktuTempuh = waktuTempuh;
    this.jarakTempuh = jarakTempuh;
    this.satuanJarak = satuanJarak;
    this.idUser = idUser;
  }

}
