package com.example.demo.service;

import com.example.demo.model.Users;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class TokenService {
    static final long EXPIRATIONTIME = 864_000_000;
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
}
