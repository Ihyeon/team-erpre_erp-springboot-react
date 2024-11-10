package com.project.erpre.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Properties;

@Service
public class EmailReceiveService {

    private static final String HOST = "imap.gmail.com";
    private static final String MAIL_STORE_TYPE = "imap";

    public void fetchEmails(String username, String password) {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", MAIL_STORE_TYPE);
        properties.put("mail.imap.host", HOST);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");

        try {
            Session emailSession = Session.getDefaultInstance(properties); // 세션 생성
            Store store = emailSession.getStore(MAIL_STORE_TYPE);  // IMAP 스토어 객체 생성
            store.connect(HOST, username, password);

            Folder emailFolder = store.getFolder("INBOX");  // 받은 편지함 폴더 열기
            emailFolder.open(Folder.READ_ONLY);

            Message[] messages = emailFolder.getMessages(); // 이메일 메시지 가져오기
            for (Message message : messages) {
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + getTextFromMessage(message));
            }

            //폴더와 스토어 닫기
            emailFolder.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }

    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
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

}
