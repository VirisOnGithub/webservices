package com.example.projet.servlet;

import com.example.projet.dao.UserDAO;
import com.example.projet.model.User;
import com.example.projet.utils.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

//        System.out.println("Login: " + username + " / " + password); // Debug

        // Vérifier les credentials (exemple simple)
        User user = UserDAO.getInstance().authenticate(username, password);
        if (user != null) {
            String token = JwtUtil.createToken(user.getIdu(), user.getPseudo());
            out.println("{\"token\": \"" + token + "\"}");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            out.println("{\"error\": \"Identifiants invalides\"}");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        out.flush();
    }
}
