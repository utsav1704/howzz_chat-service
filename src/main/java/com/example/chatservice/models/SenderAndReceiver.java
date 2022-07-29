package com.example.chatservice.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@ToString
@Getter
@Setter
public class SenderAndReceiver {
    private String senderId;
    private String receiverId;
}
