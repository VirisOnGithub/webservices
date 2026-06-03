package com.example.projet.dao;

import com.example.projet.model.Message;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class MessageDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("journalDeBordPU");

    private EntityManager getEntityManager() { return emf.createEntityManager(); }

    public void create(Message message) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(message);
            em.getTransaction().commit();
        } finally { em.close(); }
    }

    public Message findById(Integer idm) {
        EntityManager em = getEntityManager();
        try { return em.find(Message.class, idm); } finally { em.close(); }
    }

    // Récupérer les messages d'un canal spécifique (très utile pour la phase des servlets)
    public List<Message> findByChannel(Integer idc) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT m FROM Message m WHERE m.channel.idc = :idc ORDER BY m.sendDate ASC", Message.class)
                    .setParameter("idc", idc)
                    .getResultList();
        } finally { em.close(); }
    }

    public List<Message> findAll() {
        EntityManager em = getEntityManager();
        try { return em.createQuery("SELECT m FROM Message m", Message.class).getResultList(); } finally { em.close(); }
    }

    public void update(Message message) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(message);
            em.getTransaction().commit();
        } finally { em.close(); }
    }

    public void delete(Integer idm) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Message message = em.find(Message.class, idm);
            if (message != null) em.remove(message);
            em.getTransaction().commit();
        } finally { em.close(); }
    }
}