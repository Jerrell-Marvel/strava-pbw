package com.example.demo.Aktivitas;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class AktivitasController {
    @Autowired
    private AktivitasService aktivitasService;

    @GetMapping("/aktivitas/tambah")
    public String getTambahAktivitas() {
        return "tambah-aktivitas";
    }

    @PostMapping("/aktivitas/tambah")
    public String tambahAktivitas(
            @Valid Aktivitas aktivitas,
            BindingResult result,
            @RequestParam int jam,
            @RequestParam int menit,
            @RequestParam int detik) {
        if (result.hasErrors()) {
            return "tambah-aktivitas";
        }

        // Convert jam, menit, dan detik ke Duration
        Duration waktuTempuh = Duration.ofHours(jam).plusMinutes(menit).plusSeconds(detik);
        aktivitas.setWaktuTempuh(waktuTempuh);

        aktivitas.setIdUser(1);

        // Save aktivitas via service
        aktivitasService.tambahAktivitas(aktivitas);
        return "redirect:/tambah-aktivitas"; // Adjust redirect as necessary
    }
}
