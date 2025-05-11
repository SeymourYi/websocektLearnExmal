package org.example.backend.dto;

import java.time.LocalDateTime;

public class ChatMessage {
  private Long id;
  private String content;
  private String senderUsername;
  private Long senderId;
  private LocalDateTime timestamp;
  private MessageType type;

  // 无参构造函数
  public ChatMessage() {
  }

  // 全参构造函数
  public ChatMessage(Long id, String content, String senderUsername, Long senderId, LocalDateTime timestamp,
      MessageType type) {
    this.id = id;
    this.content = content;
    this.senderUsername = senderUsername;
    this.senderId = senderId;
    this.timestamp = timestamp;
    this.type = type;
  }

  public enum MessageType {
    CHAT,
    JOIN,
    LEAVE
  }

  // Getter and Setter methods
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getSenderUsername() {
    return senderUsername;
  }

  public void setSenderUsername(String senderUsername) {
    this.senderUsername = senderUsername;
  }

  public Long getSenderId() {
    return senderId;
  }

  public void setSenderId(Long senderId) {
    this.senderId = senderId;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public MessageType getType() {
    return type;
  }

  public void setType(MessageType type) {
    this.type = type;
  }
}