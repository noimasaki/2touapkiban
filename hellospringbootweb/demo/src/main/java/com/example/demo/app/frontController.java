package com.example.demo.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class frontController {
    @GetMapping
    public String home(){
        return "home";
    }
}
