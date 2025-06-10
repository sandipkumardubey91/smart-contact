package com.scm.scm.services;

import java.util.List;

import com.scm.scm.entities.Message;

public interface MessageService {

    public List<Message> getAllMessagesByReceiverId(String receiverId);

    public List<Message> getConversation(String user1, String user2);

    public void sendMessage(String senderId, String receiverId, String text);

}
