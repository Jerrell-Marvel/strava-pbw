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
      Model model) {
    int pageSize = 10;
    List<Lomba> lombaList = lombaService.getLombaByPage(page, pageSize);
    int totalLomba = lombaService.getLombaCount();
    int pageCount = (int) Math.ceil((double) totalLomba / pageSize);

    model.addAttribute("lombaList", lombaList);
    model.addAttribute("currentPage", page);
    model.addAttribute("pageCount", pageCount);

    return "lomba-list";
  }

  @GetMapping("/lomba/{id}/leaderboard")
  public String getLeaderboard(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
      @PathVariable("id") Integer idLomba, Model model) {
    List<Leaderboard> leaderboard = lombaService.getLeaderboardByLombaId(idLomba, page);

    int totalLeaderboard = lombaService.getLeaderboardByLombaIdCount(idLomba);
    int pageCount = (int) Math.ceil((double) totalLeaderboard / 10);

    model.addAttribute("currentPage", page);
    model.addAttribute("pageCount", pageCount);

    model.addAttribute("leaderboard", leaderboard);
    return "lomba-leaderboard";
  }

  @GetMapping("/admin/lomba/tambah")
  @RequiredRole("admin")
  public String getTambahLombaPage(Model model) {
    model.addAttribute("lomba", new Lomba());
    return "lomba-tambah";
  }

  @PostMapping("/admin/lomba/tambah")
  @RequiredRole("admin")
  public String tambahLomba(@Valid Lomba lomba, BindingResult result) {
    if (result.hasErrors()) {
      return "lomba-tambah";
    }
    lombaService.addLomba(lomba);
    return "redirect:/admin/lomba";
  }

  @GetMapping("/member/lomba/berlangsung")
  @RequiredRole("member")
  public String getLombaBerlangsung(
      @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
      Model model,
      HttpSession session) {
    Integer idUser = (Integer) session.getAttribute("idUser");
    int pageSize = 10;

    List<LombaBerlangsung> lombaList = lombaService.getLombaBerlangsungWithStatus(idUser, page, pageSize);
    int totalLomba = lombaService.getLombaBerlangsungWithStatusCount();
    int pageCount = (int) Math.ceil((double) totalLomba / pageSize);

    model.addAttribute("lombaList", lombaList);
    model.addAttribute("currentPage", page);
    model.addAttribute("pageCount", pageCount);

    return "lomba-berlangsung";
  }

  @GetMapping("/member/lomba/{id}/pilih-aktivitas")
  @RequiredRole("member")
  public String pilihAktivitas(
      @PathVariable("id") Integer idLomba,
      Model model,
      HttpSession session) {
    Integer idUser = (Integer) session.getAttribute("idUser");
    List<Aktivitas> aktivitasList = lombaService.getAktivitasNotInLombaMember(idUser, idLomba);
    model.addAttribute("aktivitasList", aktivitasList);
    model.addAttribute("idLomba", idLomba);
    return "pilih-aktivitas";
  }

  @PostMapping("/member/lomba/{id}/pilih-aktivitas")
  @RequiredRole("member")
  public String submitAktivitas(
      @PathVariable("id") Integer idLomba,
      @RequestParam("idAktivitas") Integer idAktivitas,
      HttpSession session) {
    Integer idUser = (Integer) session.getAttribute("idUser");
    lombaService.addLombaMember(idLomba, idUser, idAktivitas);
    return "redirect:/member/lomba/diikuti";
  }

  @GetMapping("/member/lomba/diikuti")
  @RequiredRole("member")
  public String lombaDiikuti(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
      Model model, HttpSession session) {
    Integer idUser = (Integer) session.getAttribute("idUser");
    List<LombaMember> lombaDiikuti = lombaService.getLombaDiikuti(idUser, page);
    model.addAttribute("lombaDiikuti", lombaDiikuti);

    int totalLomba = lombaService.getLombaDiikutiCount(idUser);
    int pageCount = (int) Math.ceil((double) totalLomba / 10);

    model.addAttribute("currentPage", page);
    model.addAttribute("pageCount", pageCount);

    System.out.println("lorem " + pageCount);
    return "lomba-diikuti";
  }

}
