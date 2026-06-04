package com.example.projet.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

/// Utilitaire pour crypter et décrypter les mots de passe avec BCrypt
public class BCryptUtil {
    private static final int COST = 12;

    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(COST, password.toCharArray());
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
        return result.verified;
    }
}
