package com.example.projet.servlet;

import com.example.projet.dao.UserDAO;
import com.example.projet.model.User;
import com.example.projet.utils.BCryptUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pseudo = req.getParameter("pseudo");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String avatar = ""; // On s'en occupe plus tard

        UserDAO userDAO = UserDAO.getInstance();
        User userToCreate = new User();
        userToCreate.setPseudo(pseudo);
        userToCreate.setEmail(email);
        userToCreate.setPassword(BCryptUtil.hashPassword(password)); // Hash du mot de passe
        userToCreate.setAvatar(avatar);
        userToCreate.setInscriptionDate(LocalDateTime.now());

        userDAO.create(userToCreate);
    }
}
