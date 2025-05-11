package org.example.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(name = "create_time")
  private LocalDateTime createTime;

  // 无参构造函数
  public User() {
  }

  // 全参构造函数
  public User(Long id, String username, String password, LocalDateTime createTime) {
    this.id = id;
    this.username = username;
    this.password = password;
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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public LocalDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
  }
}