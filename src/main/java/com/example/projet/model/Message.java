package com.example.projet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MESSAGE")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idm")
    private Integer idm;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "send_date")
    private LocalDateTime sendDate;

    @Column(name = "edited")
    private Boolean edited;

    @Column(name = "edit_date")
    private LocalDateTime editDate;

    // Auteur du message (idu)
    @ManyToOne
    @JoinColumn(name = "idu", nullable = false)
    private User author;

    // Canal associé (idc)
    @ManyToOne
    @JoinColumn(name = "idc", nullable = false)
    private Channel channel;

    // Message parent en cas de réponse / fil de discussion (idm_2)
    @ManyToOne
    @JoinColumn(name = "idm_2")
    private Message parentMessage;

    // Jointure ManyToMany pour la table "react" (les utilisateurs qui ont réagi à ce message)
    @ManyToMany
    @JoinTable(
            name = "react",
            joinColumns = @JoinColumn(name = "idm"),
            inverseJoinColumns = @JoinColumn(name = "idu")
    )
    @JsonIgnore
    private List<User> reactors = new ArrayList<>();

    public Message() {}

    // Getters et Setters
    public Integer getIdm() { return idm; }
    public void setIdm(Integer idm) { this.idm = idm; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getSendDate() { return sendDate; }
    public void setSendDate(LocalDateTime sendDate) { this.sendDate = sendDate; }

    public Boolean getEdited() { return edited; }
    public void setEdited(Boolean edited) { this.edited = edited; }

    public LocalDateTime getEditDate() { return editDate; }
    public void setEditDate(LocalDateTime editDate) { this.editDate = editDate; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public Channel getChannel() { return channel; }
    public void setChannel(Channel channel) { this.channel = channel; }

    public Message getParentMessage() { return parentMessage; }
    public void setParentMessage(Message parentMessage) { this.parentMessage = parentMessage; }

    @JsonIgnore
    public List<User> getReactors() { return reactors; }
    public void setReactors(List<User> reactors) { this.reactors = reactors; }
}