package com.example.demo.User;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.RequiredRole;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class UserController {
  @Autowired
  private UserService userService;

  @GetMapping("/register")
  public String getRegister(User user) {
    return "register";
  }

  @PostMapping("/register")
  public String postRegister(@Valid User user, BindingResult bindingResult) {

    if (!user.getConfirmpassword().equals(user.getPassword())) {
      bindingResult.rejectValue("confirmpassword", "Confirmation Password doesn't match",
          "Confirmation Password doesn't match");
      return "register";
    }

    // System.out.println(bindingResult.hasErrors());
    if (bindingResult.hasErrors()) {
      return "register";
    }

    if (!userService.register(user)) {
      bindingResult.rejectValue("email", "Email has been used", "Email has been used");
      return "register";
    }

    return "redirect:/results";
  }

  @GetMapping("/results")
  public String getResults() {
    return "results";
  }

  @GetMapping("/admin/data-member")
  @RequiredRole("admin")
  public String getDataMember(
      @RequestParam(name = "page", required = false, defaultValue = "1") int currentPage,
      Model model) {
    List<User> memberList = userService.getMembers(currentPage);
    int rowCount = userService.getMemberCount();
    int pageCount = (int) Math.ceil((double) rowCount / 10);

    model.addAttribute("pageCount", pageCount);
    model.addAttribute("currentPage", currentPage);
    model.addAttribute("memberList", memberList);
    return "data-member";
  }

  @GetMapping("/admin/member/edit/{id}")
  @RequiredRole("admin")
  public String getEditMember(@PathVariable("id") Integer idUser, Model model) {
    User user = userService.getUserById(idUser);
    if (user == null) {
      return "redirect:/admin/data-member"; // Redirect jika user tidak ditemukan
    }
    model.addAttribute("user", user);
    return "edit-member";
  }

  @PostMapping("/admin/member/edit")
  @RequiredRole("admin")
  public String postEditMember(@Valid User user, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "edit-member"; // Kembali ke form jika ada error
    }

    try {
      userService.updateMember(user);
      return "redirect:/admin/data-member"; // Redirect ke daftar member setelah berhasil
    } catch (Exception e) {
      bindingResult.rejectValue("email", "error.email", "Email sudah digunakan");
      return "edit-member";
    }
  }

}
