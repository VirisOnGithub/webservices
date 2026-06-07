package com.example.projet.websocket;

import com.example.projet.dao.ChannelDAO;
import com.example.projet.dao.UserDAO;
import com.example.projet.model.Channel;
import com.example.projet.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/channels")
public class ChannelWebSocket {

    // --- Etat partagé (static car une instance par connexion) ---
    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();

    // DAOs et ObjectMapper — réutilisés entre méthodes via instance
    private final ChannelDAO channelDAO = ChannelDAO.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // -------------------------------------------------------
    // Cycle de vie
    // -------------------------------------------------------

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("[WS] Connexion ouverte : " + session.getId());
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        sessions.remove(session);
        System.out.println("[WS] Connexion fermée : " + session.getId() + " — " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable t) {
        System.err.println("[WS] Erreur sur " + session.getId() + " : " + t.getMessage());
        sessions.remove(session);
    }

    // -------------------------------------------------------
    // Réception des messages
    // -------------------------------------------------------

    @OnMessage
    public void onMessage(String raw, Session session) {
        System.out.println("[WS] Message reçu de " + session.getId() + " : " + raw);
        try {
            JsonNode node = objectMapper.readTree(raw);

            if (!node.has("action")) {
                sendError(session, "Le champ 'action' est obligatoire.");
                return;
            }

            switch (node.get("action").asText()) {
                case "GET_CHANNELS"    -> handleGetChannels(session);
                case "CREATE_CHANNEL"  -> handleCreateChannel(node.get("payload"), session);
                default                -> sendError(session, "Action inconnue : " + node.get("action").asText());
            }

        } catch (Exception e) {
            sendError(session, "Message invalide : " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Handlers — équivalents de doGet / doPost
    // -------------------------------------------------------

    /** Equivalent de doGet — réponse uniquement à l'appelant */
    private void handleGetChannels(Session session) {
        try {
            List<Channel> channels = channelDAO.findAll();
            sendToSession(session, "CHANNELS_LIST", channels);
        } catch (Exception e) {
            sendError(session, "Erreur lors de la récupération des canaux : " + e.getMessage());
        }
    }

    /** Equivalent de doPost — broadcast à tous les clients connectés */
    private void handleCreateChannel(JsonNode payload, Session session) {
        try {
            if (payload == null) {
                sendError(session, "Le champ 'payload' est obligatoire.");
                return;
            }

            Channel input = objectMapper.treeToValue(payload, Channel.class);

            if (input.getName() == null || input.getName().isBlank()) {
                sendError(session, "Le champ 'name' est obligatoire.");
                return;
            }

            User creator = userDAO.findById(1); // utilisateur fictif — à remplacer Phase 4
            if (creator == null) {
                sendError(session, "Le créateur par défaut n'existe pas en base.");
                return;
            }

            Channel newChannel = new Channel();
            newChannel.setName(input.getName());
            newChannel.setDescription(input.getDescription());
            newChannel.setIsPublic(input.getIsPublic() != null ? input.getIsPublic() : true);
            newChannel.setUrl(input.getUrl());
            newChannel.setCreationDate(LocalDateTime.now());
            newChannel.setCreator(creator);

            channelDAO.create(newChannel);

            // Broadcast : tous les clients voient le nouveau canal en temps réel
            broadcast("CHANNEL_CREATED", newChannel);

        } catch (Exception e) {
            sendError(session, "Erreur lors de la création du canal : " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Utilitaires d'envoi
    // -------------------------------------------------------

    /** Envoie un message à UN seul client */
    private void sendToSession(Session session, String type, Object data) {
        try {
            String json = objectMapper.writeValueAsString(Map.of("type", type, "data", data));
            session.getAsyncRemote().sendText(json);
        } catch (IOException e) {
            System.err.println("[WS] Impossible d'envoyer à " + session.getId() + " : " + e.getMessage());
        }
    }

    /** Diffuse un message à TOUS les clients connectés */
    private void broadcast(String type, Object data) {
        String json;
        try {
            json = objectMapper.writeValueAsString(Map.of("type", type, "data", data));
        } catch (Exception e) {
            System.err.println("[WS] Sérialisation impossible : " + e.getMessage());
            return;
        }

        for (Session s : sessions) {
            if (s.isOpen()) {
                s.getAsyncRemote().sendText(json);
            }
        }
    }

    /** Envoie une erreur à UN client */
    private void sendError(Session session, String message) {
        try {
            String json = objectMapper.writeValueAsString(Map.of("type", "ERROR", "error", message));
            session.getAsyncRemote().sendText(json);
        } catch (IOException e) {
            System.err.println("[WS] Impossible d'envoyer l'erreur : " + e.getMessage());
        }
    }
}