package com.example.projet.servlet;

import com.example.projet.dao.ChannelDAO;
import com.example.projet.dao.UserDAO;
import com.example.projet.model.Channel;
import com.example.projet.model.User;
import com.fasterxml.jackson.databind.ObjectMapper; // <-- Import Jackson

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/api/channels")
public class ChannelServlet extends HttpServlet {

    private final ChannelDAO channelDAO = ChannelDAO.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // 1. GET /api/channels -> Récupérer tous les canaux
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            List<Channel> channels = channelDAO.findAll();

            // Jackson convertit la liste en chaîne JSON
            String jsonResult = objectMapper.writeValueAsString(channels);

            resp.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = resp.getWriter();
            out.print(jsonResult);
            out.flush();
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des canaux." + e.getMessage());
        }
    }

    // 2. POST /api/channels -> Créer un nouveau canal
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Jackson lit le flux d'entrée de la requête et crée l'objet Channel
            Channel channelInput = objectMapper.readValue(req.getInputStream(), Channel.class);

            if (channelInput.getName() == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Le champ 'name' est obligatoire.");
                return;
            }

            User creator = userDAO.findAll().get(0); // Utilisateur fictif en attendant la Phase 4
            if (creator == null) {
                sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Le créateur par défaut n'existe pas en base.");
                return;
            }

            Channel newChannel = new Channel();
            newChannel.setName(channelInput.getName());
            newChannel.setDescription(channelInput.getDescription());
            newChannel.setIsPublic(channelInput.getIsPublic() != null ? channelInput.getIsPublic() : true);
            newChannel.setUrl(channelInput.getUrl());
            newChannel.setCreationDate(LocalDateTime.now());
            newChannel.setCreator(creator);

            channelDAO.create(newChannel);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter out = resp.getWriter();
            out.print(objectMapper.writeValueAsString(newChannel));
            out.flush();

        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la création du canal : " + e.getMessage());
        }
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        PrintWriter out = resp.getWriter();
        out.print("{\"error\": \"" + message + "\"}");
        out.flush();
    }
}