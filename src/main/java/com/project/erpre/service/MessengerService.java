package com.project.erpre.service;

import com.project.erpre.model.dto.ChatDTO;
import com.project.erpre.model.dto.ChatMessageDTO;
import com.project.erpre.model.dto.EmployeeDTO;
import com.project.erpre.model.entity.Chat;
import com.project.erpre.model.entity.ChatParticipant;
import com.project.erpre.model.entity.Employee;
import com.project.erpre.repository.ChatParticipantRepository;
import com.project.erpre.repository.ChatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.project.erpre.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessengerService {

    private static final Logger logger = LoggerFactory.getLogger(MessengerService.class);

    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public MessengerService(ChatRepository chatRepository, ChatParticipantRepository chatParticipantRepository, EmployeeRepository employeeRepository) {
        this.chatRepository = chatRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.employeeRepository = employeeRepository;
    }


    // 2. 선택된 채팅방 조회
    public List<ChatMessageDTO> getSelectedChat(Long chatNo, String searchKeyword) {
        String employeeId = getEmployeeIdFromAuthentication();
        return chatRepository.getSelectedChat(chatNo, searchKeyword);
    }


    /////////////////////////////////////////////////////////////////////// 🟢 공통

    // 사용자 인증
    private String getEmployeeIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증된 사용자를 찾을 수 없습니다. 로그인이 필요합니다.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    /////////////////////////////////////////////////////////////////////// 🔴 채팅


    // 현재 참여하고 있는 채팅 목록 조회 및 검색
    public List<ChatDTO> getChatListByUser(String searchKeyword){
        String employeeId = getEmployeeIdFromAuthentication();
        return chatRepository.getChatListByUser(employeeId, searchKeyword);
    }

    // 새 채팅방 생성
    @Transactional
    public ChatDTO createChatRoom(List<String> participantIds) {

        // 현재 로그인 된 유저 아이디 조회
        String employeeId = getEmployeeIdFromAuthentication();

        // 채팅방 엔티티 생성
        Chat chat = new Chat();

        // 참여자 목록 가져오기
        List<Employee> participants = participantIds.stream()
                .map(participantId -> employeeRepository.findById(participantId)
                        .orElseThrow(() -> new RuntimeException("직원을 찾을 수 없습니다: " + participantId)))
                .collect(Collectors.toList());
        
        // 채팅방 제목 설정
        if(participantIds.size() == 1) {
            chat.setChatOriginTitle(participants.get(0).getEmployeeName()); // 1:1 채팅방 이름 설정
        } else {
            String firstParticipantName = participants.get(0).getEmployeeName();
            chat.setChatOriginTitle(firstParticipantName + " 외 " + (participants.size() - 1) + "인"); // 단체 채팅방 이름 설정
        }

        // 채팅방 저장
        Chat saveChat = chatRepository.save(chat);

        // 채팅 참여자 저장
        List<ChatParticipant> chatParticipants = participants.stream()
                .map(employee -> new ChatParticipant(saveChat, employee))
                .collect(Collectors.toList());

        // 현재 로그인된 사용자를 ChatParticipant로 생성하여 추가
        Employee currentUser = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("현재 사용자를 찾을 수 없습니다: " + employeeId));
        chatParticipants.add(new ChatParticipant(saveChat, currentUser));

        // 모든 참여자 저장
        chatParticipantRepository.saveAll(chatParticipants);

        // 저장된 채팅방 정보를 DTO로 변환하여 반환
        return new ChatDTO(saveChat.getChatNo(), saveChat.getChatOriginTitle(), chatParticipants);
    }

}
