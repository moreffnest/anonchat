package com.moreffnest.anonchat.controllers;

import com.moreffnest.anonchat.dto.UserDTO;
import com.moreffnest.anonchat.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/signup")
    public String getRegistrationForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken))
            return "redirect:/chatform";

        model.addAttribute("user", new UserDTO());
        return "registration";
    }

    @PostMapping("/registration")
    public String register(@ModelAttribute UserDTO user) {
        userService.createNewUser(user);
        return "redirect:/login";
    }
}
