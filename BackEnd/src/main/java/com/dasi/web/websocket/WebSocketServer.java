package com.dasi.web.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
@ServerEndpoint("/ws/notify")
@Component
@Slf4j
public class WebSocketServer {
    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        log.debug("【WebSocket】建立连接：{}", session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        log.debug("【WebSocket】关闭连接：{}", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("【WebSocket】错误连接：{}", session.getId(), throwable);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("【WebSocket】收到 {} 的消息：{}", session.getId(), message);
    }

    private void send(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ignored) {}
    }

    public static void broadcast(String message) {
        sessions.forEach(session -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException ignored) {}
        });
    }

}
