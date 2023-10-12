package gp.pyinsa.jroms.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import gp.pyinsa.jroms.dtos.LoginDto;

@Controller
@RequestMapping("/mng")
public class AuthController {

    @GetMapping("/")
    public String showMainMenu(Model model, Principal p) {
        String name = p.getName();
        model.addAttribute("name", name);
        return "redirect:/mng/dashboard";
    }

    @GetMapping("/login")
    public String loginForm(Model model, Principal p) {
        if (p != null) {
            return "redirect:/mng/dashboard";
        }

        model.addAttribute("loginDto", new LoginDto());
        return "login";
    }

}
