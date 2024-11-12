package com.project.erpre.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.erpre.model.entity.EmailFileReceive;
import com.project.erpre.model.entity.EmailReceive;
import com.project.erpre.repository.EmailReceiveFileRepository;
import com.project.erpre.repository.EmailReceiveRepository;
import com.project.erpre.repository.EmailSendFileRepository;
import com.project.erpre.repository.EmailSendRepository;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Properties;

@Service
public class EmailReceiveService {

    private static final String HOST = "imap.gmail.com"; // Gmail IMAP 서버와 통신할 수 있게함
    private static final String MAIL_STORE_TYPE = "imap"; // IMAP 프로토콜 사용을 지정함

    @Autowired
    private EmailReceiveRepository emailReceiveRepository;

    @Autowired
    private EmailReceiveFileRepository emailReceiveFileRepository;

    public void fetchAndSaveEmails(String username, String password) {
        Properties properties = new Properties(); // Gmail IMAP 서버에 연결하는데 필요한 설정들
        properties.put("mail.store.protocol", MAIL_STORE_TYPE); // 메일서버와 통신할 프로토콜
        properties.put("mail.imap.host", HOST); // IMAP 서버지정해 설정
        properties.put("mail.imap.port", "993"); // IMAP연결 표준 포트번호(993 : 암호화연결사용번호)
        properties.put("mail.imap.ssl.enable", "true"); // 이메일 서버와의 연결을 암호화

        try {
            Session emailSession = Session.getDefaultInstance(properties); // 세션 생성
            Store store = emailSession.getStore(MAIL_STORE_TYPE); // IMAP 스토어 객체 생성해 IMAP 서버에 연결함
            store.connect("imap.gmail.com", "hojinkim001155@gmail.com", "icsw xsat ynhm aeqp");// 사용자이름과 비번

            Folder emailFolder = store.getFolder("INBOX"); // 받은 편지함 폴더 열기
            emailFolder.open(Folder.READ_ONLY); // 읽기전용모드로 폴더오픈

            Message[] messages = emailFolder.getMessages(); // 이메일 메시지 가져오기
            for (Message message : messages) {
                saveEmailToDatabase(message);
            }

            emailFolder.close(false); // 폴더와 스토어 닫기
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
    }

    private void saveEmailToDatabase(Message message) throws MessagingException, IOException {
        String employeeId = "로그인한 사용자 ID"; // 실제 employeeId 값을 가져오는 로직 추가 필요
        String from = message.getFrom()[0].toString();
        String subject = message.getSubject();
        String text = getTextFromMessage(message);
    
        EmailReceive emailReceive = new EmailReceive();
        emailReceive.setEmailIdR(employeeId);
        emailReceive.setEmailAddrSendR(from);
        emailReceive.setEmailSubjectR(subject);
        emailReceive.setEmailTextR(text);
        emailReceive.setEmailStatusR("nr");
        emailReceive.setEmailDateR(new Timestamp(System.currentTimeMillis()));
    
        EmailReceive savedEmailReceive = emailReceiveRepository.save(emailReceive);
    
        if (message.getContent() instanceof MimeMultipart) {
            saveAttachments((MimeMultipart) message.getContent(), savedEmailReceive);
        }
    }
    

    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        if (message.isMimeType("text/plain")) { // 메일이 단순 텍스트 이메일이면 getContent로 가져옴
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) { // 메일이 multipart 형식이면 getTextFromMimeMultipart로 가져옴
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder(); // 본문 내용이 multipart 형식일때 사용함
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                result.append(org.jsoup.Jsoup.parse(bodyPart.getContent().toString()).text());
            }
        }
        return result.toString();
    }

    private void saveAttachments(MimeMultipart mimeMultipart, EmailReceive savedEmailReceive)
            throws MessagingException, IOException {
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                String fileName = bodyPart.getFileName();
                File tempFile = File.createTempFile("attachment-", fileName);
                try (InputStream inputStream = bodyPart.getInputStream();
                        FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[4096];
                    int byteRead;
                    while ((byteRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, byteRead);
                    }
                }

                EmailFileReceive emailFileReceive = new EmailFileReceive();
                emailFileReceive.setEmailNmR(savedEmailReceive);
                emailFileReceive.setEmailFileNameR(fileName);
                emailFileReceive.setEmailFileUrlR(tempFile.getAbsolutePath());
                emailFileReceive.setEmailFileSizeR(tempFile.length());
                emailFileReceive.setEmailFileTypeR(bodyPart.getContentType());

                emailReceiveFileRepository.save(emailFileReceive);
            }
        }
    }

}
