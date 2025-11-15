package com.dasi.core.websocket;

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
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {}

    @OnMessage
    public void onMessage(String message, Session session) {}

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
