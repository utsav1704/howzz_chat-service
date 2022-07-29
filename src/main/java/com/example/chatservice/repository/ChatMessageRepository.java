package com.example.chatservice.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.chatservice.models.ChatMessage;
import com.example.chatservice.models.MessageStatus;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage,String> {
    
    long countBySenderIdAndReceiverIdAndStatus(String senderId , String receiverId , MessageStatus status);

    List<ChatMessage> findByChatId(String chatId);

    // try with returning ChatMessage object instead of String
    @Query(value = "{'senderId':?0 , 'receiverId':{$ne : ?1}}" , fields = "{'receiverId':1 , '_id':0 , 'senderId':1}")
    List<String> findBySenderIdIncludeReceiverId(String senderId , String receiverId);

    @Query(value = "{'senderId':?0 , 'receiverId':{$ne : ?1}}" , fields = "{'receiverId':1 , '_id':0 , 'senderId':1}")
    List<ChatMessage> findBySenderIdIncludeReceiverIdReturningChatMEssage(String senderId , String receiverId);
    
    @Query(value = "{'receiverId':?0 , 'senderId' : {$nin : ?1}}" , fields = "{'senderId':1 , '_id':0 , 'receiverId':1}")
    List<String> findByReceiverIdIncludeSenderId(String senderId , Set<String> receiverIds);

    List<ChatMessage> findByReceiverId(String receiverId);

}
