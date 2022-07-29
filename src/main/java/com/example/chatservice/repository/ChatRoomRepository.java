package com.example.chatservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.chatservice.models.ChatRoom;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom , String> {

    Optional<ChatRoom> findBySenderIdAndReceiverId(String senderId , String receiverId);

    List<ChatRoom> findByChatId(String chatId);
    
}
