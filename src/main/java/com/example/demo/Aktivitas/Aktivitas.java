package com.example.demo.Aktivitas;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.Duration;

@Data
@AllArgsConstructor
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
}
