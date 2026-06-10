package com.example.projet.dao;

import com.example.projet.model.Channel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import java.util.UUID;

public class ChannelDAO {
    private static ChannelDAO instance;

    private ChannelDAO() {}

    public static ChannelDAO getInstance() {
        if (instance == null) {
            instance = new ChannelDAO();
        }
        return instance;
    }

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("liscord");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Channel channel) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.persist(channel);
            em.getTransaction().commit();
        }
    }

    public Channel findById(UUID idc) {
        try (EntityManager em = getEntityManager()) {
            return em.find(Channel.class, idc);
        }
    }

    public List<Channel> findAll() {
        try (EntityManager em = getEntityManager()) {
            return em.createQuery("SELECT c FROM Channel c", Channel.class).getResultList();
        }
    }

    public List<Channel> findAuthorizedChannels(UUID idu) {
        System.out.println("[DAO] findAuthorizedChannels pour user id=" + idu);
        try (EntityManager em = getEntityManager()) {
            return em.createQuery("SELECT DISTINCT c FROM Channel c LEFT JOIN c.members u WHERE u.id = :idu OR c.isPublic = true", Channel.class)
                    .setParameter("idu", idu)
                    .getResultList();
        }
    }

    public void update(Channel channel) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.merge(channel);
            em.getTransaction().commit();
        }
    }

    public void delete(UUID idc) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            Channel channel = em.find(Channel.class, idc);
            if (channel != null) {
                em.remove(channel);
            }
            em.getTransaction().commit();
        }
    }
}