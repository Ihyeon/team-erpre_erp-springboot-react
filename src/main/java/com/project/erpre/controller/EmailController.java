package com.project.erpre.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.erpre.model.dto.EmailSendDTO;
import com.project.erpre.model.dto.EmployeeDTO;
import com.project.erpre.model.entity.EmailSend;
import com.project.erpre.service.EmailService;
import com.project.erpre.service.EmailService.InvalidEmailAddressException;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "http://localhost:8787")
public class EmailController {

    @Autowired
    private EmailService emailService;

    // 이메일 전송
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail( // HTTP 상태 제어 코드 에러관리 위해
            @RequestParam("to") String to, // 수신자 이메일 주소
            @RequestParam("subject") String subject, // 이메일 제목
            @RequestParam("text") String text, // 이메일 본문
            @RequestParam("from") String from,
            @RequestParam("emailIds") String emailIds,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) { // 첨부파일 목록

        try {
            // EmailService를 호출하여 이메일을 전송하고, 결과 반환
            EmailSend emailSend = emailService.sendEmail(to, subject, text, from, emailIds, files);
            return ResponseEntity.ok(emailSend);
        } catch (InvalidEmailAddressException e) {
            return ResponseEntity.badRequest().body("이메일 주소 형식이 잘못되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송 중 오류가 발생했습니다.");
        }
    }

    // 보낸메일 내역
    @GetMapping("/sent/{employeeId}")
    public List<EmailSend> getEmailSends(@PathVariable String employeeId) {
        return emailService.getEmailSendByEmployeeId(employeeId);
    }

    // 이메일 뷰어
    @GetMapping("/read/{emailNmS}")
    public ResponseEntity<EmailSend> getEmailSends(@PathVariable Integer emailNmS) {
        System.out.println("API호출: /read/" + emailNmS); //로그
        EmailSend email = emailService.findEmailById(emailNmS); // emailNmS에 해당하는 이메일을 검색함
        if (email != null) {
            System.out.println("이메일 ID:" + emailNmS + "에 대한 이메일이 정상적으로 조회되었습니다.");
            return ResponseEntity.ok(email); // 있으면 반환
        } else {
            System.out.println("이메일 ID: " + emailNmS + " 에 대한 이메일을 찾을 수 없습니다."); 
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 없으면 404응답
        }
    }

}
