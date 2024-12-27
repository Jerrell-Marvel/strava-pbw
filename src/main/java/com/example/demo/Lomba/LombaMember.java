package com.example.demo.Lomba;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LombaMember {
  private Integer idLomba; // ID lomba
  private String namaLomba; // Nama lomba
  private String deskripsiLomba; // Deskripsi lomba
  private Integer idAktivitas; // ID aktivitas yang diikutkan
  private String judulAktivitas; // Judul aktivitas yang diikutkan
  private LocalDate tanggalMulai; // Tanggal mulai lomba
  private LocalDate tanggalSelesai; // Tanggal selesai lomba
}
