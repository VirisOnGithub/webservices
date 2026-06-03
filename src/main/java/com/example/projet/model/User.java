package com.example.projet.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idu")
    private Integer idu;

    @Column(name = "pseudo", nullable = false, length = 50)
    private String pseudo;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "inscription_date", updatable = false)
    private LocalDateTime inscriptionDate;

    // Constructeur vide requis par JPA
    public User() {}

    // Getters et Setters
    public Integer getIdu() { return idu; }
    public void setIdu(Integer idu) { this.idu = idu; }

    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public LocalDateTime getInscriptionDate() { return inscriptionDate; }
    public void setInscriptionDate(LocalDateTime inscriptionDate) { this.inscriptionDate = inscriptionDate; }
}