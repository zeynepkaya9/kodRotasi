package com.jsy.platform.shared.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaForwardController {

    @RequestMapping(value = {"/login", "/register", "/onboarding", "/projects", "/workspace", "/progress", "/dashboard"})
    public String forward() {
        return "forward:/index.html";
    }
}
