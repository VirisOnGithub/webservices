package com.example.projet.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "CHANNEL")
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idc")
    private UUID idc;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_public")
    private Boolean isPublic = true;

    @Column(name = "url", length = 255)
    private String url;

    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    // Jointure JPA : Plusieurs canaux peuvent avoir le même utilisateur comme créateur
    @ManyToOne
    @JoinColumn(name = "idu", nullable = false)
    private User creator;

    // Constructeur requis par JPA
    public Channel() {}

    // Getters et Setters
    public UUID getIdc() { return idc; }
    public void setIdc(UUID idc) { this.idc = idc; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }
}