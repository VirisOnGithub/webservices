package com.example.projet.tests;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class BcryptTest {
    public static void main(String[] args) {
        String password = "test123";
        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        System.out.println("Mot de passe original : " + password);
        System.out.println("Mot de passe hashé : " + hashedPassword);

        // Vérification du mot de passe
        String inputPassword = "test123"; // Essayez de changer ce mot de passe pour tester la vérification
        BCrypt.Result result = BCrypt.verifyer().verify(inputPassword.toCharArray(), hashedPassword);
        if (result.verified) {
            System.out.println("Mot de passe vérifié avec succès !");
        } else {
            System.out.println("Mot de passe incorrect !");
        }
    }
}
