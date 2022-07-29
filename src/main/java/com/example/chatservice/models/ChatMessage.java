package com.example.chatservice.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatMessage {

    @Id
    private String id;
    private String chatId;
    private String senderId;
    private String receiverId;
    private String senderName;
    private String receiverName;
    private String content;
    private Date timeStamp;
    private MessageStatus status;
    
}
