import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import './App.css';

const SOCKET_URL = 'http://localhost:8080/ws';

function App() {
  const [connected, setConnected] = useState(false);
  const [username, setUsername] = useState('');
  const [message, setMessage] = useState('');
  const [messages, setMessages] = useState([]);
  const [userId, setUserId] = useState(null);
  const [stompClient, setStompClient] = useState(null);
  const messagesEndRef = useRef(null);

  // 自动滚动到底部
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // 获取最近消息
  const fetchRecentMessages = async () => {
    try {
      const response = await fetch('http://localhost:8080/messages/recent');
      const data = await response.json();
      setMessages(data);
    } catch (error) {
      console.error('获取最近消息失败:', error);
    }
  };

  // 连接WebSocket
  const connect = () => {
    if (!username.trim()) {
      alert('请输入用户名');
      return;
    }

    const socket = new SockJS(SOCKET_URL);
    const client = Stomp.over(socket);

    client.connect({}, () => {
      setStompClient(client);
      setConnected(true);

      // 订阅公共话题
      client.subscribe('/topic/public', onMessageReceived);

      // 发送用户加入消息
      client.send('/app/chat.addUser',
        {},
        JSON.stringify({ senderUsername: username, type: 'JOIN' })
      );

      // 获取最近消息
      fetchRecentMessages();
    }, onError);
  };

  // 断开连接
  const disconnect = () => {
    if (stompClient) {
      stompClient.disconnect();
    }
    setConnected(false);
  };

  // 发送消息
  const sendMessage = (event) => {
    event.preventDefault();
    if (!message.trim() || !stompClient) return;

    const chatMessage = {
      senderId: userId,
      content: message,
      type: 'CHAT'
    };

    stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(chatMessage));
    setMessage('');
  };

  // 接收消息
  const onMessageReceived = (payload) => {
    const receivedMessage = JSON.parse(payload.body);
    
    if (receivedMessage.type === 'JOIN') {
      if (receivedMessage.senderUsername === username) {
        setUserId(receivedMessage.senderId);
      }
    }
    
    setMessages((prevMessages) => [...prevMessages, receivedMessage]);
  };

  // 连接错误处理
  const onError = (error) => {
    console.error('连接错误:', error);
    setConnected(false);
  };

  // 格式化时间
  const formatTime = (dateTime) => {
    if (!dateTime) return '';
    const date = new Date(dateTime);
    return date.toLocaleTimeString();
  };

  // 登录页面
  if (!connected) {
    return (
      <div className="join-container">
        <h1>加入聊天室</h1>
        <input
          type="text"
          placeholder="输入用户名"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && connect()}
        />
        <button onClick={connect}>加入</button>
      </div>
    );
  }

  // 聊天室页面
  return (
    <div className="chat-container">
      <div className="chat-header">
        <h2>简易聊天室</h2>
        <p>欢迎, {username}!</p>
      </div>

      <div className="chat-messages">
        {messages.map((msg, index) => {
          if (msg.type === 'JOIN') {
            return (
              <div key={index} className="system-message">
                {msg.senderUsername} 加入了聊天室
              </div>
            );
          } else if (msg.type === 'LEAVE') {
            return (
              <div key={index} className="system-message">
                {msg.senderUsername} 离开了聊天室
              </div>
            );
          } else {
            return (
              <div key={index} className={`message ${msg.senderId === userId ? 'self' : 'other'}`}>
                <div className="message-info">
                  <span className="message-sender">{msg.senderUsername}</span>
                  <span className="message-time">{formatTime(msg.timestamp)}</span>
                </div>
                <div className="message-content">{msg.content}</div>
              </div>
            );
          }
        })}
        <div ref={messagesEndRef} />
      </div>

      <form className="chat-form" onSubmit={sendMessage}>
        <input
          type="text"
          placeholder="输入消息..."
          value={message}
          onChange={(e) => setMessage(e.target.value)}
        />
        <button type="submit">发送</button>
      </form>
    </div>
  );
}

export default App;
