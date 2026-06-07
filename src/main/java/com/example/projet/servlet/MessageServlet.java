package com.example.projet.servlet;

import com.example.projet.dao.ChannelDAO;
import com.example.projet.dao.MessageDAO;
import com.example.projet.dao.UserDAO;
import com.example.projet.model.Channel;
import com.example.projet.model.Message;
import com.example.projet.model.User;
import com.example.projet.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@WebServlet("/api/channels/*")
public class MessageServlet extends HttpServlet {

    private final MessageDAO messageDAO = MessageDAO.getInstance();
    private final ChannelDAO channelDAO = ChannelDAO.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();

    // Configuration de l'ObjectMapper avec le module de gestion des dates
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // 1. GET /api/channels/{idc}/messages : Récupérer les messages d'un canal
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Integer idc;

        String pathInfo = req.getPathInfo();

        idc = getChannelId(resp, pathInfo);
        if (idc == null) return;

        try {
            // Vérifier si le canal existe
            Channel channel = channelDAO.findById(idc);
            if (channel == null) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Le canal spécifié n'existe pas.");
                return;
            }

            // Récupération des messages triés par date via le DAO
            List<Message> messages = messageDAO.findByChannel(idc);

            resp.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = resp.getWriter();
            out.print(objectMapper.writeValueAsString(messages));
            out.flush();

        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des messages : " + e.getMessage());
        }
    }

    private Integer getChannelId(HttpServletResponse resp, String pathInfo) throws IOException {
        int idc;
        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "L'ID du canal est requis dans l'URL (ex: /api/channels/c1/messages).");
            return null;
        }

        // exemple : "/1/messages" => ["", "1", "messages"]
        String[] segments = pathInfo.split("/");

        if (segments.length == 3 && "messages".equals(segments[2])) {
            try {
                idc = Integer.parseInt(segments[1]);
            } catch (NumberFormatException e) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "L'ID du canal doit être un entier valide.");
                return null;
            }
        } else {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "URL invalide. Utilisez le format : /api/channels/{idc}/messages");
            return null;
        }
        return idc;
    }

    // 2. POST /api/channels/{idc}/messages : Publier un message dans un canal
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Integer idc = getChannelId(resp, req.getPathInfo());

        try {
            // Lecture du corps JSON de la requête
            // Exemple attendu : { "content": "Mon super message !", "channel": { "idc": "c1" } }
            Message messageInput = objectMapper.readValue(req.getInputStream(), Message.class);

            if (messageInput.getContent() == null || messageInput.getContent().trim().isEmpty()) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Le contenu du message ('content') ne peut pas être vide.");
                return;
            }

            String token = JwtUtil.extractToken(req);
            Integer authorId = Integer.valueOf(Objects.requireNonNull(JwtUtil.validateToken(token)).getSubject());
            User author = userDAO.findById(authorId);
            if (author == null) {
                sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "L'auteur par défaut n'existe pas en base.");
                return;
            }

            Channel targetChannel = channelDAO.findById(idc);
            if (targetChannel == null) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Le canal spécifié n'existe pas.");
                return;
            }

            // Construction de l'entité Message finale
            Message newMessage = new Message();
                newMessage.setContent(messageInput.getContent());
            newMessage.setSendDate(LocalDateTime.now());
            newMessage.setEdited(false);
            newMessage.setAuthor(author);
            newMessage.setChannel(targetChannel);

            if (messageInput.getParentMessage() != null && messageInput.getParentMessage().getIdm() != null) {
                Message parent = messageDAO.findById(messageInput.getParentMessage().getIdm());
                if (parent != null) {
                    newMessage.setParentMessage(parent);
                }
            }

            // Sauvegarde définitive en base via JPA
            messageDAO.create(newMessage);

            // Réponse "201 Created"
            resp.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter out = resp.getWriter();
            out.print(objectMapper.writeValueAsString(newMessage));
            out.flush();

        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la publication du message : " + e.getMessage());
        }
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        PrintWriter out = resp.getWriter();
        out.print("{\"error\": \"" + message + "\"}");
        out.flush();
    }
}