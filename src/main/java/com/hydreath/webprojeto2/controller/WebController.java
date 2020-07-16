package com.hydreath.webprojeto2.controller;


import com.hydreath.webprojeto2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @Autowired
    UserRepository repository;

    @GetMapping("/")
    public String login(){
        return "home";
    }
}
