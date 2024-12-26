package com.example.demo.Aktivitas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
      @RequestParam(defaultValue = "0") int jam,
      @RequestParam(defaultValue = "0") int menit,
      @RequestParam(defaultValue = "0") int detik,
      @RequestParam("foto") MultipartFile[] foto,
      HttpSession session) {
    if (result.hasErrors()) {
      return "tambah-aktivitas";
    }

    // Convert jam, menit, dan detik ke Duration
    Duration waktuTempuh = Duration.ofHours(jam).plusMinutes(menit).plusSeconds(detik);
    waktuTempuh = waktuTempuh.minusHours(7);
    aktivitas.setWaktuTempuh(waktuTempuh);

    aktivitas.setIdUser((Integer) session.getAttribute("idUser"));

    // aktivitas.setUrlFoto(new ArrayList<>());

    for (MultipartFile file : foto) {
      if (!file.isEmpty()) {
        // Handle file processing (e.g., save to disk, database, etc.)
        try {
          if (!file.isEmpty()) {
            // Generate unique file name
            String originalName = file.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            // Save the file
            String uploadDir = "src/main/resources/static/uploads/aktivitas/";
            Path filePath = Paths.get(uploadDir + uniqueFileName);
            Files.createDirectories(filePath.getParent());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Set the unique file name in aktivitas
            aktivitas.getUrlFoto().add(uniqueFileName);
          }
        } catch (IOException e) {
          e.printStackTrace();
          return "tambah-aktivitas"; // Kembali ke form jika ada error
        }

      }

    }

    // try

    // {
    // if (!foto.isEmpty()) {
    // // Generate unique file name
    // String originalName = foto.getOriginalFilename();
    // String extension = originalName.substring(originalName.lastIndexOf("."));
    // String uniqueFileName = UUID.randomUUID().toString() + extension;

    // // Save the file
    // String uploadDir = "src/main/resources/static/uploads/aktivitas/";
    // Path filePath = Paths.get(uploadDir + uniqueFileName);
    // Files.createDirectories(filePath.getParent());
    // Files.copy(foto.getInputStream(), filePath,
    // StandardCopyOption.REPLACE_EXISTING);

    // // Set the unique file name in aktivitas
    // aktivitas.setUrlFoto(uniqueFileName);
    // }
    // } catch (IOException e) {
    // e.printStackTrace();
    // return "tambah-aktivitas"; // Kembali ke form jika ada error
    // }

    // Save aktivitas via service
    aktivitasService.tambahAktivitas(aktivitas);
    return "redirect:/aktivitas";
  }

  @GetMapping("/aktivitas")
  @RequiredRole("member")
  public String getAktivitas(@RequestParam(name = "page", required = false, defaultValue = "1") String page,
      Model model,
      HttpSession session) {
    Integer idUser = (Integer) session.getAttribute("idUser");
    List<Aktivitas> aktivitasList = aktivitasService.getAktivitasByUserId(idUser, Integer.parseInt(page));

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

    // pagination
    int rowCount = aktivitasService.getAktivitasCount(idUser);
    int pageCount = (int) Math.ceil((double) rowCount / 10);
    model.addAttribute("pageCount", pageCount);
    model.addAttribute("currentPage", page);

    model.addAttribute("aktivitasList", aktivitasList);
    return "aktivitas";
  }

  @GetMapping("/aktivitas/edit/{id}")
  @RequiredRole("member")
  public String getEditAktivitas(@PathVariable("id") Integer idAktivitas, Model model, HttpSession session) {
    Integer idUser = (Integer) session.getAttribute("idUser");
    Aktivitas aktivitas = aktivitasService.getAktivitasById(idAktivitas, idUser);

    if (aktivitas == null) {
      return "redirect:/aktivitas"; // Redirect jika aktivitas tidak ditemukan
    }

    // Ekstrak nama file dari path
    // if (aktivitas.getUrlFoto() != null) {
    // Path path = Paths.get(aktivitas.getUrlFoto());
    // aktivitas.setUrlFoto(path.getFileName().toString());
    // }

    if (aktivitas.getWaktuTempuh() != null) {
      long hours = aktivitas.getWaktuTempuh().toHours();
      long minutes = aktivitas.getWaktuTempuh().toMinutes() % 60;
      long seconds = aktivitas.getWaktuTempuh().getSeconds() % 60;
      String formatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
      aktivitas.setFormattedWaktuTempuh(formatted);
      System.out.println("Formatted Waktu Tempuh: " + formatted); // Debug log
    }

    model.addAttribute("aktivitas", aktivitas);
    return "edit-aktivitas";
  }

  @PostMapping("/aktivitas/edit")
  @RequiredRole("member")
  public String postEditAktivitas(
      @Valid Aktivitas aktivitas,
      BindingResult result,
      HttpSession session) {

    // if (aktivitas.getTanggalAktivitas() == null || aktivitas.getSatuanJarak() ==
    // null) {
    // Aktivitas existingAktivitas =
    // aktivitasService.getAktivitasById(aktivitas.getIdAktivitas());
    // if (aktivitas.getTanggalAktivitas() == null) {
    // aktivitas.setTanggalAktivitas(existingAktivitas.getTanggalAktivitas());
    // }
    // if (aktivitas.getSatuanJarak() == null) {
    // aktivitas.setSatuanJarak(existingAktivitas.getSatuanJarak());
    // }
    // }

    // Jika ada error lain (manual), tambahkan logika error di sini
    if (aktivitas.getJudul() == null || aktivitas.getJudul().isEmpty()) {
      result.rejectValue("judul", "error.judul", "Judul tidak boleh kosong");
      return "edit-aktivitas";
    }

    // try {

    // if (!foto.isEmpty()) {
    // // Simpan file foto baru
    // String uploadDir = "src/main/resources/static/uploads/aktivitas/";
    // String fileName = foto.getOriginalFilename();
    // Path filePath = Paths.get(uploadDir + fileName);
    // Files.createDirectories(filePath.getParent());
    // Files.copy(foto.getInputStream(), filePath,
    // StandardCopyOption.REPLACE_EXISTING);
    // // aktivitas.setUrlFoto(filePath.toString());
    // }

    // // Simpan perubahan via service
    // aktivitasService.updateAktivitas(aktivitas);
    // System.out.println("Aktivitas updated successfully!");
    // } catch (IOException e) {
    // e.printStackTrace();
    // System.out.println("Error saving file!");
    // return "edit-aktivitas";
    // }

    Integer idUser = (Integer) session.getAttribute("idUser");

    aktivitasService.updateAktivitas(aktivitas, idUser);

    System.out.println("Redirecting to /aktivitas");
    return "redirect:/aktivitas";
  }

  @GetMapping("/aktivitas/hapus/{id}")
  @RequiredRole("member")
  public String hapusAktivitas(@PathVariable("id") Integer idAktivitas, HttpSession session) {
    // // Ambil data aktivitas berdasarkan ID
    // Aktivitas aktivitas = aktivitasService.getAktivitasById(idAktivitas);
    // if (aktivitas == null) {
    // return "redirect:/aktivitas"; // Redirect jika aktivitas tidak ditemukan
    // }
    Integer idUser = (Integer) session.getAttribute("idUser");
    // Hapus aktivitas
    aktivitasService.deleteAktivitas(idAktivitas, idUser);
    return "redirect:/aktivitas"; // Kembali ke halaman daftar aktivitas
  }

  @PostMapping("/aktivitas/hapus-foto")
  @RequiredRole("member")
  public ResponseEntity<?> hapusFoto(@RequestBody Map<String, String> body) {
    String urlFoto = body.get("urlFoto");
    System.out.println("URL Foto yang diterima untuk dihapus: " + urlFoto);

    if (urlFoto == null || urlFoto.isEmpty()) {
      return ResponseEntity.badRequest().body("URL Foto tidak valid!");
    }

    try {
      aktivitasService.deleteFotoByUrl(urlFoto);
      return ResponseEntity.ok("Foto berhasil dihapus");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal menghapus foto");
    }
  }

  @PostMapping("/aktivitas/upload-foto")
  @RequiredRole("member")
  public ResponseEntity<?> uploadFoto(
      @RequestParam("foto") MultipartFile[] foto,
      @RequestParam("idAktivitas") Integer idAktivitas) {

    if (idAktivitas == null) {
      return ResponseEntity.badRequest().body("ID Aktivitas tidak valid!");
    }

    try {
      List<String> urls = aktivitasService.uploadFoto(foto, idAktivitas);
      return ResponseEntity.ok(urls); // Kembalikan URL foto yang berhasil diunggah
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal mengunggah foto");
    }
  }
}
