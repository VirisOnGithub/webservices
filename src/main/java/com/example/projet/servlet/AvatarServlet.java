package com.example.projet.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/api/avatars")
public class AvatarServlet extends HttpServlet {

    // Chemin absolu configuré dans le script Bash
    private static final String AVATARS_DIR = "/tmp/journal_de_bord/avatars";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // On récupère le paramètre d'URL (ex: ?filename=alice.jpg)
        String filename = req.getParameter("filename");

        if (filename == null || filename.trim().isEmpty() || filename.contains("..")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nom de fichier invalide.");
            return;
        }

        // Liaison avec le fichier physique
        File file = new File(AVATARS_DIR, filename);

        // Si l'avatar demandé n'existe pas, on peut renvoyer une image par défaut ou une 404
        if (!file.exists() || file.isDirectory()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Avatar introuvable.");
            return;
        }

        // Configuration des en-têtes HTTP pour une image JPEG
        resp.setContentType("image/jpeg");
        resp.setContentLength((int) file.length());

        // Lecture du fichier et écriture directe dans le flux de réponse (Stream)
        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = resp.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }
    }
}