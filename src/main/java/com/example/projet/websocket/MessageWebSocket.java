package com.example.projet.websocket;

import com.example.projet.dao.ChannelDAO;
import com.example.projet.dao.MessageDAO;
import com.example.projet.dao.UserDAO;
import com.example.projet.model.Channel;
import com.example.projet.model.Message;
import com.example.projet.model.User;
import com.example.projet.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.projet.util.JwtUtil.extractToken;

@ServerEndpoint("/ws/channels/{idc}/messages")
public class MessageWebSocket {

    // Sessions isolées par canal — clé = idc du channel
    private static final Map<UUID, Set<Session>> channelSessions = new ConcurrentHashMap<>();

    private final MessageDAO messageDAO = MessageDAO.getInstance();
    private final ChannelDAO channelDAO = ChannelDAO.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // -------------------------------------------------------
    // Cycle de vie
    // -------------------------------------------------------

    @OnOpen
    public void onOpen(Session session, @PathParam("idc") String idcRaw) {
        UUID idc;
        try {
            idc = UUID.fromString(idcRaw);
        } catch (IllegalArgumentException e) {
            return;
        }
        channelSessions.computeIfAbsent(idc, k -> ConcurrentHashMap.newKeySet()).add(session);

        // envoie les messages dès la connexion au socket
        handleGetMessages(session, idc);
    }

    @OnClose
    public void onClose(Session session, @PathParam("idc") String idcRaw) {
        UUID idc;
        try {
            idc = UUID.fromString(idcRaw);
        } catch (IllegalArgumentException e) {
            return;
        }
        Set<Session> sessions = channelSessions.get(idc);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) channelSessions.remove(idc); // nettoyage
        }
    }

    @OnError
    public void onError(Session session, Throwable t, @PathParam("idc") String idcRaw) {
        System.err.println("[WS] Erreur sur " + session.getId() + " : " + t.getMessage());
        try {
            UUID idc = UUID.fromString(idcRaw);
            channelSessions.getOrDefault(idc, Set.of()).remove(session);
        } catch (IllegalArgumentException ignored) {
        }
    }

    // -------------------------------------------------------
    // Réception des messages
    // -------------------------------------------------------

    @OnMessage
    public void onMessage(String raw, Session session, @PathParam("idc") String idcRaw) {
        UUID idc;
        try {
            idc = UUID.fromString(idcRaw);
        } catch (IllegalArgumentException e) {
            return;
        }
        System.out.println("[WS] Reçu de " + session.getId() + " sur canal " + idc + " : " + raw);
        try {
            JsonNode node = objectMapper.readTree(raw);
            if (!node.has("action")) {
                sendError(session, "Le champ 'action' est obligatoire.");
                return;
            }
            switch (node.get("action").asText()) {
                case "SEND_MESSAGE" -> handleSendMessage(node.get("payload"), session, idc);
                case "EDIT_MESSAGE" -> handleEditMessage(node.get("payload"), session, idc);
                default             -> sendError(session, "Action inconnue : " + node.get("action").asText());
            }
        } catch (Exception e) {
            sendError(session, "Message invalide : " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Handlers
    // -------------------------------------------------------

    /** Envoi de la liste à la connexion — équivalent du doGet */
    private void handleGetMessages(Session session, UUID idc) {
        try {
            Channel channel = channelDAO.findById(idc);
            if (channel == null) {
                sendError(session, "Le canal spécifié n'existe pas.");
                return;
            }
            List<Message> messages = messageDAO.findByChannel(idc);
            sendToSession(session, "MESSAGES_LIST", messages);
        } catch (Exception e) {
            sendError(session, "Erreur lors de la récupération des messages : " + e.getMessage());
        }
    }

    /** Envoi d'un message + broadcast — équivalent du doPost */
    private void handleSendMessage(JsonNode payload, Session session, UUID idc) {
        try {
            if (payload == null) {
                sendError(session, "Le champ 'payload' est obligatoire.");
                return;
            }

            String token = extractToken(session);
            if (token == null) {
                sendError(session, "Token manquant ou invalide.");
                return;
            }

            UUID authorId = UUID.fromString(
                    Objects.requireNonNull(JwtUtil.validateToken(token)).getSubject()
            );
            User author = userDAO.findById(authorId);
            if (author == null) {
                sendError(session, "Auteur introuvable.");
                return;
            }

            Channel channel = channelDAO.findById(idc);
            if (channel == null) {
                sendError(session, "Le canal spécifié n'existe pas.");
                return;
            }

            Message input = objectMapper.treeToValue(payload, Message.class);
            if (input.getContent() == null || input.getContent().trim().isEmpty()) {
                sendError(session, "Le contenu du message ne peut pas être vide.");
                return;
            }

            Message newMessage = new Message();
            newMessage.setContent(input.getContent());
            newMessage.setSendDate(LocalDateTime.now());
            newMessage.setEdited(false);
            newMessage.setAuthor(author);
            newMessage.setChannel(channel);
            System.out.println("[WS] Nouveau message de " + author.getPseudo() + " dans le canal " + channel.getName());

            if (input.getParentMessage() != null && input.getParentMessage().getIdm() != null) {
                Message parent = messageDAO.findById(input.getParentMessage().getIdm());
                if (parent != null) newMessage.setParentMessage(parent);
            }

            messageDAO.create(newMessage);

            // Broadcast uniquement aux clients du même canal
            broadcast(idc, "MESSAGE_CREATED", newMessage);

        } catch (Exception e) {
            sendError(session, "Erreur lors de l'envoi du message : " + e.getMessage());
        }
    }

    private void handleEditMessage(JsonNode payload, Session session, UUID idc) {
        System.out.println("[WS] Modification de message demandée par " + session.getId() + " : " + payload);
        if (payload == null || !payload.has("idm") || !payload.has("content")) {
            sendError(session, "Le champ 'payload' doit contenir 'idm' et 'content'.");
            return;
        }

        try {
            String token = extractToken(session);
            if (token == null) {
                sendError(session, "Token manquant ou invalide.");
                return;
            }

//            Integer userId = Integer.valueOf(
//                    Objects.requireNonNull(JwtUtil.validateToken(token)).getSubject()
//            );

            UUID userId = UUID.fromString(
                    Objects.requireNonNull(JwtUtil.validateToken(token)).getSubject()
            );
            User user = userDAO.findById(userId);
            if (user == null) {
                sendError(session, "Utilisateur introuvable.");
                return;
            }

            int idm = payload.get("idm").asInt();
            String newContent = payload.get("content").asText();

            Message message = messageDAO.findById(idm);
            if (message == null) {
                sendError(session, "Message introuvable.");
                return;
            }
            if (!Objects.equals(message.getAuthor().getIdu(), userId)) {
                sendError(session, "Vous n'êtes pas l'auteur de ce message.");
                return;
            }
            if (newContent.trim().isEmpty()) {
                sendError(session, "Le contenu du message ne peut pas être vide.");
                return;
            }

            message.setContent(newContent);
            message.setEdited(true);
            message.setEditDate(LocalDateTime.now());
            messageDAO.update(message);

            // Broadcast uniquement aux clients du même canal
            broadcast(idc, "MESSAGE_UPDATED", message);

        } catch (Exception e) {
            sendError(session, "Erreur lors de la modification du message : " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Utilitaires
    // -------------------------------------------------------


    private void sendToSession(Session session, String type, Object data) {
        try {
            session.getAsyncRemote().sendText(
                    objectMapper.writeValueAsString(Map.of("type", type, "data", data))
            );
        } catch (IOException e) {
            System.err.println("[WS] Envoi raté vers " + session.getId());
        }
    }

    private void broadcast(UUID idc, String type, Object data) {
        String json;
        try {
            json = objectMapper.writeValueAsString(Map.of("type", type, "data", data));
        } catch (Exception e) {
            System.out.println("[WS] Erreur lors de la sérialisation pour le broadcast : " + e.getMessage());
            return;
        }
        for (Session s : channelSessions.getOrDefault(idc, Set.of())) {
            System.out.println("[WS] Broadcast vers " + s.getId() + " : " + type);
            if (s.isOpen()) {
                s.getAsyncRemote().sendText(json);
            }
        }
    }

    private void sendError(Session session, String message) {
        try {
            session.getAsyncRemote().sendText(
                    objectMapper.writeValueAsString(Map.of("type", "ERROR", "error", message))
            );
        } catch (IOException e) {
            System.err.println("[WS] Impossible d'envoyer l'erreur.");
        }
    }
}