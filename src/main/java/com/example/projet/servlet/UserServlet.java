package com.example.projet.servlet;

import com.example.projet.dao.UserDAO;
import com.example.projet.model.User;
import com.example.projet.util.JwtUtil;
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
import java.util.Objects;
import java.util.UUID;

@WebServlet("/api/user/@me")
public class UserServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = JwtUtil.extractToken(req);
        UUID authorId = UUID.fromString(Objects.requireNonNull(JwtUtil.validateToken(token)).getSubject());
        User user = UserDAO.getInstance().findById(authorId);

        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            PrintWriter out = resp.getWriter();
            out.print("{\"error\": \"Utilisateur non trouvé.\"}");
            out.flush();
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = resp.getWriter();
        out.print(objectMapper.writeValueAsString(user));
        out.flush();
    }
}
