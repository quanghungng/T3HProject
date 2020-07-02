package com.example.demo.service;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.Users;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TokenService {
    static final long EXPIRATIONTIME = 864_000_000;
    static final long EXPIRATIONTIMEFORMAILVERIFY = 120_000;
    static final String SECRET = "123456zx";
    @Autowired
    UserRepository userRepository;



    public String generateJWT(String username, String password, String id) {
        String JWT = Jwts.builder()
                .setSubject(username)
                .setSubject(password)
                .setSubject(id)
                .setExpiration(new Date(System.currentTimeMillis() +
                        EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        return JWT;
    }

    public String readJWT(String token) {
        if (token != null) {
            String user = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return user;
        }
        return null;
    }


    public boolean validateToken(String token){
        try{
            String userId = readJWT(token);
            Optional<Users> optionalUsers = userRepository.findById(userId);
            if(!optionalUsers.isPresent()){
                throw new Exception("user not found");
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public String generateJWTForRegister(String username, String password, String name , String phone, String email, String address ){



        String JWT = Jwts.builder()
                .setSubject(username)
                .claim("username",username)
                .claim("password",password)
                .claim("name",name)
                .claim("phone",phone)
                .claim("email",email)
                .claim("address", address)
                .setExpiration(new Date(System.currentTimeMillis() +
                        EXPIRATIONTIMEFORMAILVERIFY))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        return JWT;
    }

    public RegisterRequest readRegisterToken(String token){
        String username = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .get("username",String.class);
        String password = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .get("password",String.class);
        String name = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .get("name",String.class);
        String phone = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .get("phone",String.class);
        String email = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .get("email",String.class);
        String address = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .get("address",String.class);
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(password);
        registerRequest.setName(name);
        registerRequest.setEmail(email);
        registerRequest.setPhone(phone);
        registerRequest.setAddress(address);
        return registerRequest;
    }
    public boolean validateEmailToken(String token){
        try{
            RegisterRequest registerRequest = readRegisterToken(token);
            if (registerRequest != null)
            return true;
        }catch (Exception e){
            return false;
        }
        return false;
    }

}
