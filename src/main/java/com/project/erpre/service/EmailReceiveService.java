package com.project.erpre.service;

import com.project.erpre.model.dto.EmailReceiveDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.erpre.model.entity.EmailFileReceive;
import com.project.erpre.model.entity.EmailReceive;
import com.project.erpre.repository.EmailReceiveFileRepository;
import com.project.erpre.repository.EmailReceiveRepository;
import com.project.erpre.repository.EmployeeRepository;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class EmailReceiveService {

    @Autowired
    private EmployeeRepository employeeRepository;

    private static final String HOST = "imap.gmail.com"; // Gmail IMAP 서버와 통신할 수 있게함
    private static final String MAIL_STORE_TYPE = "imap"; // IMAP 프로토콜 사용을 지정함

    private static final String MAIN_USERNAME = "hojinkim001155@gmail.com";
    private static final String MAIN_PASSWORD = "icsw xsat ynhm aeqp";

    public List<EmailReceiveDTO> fetchEmailsFromIMAP(String employeeEmail) {
        Properties properties = new Properties(); // Gmail IMAP 서버에 연결하는데 필요한 설정들
        properties.put("mail.store.protocol", "imap"); // 메일서버와 통신할 프로토콜
        properties.put("mail.imap.host", "imap.gmail.com"); // IMAP 서버지정해 설정
        properties.put("mail.imap.port", "993"); // IMAP연결 표준 포트번호(993 : 암호화연결사용번호)
        properties.put("mail.imap.ssl.enable", "true"); // 이메일 서버와의 연결을 암호화
        properties.put("mail.debug", "true"); // 디버그 활성화

        List<EmailReceiveDTO> emails = new ArrayList<>();

        try {
            System.out.println("메일을 가져오기위한 로그인계정: " + MAIN_USERNAME);
            Session emailSession = Session.getDefaultInstance(properties); // 세션 생성
            Store store = emailSession.getStore("imap"); // IMAP 스토어 객체 생성해 IMAP 서버에 연결함
            store.connect(MAIN_USERNAME, MAIN_PASSWORD);// 사용자이름과 비번
            System.out.println("수신메일을 위한 계정이 성공적으로 연결되었습니다.");

            try {
                Folder[] folders = store.getDefaultFolder().list("*");
                for (Folder folder : folders) {
                    System.out.println("Available folder: " + folder.getFullName());
                }
            } catch (MessagingException e) {
                System.out.println("IMAP 폴더 목록을 가져오는 중 오류 발생:");
                e.printStackTrace();
            }

            Folder emailFolder = store.getFolder("[Gmail]/전체보관함"); // 받은 편지함 폴더 열기
            if (emailFolder == null) { // emailFolder가 null일 경우
                System.out.println("[Gmail]/전체보관함 폴더를 찾을 수 없습니다.");
                return emails;
            }
            emailFolder.open(Folder.READ_ONLY); // 읽기전용모드로 폴더오픈

            Message[] messages = emailFolder.getMessages(); // 이메일 메시지 가져오기
            if (messages == null) { // messages가 null인 경우
                System.out.println("폴더에서 메시지를 가져오지 못했습니다.");
                return emails;
            }
            for (Message message : messages) {
                Address[] recipients = message.getRecipients(Message.RecipientType.TO);
                if (recipients == null) { // recipients가 null인 경우
                    System.out.println("수신자가 없는 메시지가 있습니다.");
                    continue;
                }
                for (Address address : recipients) {
                    if (address instanceof InternetAddress) {
                        String recipientEmail = ((InternetAddress) address).getAddress();
                        System.out.println("수신자 이메일: " + recipientEmail);
                        System.out.println("로그인직원 이메일: " + employeeEmail);
                        if (recipientEmail != null && recipientEmail.equalsIgnoreCase(employeeEmail)) {
                            EmailReceiveDTO emailDTO = convertMessageToDTO(message, employeeEmail);
                            emails.add(emailDTO);
                            break;
                        }
                    }
                }
            }
            emailFolder.close(false); // 폴더와 스토어 닫기
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emails;
    }

    private EmailReceiveDTO convertMessageToDTO(Message message, String employeeEmail)
            throws MessagingException, IOException {
        String from = message.getFrom()[0].toString();
        String subject = message.getSubject();
        String text = getTextFromMessage(message);

        return EmailReceiveDTO.builder()
                .emailAddrSendR(from)
                .emailSubjectR(subject)
                .emailTextR(text)
                .emailDateR(new Timestamp(message.getReceivedDate().getTime()))
                .emailStatusR("nr")
                .employeeEmail(employeeEmail)
                .build();
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
    //
    // private void saveAttachments(MimeMultipart mimeMultipart, EmailReceive
    // savedEmailReceive)
    // throws MessagingException, IOException {
    // int count = mimeMultipart.getCount();
    // for (int i = 0; i < count; i++) {
    // BodyPart bodyPart = mimeMultipart.getBodyPart(i);
    // if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
    // String fileName = bodyPart.getFileName();
    // File tempFile = File.createTempFile("attachment-", fileName);
    // try (InputStream inputStream = bodyPart.getInputStream();
    // FileOutputStream outputStream = new FileOutputStream(tempFile)) {
    // byte[] buffer = new byte[4096];
    // int byteRead;
    // while ((byteRead = inputStream.read(buffer)) != -1) {
    // outputStream.write(buffer, 0, byteRead);
    // }
    // }
    //
    // EmailFileReceive emailFileReceive = new EmailFileReceive();
    // emailFileReceive.setEmailNmR(savedEmailReceive);
    // emailFileReceive.setEmailFileNameR(fileName);
    // emailFileReceive.setEmailFileUrlR(tempFile.getAbsolutePath());
    // emailFileReceive.setEmailFileSizeR(tempFile.length());
    // emailFileReceive.setEmailFileTypeR(bodyPart.getContentType());
    //
    // emailReceiveFileRepository.save(emailFileReceive);
    // }
    // }
    // }
    //
    // //받은메일 전체 조회
    // public List<EmailReceive> getEmailReceiveByEmployeeId(String employeeId) {
    // return emailReceiveRepository.findByEmployeeId(employeeId);
    // }
}
