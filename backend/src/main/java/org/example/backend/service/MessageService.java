package org.example.backend.service;

import org.example.backend.entity.Message;
import org.example.backend.entity.User;
import org.example.backend.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MessageService {
  private final MessageRepository messageRepository;

  public MessageService(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  public Message saveMessage(String content, User sender) {
    Message message = new Message();
    message.setContent(content);
    message.setSender(sender);

    return messageRepository.save(message);
  }

  public List<Message> getRecentMessages() {
    List<Message> messages = messageRepository.findTop50ByOrderByCreateTimeDesc();
    Collections.reverse(messages); // 反转列表，使最早的消息在前面
    return messages;
  }
}