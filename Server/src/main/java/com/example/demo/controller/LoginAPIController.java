package com.example.demo.controller;

import com.example.demo.constant.Code;
import com.example.demo.constant.Message;
import com.example.demo.dto.BaseResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.Cart;
import com.example.demo.model.Product;
import com.example.demo.model.UserInfo;
import com.example.demo.model.Users;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        if(registerRequest.getName().isEmpty() ||
                registerRequest.getEmail().isEmpty()||
                registerRequest.getPhone().isEmpty() ||
                registerRequest.getPassword().isEmpty() ||
                registerRequest.getUsername().isEmpty() ||
                registerRequest.getAddress().isEmpty()){
            response.setCode(Code.INVALID_DATA);
            response.setMessage("Missing data fields");
            response.setData(null);
            return response;
        }
        if(registerRequest.getUsername().length() < 6){
            response.setCode(Code.INVALID_DATA);
            response.setMessage("Invalid Username");
            response.setData(null);
            return response;
        }
        if(registerRequest.getPassword().length() < 6){
            response.setCode(Code.INVALID_DATA);
            response.setMessage("Invalid Password");
            response.setData(null);
            return response;
        }
        List<Users> listUser = userRepository.findAll();
        boolean found = false;
        for(Users user:listUser){
            if(registerRequest.getUsername().equals(user.getUsername())){
                response.setMessage("Username has been used");
                found = true;
            }
            if(registerRequest.getPhone().equals(user.getPhone())){
                response.setMessage("Phone number has been used");
                found = true;
            }
            if(registerRequest.getEmail().equals(user.getEmail())){
                response.setMessage("Email has been used");
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
            response.setMessage("Registered Successfully");
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

    @RequestMapping(value = "getUserInfo", method = RequestMethod.GET)
    public BaseResponse getUserInfo(@RequestHeader("token") String token){
        BaseResponse response = new BaseResponse();
        if(!tokenService.validateToken(token)){
            response.setCode(Code.NOT_FOUND);
            response.setMessage(Message.NOT_FOUND);
            response.setData(null);
            return response;
        }
        String userID = tokenService.readJWT(token);
        Optional<Users> user = userRepository.findById(userID);
        if(!user.isPresent()){
            response.setCode(Code.NOT_FOUND);
            response.setMessage(Message.NOT_FOUND);
            response.setData(null);
            return response;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.get().getId());
        userInfo.setUsername(user.get().getUsername());
        userInfo.setCart(user.get().getCart());
        userInfo.setListRole(user.get().getListRole());
        userInfo.setAddress(user.get().getAddress());
        userInfo.setEmail(user.get().getAddress());
        userInfo.setPhone(user.get().getPhone());
        userInfo.setName(user.get().getName());

        response.setCode(Code.SUCCESS);
        response.setMessage(Message.GET_DATA_SUCCESS);
        response.setData(userInfo);
        return response;
    }
}
