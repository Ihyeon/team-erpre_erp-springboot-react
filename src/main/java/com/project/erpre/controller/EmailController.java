package com.project.erpre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.erpre.model.entity.EmailSend;
import com.project.erpre.service.EmailService;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "http://localhost:8787")
public class EmailController {

    @Autowired
    private EmailService emailService;

    //이메일 전송
    @PostMapping("/send")
    public EmailSend sendEmail(
            @RequestParam("to") String to, //수신자 이메일 주소
            @RequestParam("subject") String subject, //이메일 제목
            @RequestParam("text") String text, //이메일 본문
            @RequestParam(value = "files", required = false) List<MultipartFile> files) { //첨부파일 목록

        try {
            //EmailService를 호출하여 이메일을 전송하고, 결과 반환
            EmailSend emailSend = emailService.sendEmail(to, subject, text, files);
            return emailSend;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("이메일 전송에 실패했습니다.");
        }
    }
}
