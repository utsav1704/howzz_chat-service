package com.example.chatservice.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.example.chatservice.exception.ResourceNotFoundException;
import com.example.chatservice.models.ChatMessage;
import com.example.chatservice.models.MessageStatus;
import com.example.chatservice.repository.ChatMessageRepository;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private MongoOperations mongoOperations;

    public ChatMessage saveMessage(ChatMessage chatMessage){
        chatMessage.setStatus(MessageStatus.RECEIVED);
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    public ChatMessage saveGlobalMessage(ChatMessage chatMessage){
        var chatId = chatMessage.getReceiverName().toLowerCase()+"_"+chatMessage.getReceiverId();
        chatMessage.setChatId(chatId);
        
        chatRoomService.createChatRoomForGlobal(chatMessage.getSenderId(), chatMessage.getReceiverId(), chatId);

        chatMessage.setStatus(MessageStatus.RECEIVED);        
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    public long countNewMessages(String senderId , String receiverId){
        return chatMessageRepository.countBySenderIdAndReceiverIdAndStatus(senderId, receiverId, MessageStatus.RECEIVED);
    }

    public List<ChatMessage> findChatMessages(String senderId , String receiverId){
        var chatId = chatRoomService.getChatId(senderId, receiverId, false);

        var chatMessages = chatId.map(cId -> chatMessageRepository.findByChatId(cId)).orElse(new ArrayList<>());

        if(chatMessages.size() > 0){
            updateStatues(senderId , receiverId , MessageStatus.DELIVERED);
        }

        return chatMessages;
    }

    public List<ChatMessage> getGlobalMessages(String receiverId){
        return chatMessageRepository.findByReceiverId(receiverId);
    }

    public ChatMessage findById(String id){
        return chatMessageRepository
                    .findById(id)
                    .map(chatMessage -> {
                        chatMessage.setStatus(MessageStatus.DELIVERED);
                        return chatMessageRepository.save(chatMessage);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("can not find any message for "+id));             
    }

    public void updateStatues(String senderId , String receiverId , MessageStatus status){
        Query query = new Query(
            Criteria
                .where("senderId").is(senderId)
                .and("receiverId").is(receiverId)
        );
        Update update = Update.update("status", status);
        mongoOperations.updateMulti(query, update, ChatMessage.class);
    }
 
    public List<String> getUsers(String senderId){

        List<String> bySender = chatMessageRepository.findBySenderIdIncludeReceiverId(senderId , "1704");
        // System.out.println(bySender.toString());

        List<ChatMessage> findBySenderIdIncludeReceiverIdReturningChatMEssage = chatMessageRepository.findBySenderIdIncludeReceiverIdReturningChatMEssage(senderId, "1704");
        Set<String> receiverIds = new HashSet<>();

        for (ChatMessage chatMessage : findBySenderIdIncludeReceiverIdReturningChatMEssage) {
            receiverIds.add(chatMessage.getReceiverId());
        }

        // System.out.println(receiverIds);

        List<String> byReceiver = chatMessageRepository.findByReceiverIdIncludeSenderId(senderId , receiverIds);
        // System.out.println(byReceiver.toString());

        Set<String> ids = new HashSet<>();
        ids.addAll(bySender);
        ids.addAll(byReceiver);

        return new ArrayList<>(ids);
    }
}
