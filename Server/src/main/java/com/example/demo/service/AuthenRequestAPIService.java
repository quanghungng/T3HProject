package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenRequestAPIService {
    @Autowired
    TokenService tokenService;
    public  String checkValidOfTokenAndId(String token, String id){
        if(tokenService.validateToken(token)){
            String idInToken = tokenService.readJWT(token);
            if(id.equals(idInToken)){
                return "ok";
            }
            else{
                return "Not allowed";
            }
        }
        else{
            return "Invalid token";
        }
    }
}
