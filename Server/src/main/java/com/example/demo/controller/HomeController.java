package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping(value = "/index")
    public String home() {
        return "index";
    }
    @RequestMapping(value = "/listProduct")
    public String listProduct() { return "listProduct"; }
    @RequestMapping(value = "/productDetail")
    public String productDetail() { return "productDetail"; }
    @RequestMapping(value = "/registration")
    public String register() { return "registration"; }
    @RequestMapping(value = "/login")
    public String login() { return "login"; }

}
