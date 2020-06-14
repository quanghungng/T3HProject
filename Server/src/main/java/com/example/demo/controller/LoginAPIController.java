package com.example.demo.controller;

import com.example.demo.constant.Code;
import com.example.demo.constant.Message;
import com.example.demo.dto.BaseResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.Cart;
import com.example.demo.model.Product;
import com.example.demo.model.Users;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/apiLogin")
public class LoginAPIController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenService tokenService;
    @RequestMapping(value = "register" , method = RequestMethod.POST)
    public BaseResponse register(@RequestBody RegisterRequest registerRequest){
        BaseResponse response = new BaseResponse();
        if(registerRequest.getUsername().length() < 6){
            response.setCode(Code.INVALID_DATA);
            response.setMessage("Ten dang nhap khong hop le!");
            response.setData(null);
            return response;
        }
        if(registerRequest.getPassword().length() < 6){
            response.setCode(Code.INVALID_DATA);
            response.setMessage("Password khong hop le!");
            response.setData(null);
            return response;
        }
        if(registerRequest.getName().isEmpty() ||
        registerRequest.getEmail().isEmpty()||
        registerRequest.getPhone().isEmpty() ||
        registerRequest.getPassword().isEmpty() ||
        registerRequest.getUsername().isEmpty() ||
        registerRequest.getAddress().isEmpty()){
            response.setCode(Code.INVALID_DATA);
            response.setMessage("Chua nhap du du lieu");
            response.setData(null);
            return response;
        }
        List<Users> listUser = userRepository.findAll();
        boolean found = false;
        for(Users user:listUser){
            if(registerRequest.getUsername().equals(user.getUsername())){
                response.setMessage("Ten dang nhap da duoc su sung");
                found = true;
            }
            if(registerRequest.getPhone().equals(user.getPhone())){
                response.setMessage("So dien thoai da duoc su dung!");
                found = true;
            }
            if(registerRequest.getEmail().equals(user.getEmail())){
                response.setMessage("Email da duoc su dung!");
                found = true;
            }
        }
        if(found){
            response.setCode(Code.INVALID_DATA);
            response.setData(null);
        }
        else{
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
            response.setCode(Code.SUCCESS);
            response.setMessage("Dang ki thanh cong!");
            response.setData(newUser);
        }
        return response;
    }
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public BaseResponse login(@RequestBody LoginRequest loginRequest){
        BaseResponse response = new BaseResponse();
        if(loginRequest.getUsername().isEmpty() || loginRequest.getPassword().isEmpty()){
            response.setCode(Code.INVALID_DATA);
            response.setMessage(Message.INVALID_DATA);
            response.setData(null);
            return response;
        }
        List<Users> user = userRepository.findByUsernameAndPassword(loginRequest.getUsername(),loginRequest.getPassword());
        if(user.size() == 0){
            response.setCode(Code.NOT_FOUND);
            response.setMessage(Message.NOT_FOUND);
            response.setData(null);
        }
        else{
            String token = tokenService.generateJWT(loginRequest.getUsername(),loginRequest.getPassword(),user.get(0).getId());
            response.setCode(Code.SUCCESS);
            response.setMessage("Login thanh cong!");
            response.setData(token);
        }

        return response;
    }
}
