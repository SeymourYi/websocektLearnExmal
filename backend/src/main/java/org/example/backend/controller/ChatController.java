package org.example.backend.controller;

import org.example.backend.dto.ChatMessage;
import org.example.backend.entity.Message;
import org.example.backend.entity.User;
import org.example.backend.service.MessageService;
import org.example.backend.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ChatController {

  private final UserService userService;
  private final MessageService messageService;

  public ChatController(UserService userService, MessageService messageService) {
    this.userService = userService;
    this.messageService = messageService;
  }

  @MessageMapping("/chat.sendMessage")
  @SendTo("/topic/public")
  public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
    Optional<User> userOpt = userService.getUserById(chatMessage.getSenderId());
    if (userOpt.isPresent()) {
      User user = userOpt.get();
      Message savedMessage = messageService.saveMessage(chatMessage.getContent(), user);

      chatMessage.setId(savedMessage.getId());
      chatMessage.setSenderUsername(user.getUsername());
      chatMessage.setTimestamp(LocalDateTime.now());
    }
    return chatMessage;
  }

  @MessageMapping("/chat.addUser")
  @SendTo("/topic/public")
  public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
    Optional<User> userOpt = userService.getUserByUsername(chatMessage.getSenderUsername());
    User user;

    if (userOpt.isEmpty()) {
      // 创建新用户
      user = userService.createUser(chatMessage.getSenderUsername(), "defaultPassword");
    } else {
      user = userOpt.get();
    }

    // 保存用户信息到WebSocket会话
    headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderUsername());
    headerAccessor.getSessionAttributes().put("userId", user.getId());

    chatMessage.setSenderId(user.getId());
    chatMessage.setTimestamp(LocalDateTime.now());

    return chatMessage;
  }

  @GetMapping("/messages/recent")
  @ResponseBody
  public List<ChatMessage> getRecentMessages() {
    List<Message> messages = messageService.getRecentMessages();

    return messages.stream()
        .map(msg -> {
          ChatMessage chatMessage = new ChatMessage();
          chatMessage.setId(msg.getId());
          chatMessage.setContent(msg.getContent());
          chatMessage.setSenderId(msg.getSender().getId());
          chatMessage.setSenderUsername(msg.getSender().getUsername());
          chatMessage.setTimestamp(msg.getCreateTime());
          chatMessage.setType(ChatMessage.MessageType.CHAT);
          return chatMessage;
        })
        .collect(Collectors.toList());
  }
}