package com.example.projet.tests;

import com.example.projet.util.JwtUtil;
import io.jsonwebtoken.Claims;

public class TokenTest {
    public static void main(String[] args) {
        String token = JwtUtil.createToken(1, "Alice");
        System.out.println("Generated Token: " + token);
        Claims claims = JwtUtil.validateToken(token);
        if (claims != null) {
            System.out.println("User ID: " + claims.getSubject());
            System.out.println("Username: " + claims.get("username"));
        } else {
            System.out.println("Invalid token");
        }
    }
}
