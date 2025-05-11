package org.example.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @Column(name = "create_time")
  private LocalDateTime createTime;

  // 无参构造函数
  public Message() {
  }

  // 全参构造函数
  public Message(Long id, String content, User sender, LocalDateTime createTime) {
    this.id = id;
    this.content = content;
    this.sender = sender;
    this.createTime = createTime;
  }

  @PrePersist
  public void prePersist() {
    createTime = LocalDateTime.now();
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

  public User getSender() {
    return sender;
  }

  public void setSender(User sender) {
    this.sender = sender;
  }

  public LocalDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
  }
}