package com.lays.controller;

import com.lays.dto.UserDTO;
import com.lays.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "users";
    }

    @GetMapping("/{id}")
    public String getUser(@PathVariable("id") Long id, Model model) {
        try {
            model.addAttribute("user", userService.findUserById(id));
            return "user-detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        try {
            model.addAttribute("user", userService.findUserById(id));
            return "user-edit";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute("user") UserDTO userDTO,
                             Model model) {
        try {
            userService.updateUser(id, userDTO.getUsername());
            return "redirect:/users";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", userDTO);
            return "user-edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        try {
            userService.deleteUser(id);
            return "redirect:/users";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}