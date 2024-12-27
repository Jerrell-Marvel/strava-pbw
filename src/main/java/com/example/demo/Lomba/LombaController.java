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
  @RequiredRole("admin")
  public String getLeaderboard(@PathVariable("id") Integer idLomba, Model model) {
    List<Leaderboard> leaderboard = lombaService.getLeaderboardByLombaId(idLomba);
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

}
