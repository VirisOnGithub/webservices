package com.example.projet.tests;

import com.example.projet.dao.ChannelDAO;
import com.example.projet.dao.MessageDAO;
import com.example.projet.dao.UserDAO;
import com.example.projet.model.Channel;
import com.example.projet.model.Message;
import com.example.projet.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("=== DÉBUT DES TESTS JPA ===");

        // 1. Instanciation des DAOs
        UserDAO userDAO = new UserDAO();
        ChannelDAO channelDAO = new ChannelDAO();
        MessageDAO messageDAO = new MessageDAO();

        try {
            // ==========================================================
            // TEST 1 : Lecture des Utilisateurs (Données Jouets)
            // ==========================================================
            System.out.println("\n--- Test 1 : Lecture des utilisateurs ---");
            List<User> users = userDAO.findAll();
            System.out.println("Nombre d'utilisateurs trouvés : " + users.size());
            for (User u : users) {
                System.out.println("User trouvé : " + u.getPseudo() + " (" + u.getEmail() + ")");
            }

            // ==========================================================
            // TEST 2 : Lecture des Canaux et de leur Créateur
            // ==========================================================
            System.out.println("\n--- Test 2 : Lecture des canaux ---");
            List<Channel> channels = channelDAO.findAll();
            for (Channel c : channels) {
                System.out.println("Canal : " + c.getName() + " | Public : " + c.getIsPublic());
                // Grâce à JPA, on accède directement à l'objet User relié !
                System.out.println("  -> Créé par : " + c.getCreator().getPseudo());
            }

            // ==========================================================
            // TEST 3 : Lecture des Messages du canal 'Général' (c1)
            // ==========================================================
            System.out.println("\n--- Test 3 : Messages du canal 'Général' ---");
            List<Message> generalMessages = messageDAO.findByChannel(1);
            for (Message m : generalMessages) {
                System.out.println("[" + m.getAuthor().getPseudo() + "] : " + m.getContent());
            }

            // ==========================================================
            // TEST 4 : Insertion d'un nouveau Message (Écriture)
            // ==========================================================
            System.out.println("\n--- Test 4 : Insertion d'un nouveau message ---");

            // On récupère Bob (u2) et le canal Général (c1) pour lier le message
            User bob = userDAO.findById(2);
            Channel generalChannel = channelDAO.findById(1);

            if (bob != null && generalChannel != null) {
                Message newMessage = new Message();
                newMessage.setContent("Ceci est un message de test généré par JPA !");
                newMessage.setSendDate(LocalDateTime.now());
                newMessage.setEdited(false);
                newMessage.setAuthor(bob);
                newMessage.setChannel(generalChannel);

                // Sauvegarde en base
                messageDAO.create(newMessage);
                System.out.println("✅ Nouveau message inséré avec succès !");

                // Vérification immédiate de la suppression pour laisser la base propre (optionnel)
                System.out.println("\n--- Nettoyage du message de test ---");
                messageDAO.delete(newMessage.getIdm());
                System.out.println("✅ Message de test supprimé.");
            } else {
                System.out.println("❌ Erreur : Bob ou le Canal Général n'existent pas en base.");
            }

        } catch (Exception e) {
            System.err.println("❌ Une erreur est survenue lors des tests JPA :");
            e.printStackTrace();
        }

        System.out.println("\n=== FIN DES TESTS JPA ===");
    }
}