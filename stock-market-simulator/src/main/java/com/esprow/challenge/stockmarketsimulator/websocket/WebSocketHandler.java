package com.esprow.challenge.stockmarketsimulator.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    private Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        getUserIDFromSession(session)
                .ifPresentOrElse(userID -> userSessions.put(userID, session), () -> {
                    log.warn("Received session without userID attribute");
                });

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        getUserIDFromSession(session).ifPresent(userID -> userSessions.remove(userID));
        super.afterConnectionClosed(session, status);
    }

    private Optional<String> getUserIDFromSession(WebSocketSession session) {
        return Optional.ofNullable(session.getHandshakeHeaders().get("userid").get(0));
    }

    public void sendMessage(String userId, String message) throws IOException {
        Objects.requireNonNull(message, "User message is NULL");
        WebSocketSession session = Optional.ofNullable(userSessions.get(userId))
                .orElseThrow(() -> new IllegalArgumentException("Don't found user by ID=" + userId));
        session.sendMessage(new TextMessage(message));
    }
}
