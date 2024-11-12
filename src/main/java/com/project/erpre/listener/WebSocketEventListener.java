package com.project.erpre.listener;

import com.project.erpre.controller.MessengerController;
import com.project.erpre.event.StatusMessageUpdateEvent;
import com.project.erpre.event.StatusUpdateEvent;
import com.project.erpre.model.dto.EmployeeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet(); // 현재 접속중인 유저 ID 저장
    private final SimpMessagingTemplate messagingTemplate; // 클라이언트에게 메시지 전송

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 웹소켓 연결 이벤트 처리
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication auth = (Authentication) headerAccessor.getUser();

        if (auth != null) {
            String employeeId = auth.getName();
            onlineUsers.add(employeeId);
        }
    }

    // 웹소켓 연결 해제 이벤트 처리
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication auth = (Authentication) headerAccessor.getUser();

        if (auth != null) {
            String employeeId = auth.getName();
            onlineUsers.remove(employeeId);
        }
    }

    // 상태 업데이트 이벤트 처리
    @EventListener
    public void handleStatusUpdateEvent(StatusUpdateEvent event) {
        logger.info("상태 업데이트 이벤트 수신: {}", event.getStatus());
        String statusUpdate = event.getStatus();
        messagingTemplate.convertAndSend("/topic/status", statusUpdate);
    }

    // 상태 메시지 업데이트 이벤트 처리
    @EventListener
    public void handleStatusMessageUpdateEvent(StatusMessageUpdateEvent event) {
        logger.info("상태 메시지 업데이트 이벤트 수신: {}", event.getStatusMessage());
        String statusMessageUpdate = event.getStatusMessage();
        messagingTemplate.convertAndSend("/topic/statusMessage", statusMessageUpdate);
    }

    // 사용자의 온라인 상태 확인
    // 메시지 전송 시 해당 사용자의 온라인 여부에 따라 실시간 전송, 알림 결정
    public boolean isUserOnline(String employeeId) {
        return onlineUsers.contains(employeeId);
    }



}
