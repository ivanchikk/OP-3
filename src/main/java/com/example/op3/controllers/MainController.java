package com.example.op3.controllers;

import com.example.op3.models.User;
import com.example.op3.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@SessionAttributes("user")
public class MainController {
    private final UserService userService;
    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int PASSWORD_MAX_LENGTH = 50;

    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupUser(@ModelAttribute("user") User user, Model model,
                             @RequestParam(name = "username") String username,
                             @RequestParam(name = "password") String password,
                             @RequestParam(name = "passwordConfirm") String passwordConfirm) {
        return userService.getByUsername(username).map(existingUser -> {
            model.addAttribute("warningUserExist", "User with this username already exists");
            return "signup";
        }).orElseGet(() -> {
            if (username.length() > USERNAME_MAX_LENGTH)
                model.addAttribute("warningUsername", "Username max length is " + USERNAME_MAX_LENGTH);
            if (password.length() > PASSWORD_MAX_LENGTH)
                model.addAttribute("warningPasswordLength", "Password max length is " + PASSWORD_MAX_LENGTH);
            if (!Objects.equals(password, passwordConfirm))
                model.addAttribute("warningPasswordConfirm", "Passwords don't match");
            if (username.length() > USERNAME_MAX_LENGTH || password.length() > PASSWORD_MAX_LENGTH || !Objects.equals(password, passwordConfirm))
                return "signup";

            user.setUsername(username);
            user.setPassword(password);
            userService.add(user);
            return "redirect:/login";
        });
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/signup";
    }

}
