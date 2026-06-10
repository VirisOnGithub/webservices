package com.example.projet.dao;

import com.example.projet.model.Attachment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class AttachmentDAO {
    private static AttachmentDAO instance;

    private AttachmentDAO() {}

    public static AttachmentDAO getInstance() {
        if (instance == null) {
            instance = new AttachmentDAO();
        }
        return instance;
    }

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("liscord");

    private EntityManager getEntityManager() { return emf.createEntityManager(); }

    public void create(Attachment attachment) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.persist(attachment);
            em.getTransaction().commit();
        }
    }

    public Attachment findById(Integer ida) {
        try (EntityManager em = getEntityManager()) {
            return em.find(Attachment.class, ida);
        }
    }

    public List<Attachment> findAll() {
        try (EntityManager em = getEntityManager()) {
            return em.createQuery("SELECT a FROM Attachment a", Attachment.class).getResultList();
        }
    }

    public void update(Attachment attachment) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.merge(attachment);
            em.getTransaction().commit();
        }
    }

    public void delete(Integer ida) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            Attachment attachment = em.find(Attachment.class, ida);
            if (attachment != null) em.remove(attachment);
            em.getTransaction().commit();
        }
    }
}