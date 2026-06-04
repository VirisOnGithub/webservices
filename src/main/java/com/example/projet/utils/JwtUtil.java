package com.example.projet.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static final SecretKey key = Keys.hmacShaKeyFor(
            "QuestcequejaimeLiscordMoi2026AvecLesCoursDeMaximeMorge".getBytes() // +32 bits de clé
    );
    private static final long EXPIRATION_TIME = 3600000; // 1 heure

    // Créer un token
    public static String createToken(Integer userId, String username) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // Valider et récupérer les claims
    public static Claims validateToken(String token) {
        try {
            return (Claims) Jwts.parser().verifyWith(key).build().parse(token).getPayload();
        } catch (Exception e) {
            return null; // Token invalide
        }
    }

    // Extraire le token du header Authorization
    public static String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    // test
    public static void main(String[] args) {
        // Exemple d'utilisation
        String token = createToken(12345, "john_doe");
        System.out.println("Token: " + token);

        Claims claims = validateToken(token);
        if (claims != null) {
            System.out.println("User ID: " + claims.getSubject());
            System.out.println("Username: " + claims.get("username"));
        } else {
            System.out.println("Token invalide");
        }
    }
}

