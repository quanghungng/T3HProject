package com.example.demo.controller.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {
    @RequestMapping("indexadmin")
    public String indexAdmin(){
        return "Admin/indexadmin";
    }
}
