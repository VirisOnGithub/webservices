package com.example.projet.dao;

import com.example.projet.model.Attachment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class AttachmentDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("journalDeBordPU");

    private EntityManager getEntityManager() { return emf.createEntityManager(); }

    public void create(Attachment attachment) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(attachment);
            em.getTransaction().commit();
        } finally { em.close(); }
    }

    public Attachment findById(Integer ida) {
        EntityManager em = getEntityManager();
        try { return em.find(Attachment.class, ida); } finally { em.close(); }
    }

    public List<Attachment> findAll() {
        EntityManager em = getEntityManager();
        try { return em.createQuery("SELECT a FROM Attachment a", Attachment.class).getResultList(); } finally { em.close(); }
    }

    public void update(Attachment attachment) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(attachment);
            em.getTransaction().commit();
        } finally { em.close(); }
    }

    public void delete(Integer ida) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Attachment attachment = em.find(Attachment.class, ida);
            if (attachment != null) em.remove(attachment);
            em.getTransaction().commit();
        } finally { em.close(); }
    }
}