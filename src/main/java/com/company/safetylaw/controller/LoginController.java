package com.company.safetylaw.controller;

import com.company.safetylaw.security.LoginUser;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @GetMapping({"/", "/index", "/index.html", "/login"})
    public String index(
            Model model,
            Authentication authentication,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String expired,
            @Value("${app.auth.development-login-enabled:false}") boolean developmentLogin,
            @Value("${app.demo}") boolean localPreview) {
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return "redirect:/dashboard";
        }
        model.addAttribute("developmentLogin", developmentLogin);
        model.addAttribute("localPreview", localPreview);
        model.addAttribute("loggedOut", logout != null);
        String message = null;
        if (error != null) {
            message =
                    switch (error) {
                        case "unavailable" -> "인사 정보를 조회하지 못했습니다. 잠시 후 다시 시도해 주세요.";
                        case "disabled" -> "사번 입력 로그인이 비활성화되어 있습니다.";
                        default -> "사번을 확인해 주세요. 인사 정보에서 사용자를 식별할 수 없습니다.";
                    };
        } else if (expired != null) {
            message = "로그인 화면이 만료되었습니다. 사번을 다시 입력해 주세요.";
        }
        model.addAttribute("loginError", message);
        return "index";
    }
}
