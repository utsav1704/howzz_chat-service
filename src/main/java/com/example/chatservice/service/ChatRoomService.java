package com.example.chatservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chatservice.models.ChatRoom;
import com.example.chatservice.repository.ChatRoomRepository;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatId(String senderId , String receiverId , boolean createIfNotExist){
        return chatRoomRepository
                .findBySenderIdAndReceiverId(senderId, receiverId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if(!createIfNotExist){
                        return Optional.empty();
                    }
                    var chatId = String.format("%s_%s", senderId , receiverId);

                    ChatRoom senderReceiver = ChatRoom
                                                .builder()
                                                .chatId(chatId)
                                                .senderId(senderId)
                                                .receiverId(receiverId)
                                                .build();
                    ChatRoom receiverSender = ChatRoom
                                                .builder()
                                                .chatId(chatId)
                                                .senderId(receiverId)
                                                .receiverId(senderId)
                                                .build();
                    chatRoomRepository.save(senderReceiver);
                    chatRoomRepository.save(receiverSender);

                    return Optional.of(chatId);

                });
    }

    public void createChatRoomForGlobal(String senderId , String receiverId , String chatId){
        List<ChatRoom> findByChatId = chatRoomRepository.findByChatId(chatId);
        ChatRoom global = findByChatId.get(0);
        ChatRoom chatRoom = ChatRoom.builder()
        .id(global.getId())
                                .chatId(chatId)
                                .senderId(senderId)
                                .receiverId(receiverId)
                                .build();
        chatRoomRepository.save(chatRoom);
        // return chatId;
    }
    
}
