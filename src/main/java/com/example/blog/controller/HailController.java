package com.example.blog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HailController {

    @GetMapping("/hail")
    public String hail() {
        return "Server is alive at " + java.time.LocalDateTime.now();
    }
}
