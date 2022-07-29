package com.example.chatservice.models;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatNotification {

    private String id;
    private String senderId;
    private String senderName;
    
}
