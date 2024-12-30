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

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class UserController {
  @Autowired
  private UserService userService;

  @GetMapping("/")
  public String getHome(HttpSession session, Model model) {
    model.addAttribute("userRole", session.getAttribute("role"));

    return "index";
  }

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
    model.addAttribute("userRole", "admin");
    return "data-member";
  }

  @GetMapping("/admin/member/edit/{id}")
  @RequiredRole("admin")
  public String getEditMember(@PathVariable("id") Integer idUser, Model model) {
    User user = userService.getUserById(idUser);
    if (user == null) {
      model.addAttribute("userRole", "admin");
      return "redirect:/admin/data-member";
    }
    model.addAttribute("user", user);
    model.addAttribute("userRole", "admin");
    return "edit-member";
  }

  @PostMapping("/admin/member/edit")
  @RequiredRole("admin")
  public String postEditMember(@Valid User user, BindingResult bindingResult, Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("userRole", "admin");
      return "edit-member";
    }

    try {
      userService.updateMember(user);
      model.addAttribute("userRole", "admin");
      return "redirect:/admin/data-member";
    } catch (Exception e) {
      bindingResult.rejectValue("email", "error.email", "Email sudah digunakan");
      model.addAttribute("userRole", "admin");
      return "edit-member";
    }
  }

}
