package com.example.projet.tests;

import com.example.projet.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectMapperTest {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Message messageInput = objectMapper.readValue("{\"content\": \"Hello !\", \"author\": \"1\"}", Message.class);

        System.out.println("Message content: " + messageInput.getContent());
        System.out.println("Message author ID: " + (messageInput.getAuthor() != null ? messageInput.getAuthor().getIdu() : "null"));
    }
}
