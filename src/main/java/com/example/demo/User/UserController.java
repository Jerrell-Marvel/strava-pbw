package com.example.demo.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.stereotype.Controller;
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
            bindingResult.rejectValue("username", "Username has been used", "Username has been used");
            return "register";
        }

        return "redirect:/results";
    }

    @GetMapping("/results")
    public String getResults() {
        return "results";
    }
}
