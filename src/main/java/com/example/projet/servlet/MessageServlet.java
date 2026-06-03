package com.example.projet.servlet;

import com.example.projet.dao.ChannelDAO;
import com.example.projet.dao.MessageDAO;
import com.example.projet.dao.UserDAO;
import com.example.projet.model.Channel;
import com.example.projet.model.Message;
import com.example.projet.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/api/messages")
public class MessageServlet extends HttpServlet {

    private final MessageDAO messageDAO = new MessageDAO();
    private final ChannelDAO channelDAO = new ChannelDAO();
    private final UserDAO userDAO = new UserDAO();

    // Configuration de l'ObjectMapper avec le module de gestion des dates
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // 1. GET /api/messages?idc=XXX -> Récupérer l'historique des messages d'un canal
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Integer idc;
        try {
            idc = Integer.parseInt(req.getParameter("idc"));
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "L'ID du canal ('idc') doit être un entier valide.");
            return;
        }

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

    // 2. POST /api/messages -> Publier un nouveau message dans un canal
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Lecture du corps JSON de la requête
            // Exemple attendu : { "content": "Mon super message !", "channel": { "idc": "c1" } }
            Message messageInput = objectMapper.readValue(req.getInputStream(), Message.class);

            if (messageInput.getContent() == null || messageInput.getContent().trim().isEmpty()) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Le contenu du message ('content') ne peut pas être vide.");
                return;
            }

            if (messageInput.getChannel() == null || messageInput.getChannel().getIdc() == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "L'ID du canal cible ('channel.idc') est obligatoire.");
                return;
            }

            // Vérification de l'existence du canal cible
            Channel targetChannel = channelDAO.findById(messageInput.getChannel().getIdc());
            if (targetChannel == null) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Le canal spécifié n'existe pas.");
                return;
            }

            // Comme pour ChannelServlet, on simule l'utilisateur connecté "u1" (Alice)
            // (Sera géré dynamiquement à la Phase 4)
            User author = userDAO.findById(1);
            if (author == null) {
                sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "L'auteur par défaut n'existe pas en base.");
                return;
            }

            // Construction de l'entité Message finale
            Message newMessage = new Message();
                newMessage.setContent(messageInput.getContent());
            newMessage.setSendDate(LocalDateTime.now());
            newMessage.setEdited(false);
            newMessage.setAuthor(author);
            newMessage.setChannel(targetChannel);

            // Optionnel : Gestion d'un éventuel message parent s'il s'agit d'un fil / thread de réponse
            if (messageInput.getParentMessage() != null && messageInput.getParentMessage().getIdm() != null) {
                Message parent = messageDAO.findById(messageInput.getParentMessage().getIdm());
                if (parent != null) {
                    newMessage.setParentMessage(parent);
                }
            }

            // Sauvegarde définitive en base via JPA
            messageDAO.create(newMessage);

            // Réponse 201 Created
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