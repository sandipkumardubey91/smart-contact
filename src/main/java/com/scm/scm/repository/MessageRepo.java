package com.scm.scm.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scm.scm.entities.Message;

@Repository
public interface MessageRepo extends JpaRepository<Message, String> {

    List<Message> findByReceiverId(String receiverId);

    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
            String senderId1, String receiverId1, String senderId2, String receiverId2, Sort sort);

}
