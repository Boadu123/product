package com.example.product.Service;

import java.util.Date;

import javax.crypto.SecretKey;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final Dotenv dotenv = Dotenv.load();
    private final String SECRET_KEY = dotenv.get("SECRET_KEY");
    private final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

}
