package com.example.projet.dao;

import com.example.projet.model.Channel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class ChannelDAO {
    private static ChannelDAO instance;

    private ChannelDAO() {}

    public static ChannelDAO getInstance() {
        if (instance == null) {
            instance = new ChannelDAO();
        }
        return instance;
    }

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("journalDeBordPU");

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

    public Channel findById(Integer idc) {
        try (EntityManager em = getEntityManager()) {
            return em.find(Channel.class, idc);
        }
    }

    public List<Channel> findAll() {
        try (EntityManager em = getEntityManager()) {
            return em.createQuery("SELECT c FROM Channel c", Channel.class).getResultList();
        }
    }

    public void update(Channel channel) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.merge(channel);
            em.getTransaction().commit();
        }
    }

    public void delete(Integer idc) {
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