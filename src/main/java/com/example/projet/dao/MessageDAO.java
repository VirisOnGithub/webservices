package com.example.projet.dao;

import com.example.projet.model.Message;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import java.util.UUID;

public class MessageDAO {
    private static MessageDAO instance;

    private MessageDAO() {}

    public static MessageDAO getInstance() {
        if (instance == null) {
            instance = new MessageDAO();
        }
        return instance;
    }

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("liscord");

    private EntityManager getEntityManager() { return emf.createEntityManager(); }

    public void create(Message message) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.persist(message);
            em.getTransaction().commit();
        }
    }

    public Message findById(Integer idm) {
        try (EntityManager em = getEntityManager()) {
            return em.find(Message.class, idm);
        }
    }

    // Récupérer les messages d'un canal spécifique (très utile pour la phase des servlets)
    public List<Message> findByChannel(UUID idc) {
        try (EntityManager em = getEntityManager()) {
            return em.createQuery("SELECT m FROM Message m WHERE m.channel.idc = :idc ORDER BY m.sendDate ASC", Message.class)
                    .setParameter("idc", idc)
                    .getResultList();
        }
    }

    public List<Message> findAll() {
        try (EntityManager em = getEntityManager()) {
            return em.createQuery("SELECT m FROM Message m", Message.class).getResultList();
        }
    }

    public void update(Message message) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.merge(message);
            em.getTransaction().commit();
        }
    }

    public void delete(Integer idm) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            Message message = em.find(Message.class, idm);
            if (message != null) em.remove(message);
            em.getTransaction().commit();
        }
    }
}