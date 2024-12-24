package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.User.User;
import com.example.demo.User.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
  @Autowired
  private UserService userService;

  @GetMapping("/login")
  public String getLogin(HttpSession session) {
    String email = (String) session.getAttribute("email");
    if (email == null) {
      return "login";
    }

    return "redirect:/dashboard";
  }

  @GetMapping("/dashboard")
  public String getDashboard(HttpSession session, Model model) {
    String email = (String) session.getAttribute("email");
    if (email == null) {
      return "redirect:/login";
    }

    model.addAttribute("email", email);

    return "dashboard";
  }

  @PostMapping("/login")
  public String postLogin(
      @RequestParam(name = "email") String email,
      @RequestParam(name = "password") String password,
      HttpSession session,
      Model model) {
    User user = userService.login(email, password);
    if (user != null) {
      session.setAttribute("email", user.getEmail());
      return "redirect:/dashboard";
    } else {
      model.addAttribute("status", "failed");
      return "login";
    }
  }

  @GetMapping("/logout")
  public String getLogout(HttpSession session) {
    session.removeAttribute("email");
    return "redirect:/";
  }
}
