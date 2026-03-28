package com.lays.controller;

import com.lays.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model,
                               HttpServletRequest request) {

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .build()
                .toUriString();

        String error = registrationService.register(
                username,
                email,
                password,
                confirmPassword,
                baseUrl
        );

        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "register";
        }

        return "redirect:/login?registered";
    }

    @GetMapping("/verification")
    public String verifyUser(@RequestParam("code") String code, Model model) {
        try {
            registrationService.verify(code);
            model.addAttribute("message", "Аккаунт подтверждён. Теперь можно войти.");
            return "login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}