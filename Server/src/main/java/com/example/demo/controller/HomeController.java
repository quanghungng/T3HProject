package com.example.demo.controller;

import com.example.demo.constant.Code;
import com.example.demo.dto.BaseResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.Cart;
import com.example.demo.model.Users;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenService tokenService;
    @Autowired
    JavaMailSender javaMailSender;
    @RequestMapping(value = "/index")
    public String home() {
        return "index";
    }
    @RequestMapping(value = "/listProduct")
    public String listProduct() { return "listProduct"; }
    @RequestMapping(value = "/productDetail")
    public String productDetail() {
        return "productDetail"; }
    @RequestMapping(value = "/registration")
    public String register() { return "registration"; }
    @RequestMapping(value = "/login")
    public String login() { return "login"; }
    @RequestMapping(value = "/cart")
    public String cart() { return "cart"; }
    @RequestMapping(value = "/checkout")
    public String checkout() { return "checkout"; }
    @RequestMapping(value = "/contact")
    public String contact() { return "contact"; }
    @RequestMapping(value = "/verifyRegister")
    public String verifyRegister() { return "verifyRegister"; }
    @RequestMapping(value = "/successVerify")
    public String successVerify() { return "successVerify"; }
    @RequestMapping(value = "/failedVerify")
    public String failedVerify() { return "failedVerify"; }

    @RequestMapping(value = "/verifyRegister/{token}")
    public String verifyRegister(@PathVariable("token") String token){
        RegisterRequest registerRequest = new RegisterRequest();
        if(tokenService.validateEmailToken(token)){
            registerRequest = tokenService.readRegisterToken(token);
        }
        else{
            return "redirect:/failedVerify";
        }


        List<Users> listUser = userRepository.findAll();

        boolean found = false;
        for(Users user:listUser){
            if(registerRequest.getUsername().equals(user.getUsername())){
                found = true;
            }
            if(registerRequest.getPhone().equals(user.getPhone())){
                found = true;
            }
            if(registerRequest.getEmail().equals(user.getEmail())){
                found = true;
            }
        }
        if(found){
            return "redirect:/failedVerify";
        }

        Users newUser = new Users();
        newUser.setName(registerRequest.getName());
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(registerRequest.getPassword());
        newUser.setAddress(registerRequest.getAddress());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPhone(registerRequest.getPhone());
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        newUser.setListRole(roles);
        newUser.setListOrder(new ArrayList<>());
        Cart cart = new Cart();
        cart.setListProduct(new ArrayList<>());
        newUser.setCart(cart);
        userRepository.save(newUser);

        return "redirect:/successVerify";
    }
}
