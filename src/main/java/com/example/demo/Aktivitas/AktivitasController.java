package com.example.demo.Aktivitas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.RequiredRole;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class AktivitasController {
  @Autowired
  private AktivitasService aktivitasService;

  @GetMapping("/aktivitas/tambah")
  @RequiredRole("member")
  public String getTambahAktivitas(Model model) {
    model.addAttribute("aktivitas", new Aktivitas());
    return "tambah-aktivitas";
  }

  @PostMapping("/aktivitas/tambah")
  @RequiredRole("member")
  public String tambahAktivitas(
      @Valid Aktivitas aktivitas,
      BindingResult result,
      @RequestParam int jam,
      @RequestParam int menit,
      @RequestParam int detik,
      @RequestParam("foto") MultipartFile foto,
      HttpSession session) {
    if (result.hasErrors()) {
      return "tambah-aktivitas";
    }

    // Convert jam, menit, dan detik ke Duration
    Duration waktuTempuh = Duration.ofHours(jam).plusMinutes(menit).plusSeconds(detik);
    waktuTempuh = waktuTempuh.minusHours(7);
    aktivitas.setWaktuTempuh(waktuTempuh);

    aktivitas.setIdUser((Integer) session.getAttribute("idUser"));

    try {
      String uploadDir = "src/main/resources/static/uploads/aktivitas/";
      String fileName = foto.getOriginalFilename();
      Path filePath = Paths.get(uploadDir + fileName);
      Files.createDirectories(filePath.getParent());
      Files.copy(foto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
      aktivitas.setUrlFoto(filePath.toString());
    } catch (IOException e) {
      e.printStackTrace();
      return "tambah-aktivitas"; // Kembali ke form jika ada error
    }

    // Save aktivitas via service
    aktivitasService.tambahAktivitas(aktivitas);
    return "redirect:/aktivitas/tambah"; // Adjust redirect as necessary
  }

  @GetMapping("/aktivitas")
  @RequiredRole("member")
  public String getAktivitas(Model model, HttpSession session) {
    Integer idUser = (Integer) session.getAttribute("idUser");
    List<Aktivitas> aktivitasList = aktivitasService.getAktivitasByUserId(idUser);
    aktivitasList.forEach(aktivitas -> {
      if (aktivitas.getWaktuTempuh() != null) {
        long hours = aktivitas.getWaktuTempuh().toHours();
        long minutes = aktivitas.getWaktuTempuh().toMinutes() % 60;
        long seconds = aktivitas.getWaktuTempuh().getSeconds() % 60;
        String formatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        aktivitas.setFormattedWaktuTempuh(formatted);
        System.out.println("Formatted Waktu Tempuh: " + formatted); // Debug log
      } else {
        System.out.println("Waktu Tempuh is null for Aktivitas ID: " + aktivitas.getIdAktivitas());
      }
    });
    model.addAttribute("aktivitasList", aktivitasList);
    return "aktivitas";
  }
}
