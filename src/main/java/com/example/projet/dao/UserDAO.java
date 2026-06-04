package com.example.projet.dao;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.projet.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class UserDAO {
    private static UserDAO instance;

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("journalDeBordPU");

    private UserDAO() {}

    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    // Permet d'obtenir un EntityManager pour chaque transaction
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Créer un utilisateur (Enregistrer)
    public void create(User user) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        }
    }

    // Trouver un utilisateur par son ID
    public User findById(Integer idu) {
        try (EntityManager em = getEntityManager()) {
            return em.find(User.class, idu);
        }
    }

    // Récupérer tous les utilisateurs
    public List<User> findAll() {
        try (EntityManager em = getEntityManager()) {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        }
    }

    // Mettre à jour un utilisateur
    public void update(User user) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        }
    }

    // Supprimer un utilisateur
    public void delete(Integer idu) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            User user = em.find(User.class, idu);
            if (user != null) {
                em.remove(user);
            }
            em.getTransaction().commit();
        }
    }

    public User authenticate(String username, String password) {
//        System.out.println("username: " + username);
//        System.out.println("password: " + password);
        List<User> users = findAll();
        for (User u : users) {
//            System.out.println(BCrypt.verifyer().verify(password.toCharArray(), u.getPassword()).verified);
            if (u.getPseudo().equals(username) && BCrypt.verifyer().verify(password.toCharArray(), u.getPassword()).verified) {
                return u; // Authentification réussie
            }
        }
        return null; // Authentification échouée
    }
}