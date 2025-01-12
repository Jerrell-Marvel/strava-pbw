package com.example.demo.Lomba;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.RequiredRole;
import com.example.demo.Aktivitas.Aktivitas;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class LombaController {

  @Autowired
  private LombaService lombaService;

  @GetMapping("/admin/lomba")
  @RequiredRole("admin")
  public String getLombaList(
      @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(name = "search", required = false, defaultValue = "") String search,
      Model model) {

    int pageSize = 10;
    List<Lomba> lombaList = lombaService.getLombaBySearch(search, page, pageSize);
    int totalLomba = lombaService.getLombaCount(search);
    int pageCount = (int) Math.ceil((double) totalLomba / pageSize);

    model.addAttribute("lombaList", lombaList);
    model.addAttribute("currentPage", page);
    model.addAttribute("pageCount", pageCount);
    model.addAttribute("search", search); // Tambahkan search ke model
    model.addAttribute("userRole", "admin");

    return "lomba-list";
  }

  @GetMapping("/lomba/{id}/leaderboard")
  public String getLeaderboard(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
      @PathVariable("id") Integer idLomba, Model model, HttpSession session) {
    List<Leaderboard> leaderboard = lombaService.getLeaderboardByLombaId(idLomba, page);

    int totalLeaderboard = lombaService.getLeaderboardByLombaIdCount(idLomba);
    int pageCount = (int) Math.ceil((double) totalLeaderboard / 10);

    model.addAttribute("currentPage", page);
    model.addAttribute("pageCount", pageCount);

    model.addAttribute("leaderboard", leaderboard);
    model.addAttribute("userRole", session.getAttribute("role"));
    return "lomba-leaderboard";
  }

  @GetMapping("/admin/lomba/tambah")
  @RequiredRole("admin")
  public String getTambahLombaPage(Model model) {
    model.addAttribute("lomba", new Lomba());
    model.addAttribute("userRole", "admin");
    return "lomba-tambah";
  }

  @PostMapping("/admin/lomba/tambah")
  @RequiredRole("admin")
  public String tambahLomba(@Valid Lomba lomba, BindingResult result, Model model) {
    // if (result.hasErrors()) {
    // model.addAttribute("userRole", "admin");
    // return "lomba-tambah";
    // }
    // lombaService.addLomba(lomba);
    // model.addAttribute("userRole", "admin");
    // return "redirect:/admin/lomba";
    if (lomba.getTanggalMulai() != null && lomba.getTanggalSelesai() != null) {
      if (!lomba.getTanggalMulai().isBefore(lomba.getTanggalSelesai())) {
        result.rejectValue("tanggalMulai", "error.lomba", "Tanggal Mulai harus sebelum Tanggal Selesai.");
        model.addAttribute("tanggalError", "Tanggal Mulai harus sebelum Tanggal Selesai.");
      }
    }
    if (result.hasErrors()) {
      model.addAttribute("userRole", "admin");
      return "lomba-tambah";
    }
    lombaService.addLomba(lomba);
    model.addAttribute("userRole", "admin");
    return "redirect:/admin/lomba";
  }

  @GetMapping("/member/lomba/berlangsung")
  @RequiredRole("member")
  public String getLombaBerlangsung(
      @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(name = "search", required = false, defaultValue = "") String search,
      Model model,
      HttpSession session) {

    Integer idUser = (Integer) session.getAttribute("idUser");
    int pageSize = 10;

    List<LombaBerlangsung> lombaList = lombaService.getLombaBerlangsungWithSearch(idUser, search, page, pageSize);
    int totalLomba = lombaService.getLombaBerlangsungWithSearchCount(idUser, search);
    int pageCount = (int) Math.ceil((double) totalLomba / pageSize);

    model.addAttribute("lombaList", lombaList);
    model.addAttribute("currentPage", page);
    model.addAttribute("pageCount", pageCount);
    model.addAttribute("search", search); // Tambahkan search ke model
    model.addAttribute("userRole", "member");

    return "lomba-berlangsung";
  }

  @GetMapping("/member/lomba/{id}/pilih-aktivitas")
  @RequiredRole("member")
  public String pilihAktivitas(
      @PathVariable("id") Integer idLomba,
      @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
      Model model,
      HttpSession session) {
    Integer idUser = (Integer) session.getAttribute("idUser");
    int pageSize = 10;
    List<Aktivitas> aktivitasList = lombaService.getAktivitasNotInLombaMember(idUser, idLomba, page);

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

    int totalLomba = lombaService.getAktivitasNotInLombaMemberCount(idUser, idLomba);
    int pageCount = (int) Math.ceil((double) totalLomba / pageSize);
    model.addAttribute("aktivitasList", aktivitasList);
    model.addAttribute("idLomba", idLomba);
    model.addAttribute("currentPage", page);
    model.addAttribute("pageCount", pageCount);
    model.addAttribute("userRole",
        "member");
    return "pilih-aktivitas";
  }

  @PostMapping("/member/lomba/{id}/pilih-aktivitas")
  @RequiredRole("member")
  public String submitAktivitas(
      @PathVariable("id") Integer idLomba,
      @RequestParam("idAktivitas") Integer idAktivitas,
      HttpSession session, Model model) {
    Integer idUser = (Integer) session.getAttribute("idUser");
    lombaService.addLombaMember(idLomba, idUser, idAktivitas);
    model.addAttribute("userRole", "member");
    return "redirect:/member/lomba/diikuti";
  }

  @GetMapping("/member/lomba/diikuti")
  @RequiredRole("member")
  public String lombaDiikuti(
      @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(name = "search", required = false, defaultValue = "") String search,
      Model model,
      HttpSession session) {

    Integer idUser = (Integer) session.getAttribute("idUser");
    int pageSize = 10;

    List<LombaMember> lombaDiikuti = lombaService.getLombaDiikutiWithSearch(idUser, search, page, pageSize);
    int totalLomba = lombaService.getLombaDiikutiWithSearchCount(idUser, search);
    int pageCount = (int) Math.ceil((double) totalLomba / pageSize);

    model.addAttribute("lombaDiikuti", lombaDiikuti);
    model.addAttribute("currentPage", page);
    model.addAttribute("pageCount", pageCount);
    model.addAttribute("search", search); // Tambahkan search ke model
    model.addAttribute("userRole", "member");

    return "lomba-diikuti";
  }

}
