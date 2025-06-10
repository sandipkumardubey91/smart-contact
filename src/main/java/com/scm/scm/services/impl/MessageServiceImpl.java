package com.scm.scm.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.scm.scm.entities.Message;
import com.scm.scm.repository.MessageRepo;
import com.scm.scm.services.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepo messageRepo;

    public List<Message> getAllMessagesByReceiverId(String receiverId){
        return messageRepo.findByReceiverId(receiverId);
    }

    public List<Message> getConversation(String user1, String user2) {
        Sort sort = Sort.by(Sort.Order.asc("timestamp"));
        return messageRepo.findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(user1, user2, user1, user2, sort);
    }

    public void sendMessage(String senderId, String receiverId, String text) {
        Message msg = new Message();
        String msgId = UUID.randomUUID().toString();
        msg.setId(msgId);
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setMessageText(text);
        // msg.setTimestamp(LocalDateTime.now());
        messageRepo.save(msg);
    }

}
