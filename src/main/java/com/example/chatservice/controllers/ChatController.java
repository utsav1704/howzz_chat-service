package com.example.chatservice.controllers;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatservice.models.ChatMessage;
import com.example.chatservice.models.ChatNotification;
import com.example.chatservice.service.ChatMessageService;
import com.example.chatservice.service.ChatRoomService;

@RestController
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatMessageService chatMessageService;

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public ChatMessage receiveGlobalMessage(@Payload ChatMessage chatMessage){
        // System.out.println(chatMessage.getSenderName());
        return chatMessageService.saveGlobalMessage(chatMessage);
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage){
        // System.out.println(chatMessage);
        var chatId = chatRoomService.getChatId(chatMessage.getSenderId(), chatMessage.getReceiverId(), true);
        chatMessage.setChatId(chatId.get());

        ChatMessage saved = chatMessageService.saveMessage(chatMessage);

        simpMessagingTemplate.convertAndSendToUser(chatMessage.getReceiverId(), "/queue/messages", new ChatNotification(saved.getId(),saved.getSenderId(),saved.getSenderName()));
    }

    @GetMapping("/getUsers/{senderId}")
    public ResponseEntity<?> getUsers(@PathVariable String senderId){
        List<String> users = chatMessageService.getUsers(senderId);
        Collections.sort(users);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/messages/{senderId}/{receiverId}/count")
    public ResponseEntity<Long> countNewMessages(@PathVariable String senderId , @PathVariable String receiverId){
        return ResponseEntity.ok(chatMessageService.countNewMessages(senderId, receiverId));
    }

    @GetMapping("/messages/{senderId}/{receiverId}")
    public ResponseEntity<?> findChatMessages(@PathVariable String senderId , @PathVariable String receiverId){
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, receiverId));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<?> findMessage(@PathVariable String id){
        return ResponseEntity.ok(chatMessageService.findById(id));
    }

    @GetMapping("/messages/global/{receiverId}")
    public ResponseEntity<?> loadGlobalMessages(@PathVariable String receiverId){
        return ResponseEntity
                .ok(chatMessageService.getGlobalMessages(receiverId));
    }
    
}
