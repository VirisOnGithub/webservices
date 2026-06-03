package com.example.projet.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ATTACHMENT")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ida")
    private Integer ida;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "mime", length = 100)
    private String mime;

    @Column(name = "url", length = 255)
    private String url;

    public Attachment() {}

    // Getters et Setters
    public Integer getIda() { return ida; }
    public void setIda(Integer ida) { this.ida = ida; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMime() { return mime; }
    public void setMime(String mime) { this.mime = mime; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}