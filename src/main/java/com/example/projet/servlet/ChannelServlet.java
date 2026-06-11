package com.example.projet.servlet;

import com.example.projet.dao.ChannelDAO;
import com.example.projet.model.Channel;
import com.example.projet.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper; // <-- Import Jackson

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.example.projet.util.JwtUtil.extractToken;

@WebServlet("/api/channels")
public class ChannelServlet extends HttpServlet {

    private final ChannelDAO channelDAO = ChannelDAO.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // 1. GET /api/channels -> Récupérer tous les canaux
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        System.out.println(
                "[Servlet] GET /api/channels - Headers: " + req.getHeaderNames().asIterator().toString()
        );

        try {
            String token = extractToken(req);


            UUID userId = UUID.fromString(
                    Objects.requireNonNull(JwtUtil.validateToken(token)).getSubject()
            );

            System.out.println(
                    "[Servlet] User ID extrait du token: " + userId
            );

            List<Channel> channels = channelDAO.findAuthorizedChannels(userId);

            System.out.println(
                    "[Servlet] Canaux récupérés pour l'utilisateur " + userId + ": " + channels.size()
            );

            // Jackson convertit la liste en chaîne JSON
            String jsonResult = objectMapper.writeValueAsString(channels);

            resp.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = resp.getWriter();
            out.print(jsonResult);
            out.flush();
        } catch (Exception e) {
            System.out.println(
                    "[Servlet] Erreur lors de la récupération des canaux: " + e.getMessage()
            );
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des canaux." + e.getMessage());
        }
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        PrintWriter out = resp.getWriter();
        out.print("{\"error\": \"" + message + "\"}");
        out.flush();
    }
}