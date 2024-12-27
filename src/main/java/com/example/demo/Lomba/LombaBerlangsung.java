package com.example.demo.Lomba;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LombaBerlangsung {
  private Integer idLomba;

  @NotNull
  @Size(max = 255)
  private String namaLomba;

  private String deskripsiLomba;

  @NotNull
  private LocalDate tanggalMulai;

  @NotNull
  private LocalDate tanggalSelesai;

  private Boolean statusMengikuti;
}
