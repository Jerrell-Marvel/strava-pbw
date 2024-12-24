package com.example.demo.Aktivitas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

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
}
