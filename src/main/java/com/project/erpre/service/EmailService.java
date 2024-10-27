package com.project.erpre.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.project.erpre.model.entity.EmailSend;
import com.project.erpre.repository.EmailRepository;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailRepository emailRepository;

    @Transactional
    public EmailSend sendEmail(String to, String subject, String text, List<MultipartFile> files) {

        try {
            MimeMessage message = mailSender.createMimeMessage(); // 이메일 메세지 생성
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // true: 첨부파일 지원을 의미

            // 수신자
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            helper.setFrom("hojinkim001155@gmail.com");

            //첨부파일
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
                    file.transferTo(tempFile);
                    FileSystemResource fileResource = new FileSystemResource(tempFile);
                    helper.addAttachment(file.getOriginalFilename(), fileResource); // FileSystemResource를 사용해 파일 리소스를 생성한 다음, helper.addAttachment를 통해 이메일에 첨부
                }
            }
            //이메일 전송
            mailSender.send(message);

            // EmailSend 엔티티 생성 및 저장
            EmailSend emailSend = new EmailSend();
            emailSend.setEmailIdS("E001");
            emailSend.setEmailAddrReceiveS(to);
            emailSend.setEmailSubjectS(subject);
            emailSend.setEmailContentS(text);
            emailSend.setEmailStatusS("nr");
            emailSend.setEmailDateS(new Timestamp(System.currentTimeMillis()));

            EmailSend savedEmailSend = emailRepository.save(emailSend);
            return savedEmailSend;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("이메일 전송 중 오류가 발생했습니다.");
        }
    }
}

