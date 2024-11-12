//package com.project.erpre.config;
//
//import io.github.cdimascio.dotenv.Dotenv;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//import javax.annotation.PostConstruct;
//import java.util.Properties;
//
//// 이메일 관련 계정 정보 외부 사용위함
//@Configuration
//public class EnvConfig {
//
//    private final Dotenv dotenv;
//
//    public EnvConfig() {
//        this.dotenv = Dotenv.configure().ignoreIfMissing().load();
//    }
//
//    @Bean
//    public JavaMailSender javaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//
//        mailSender.setHost(dotenv.get("SMTP_HOST"));
//        mailSender.setPort(Integer.parseInt(dotenv.get("SMTP_PORT")));
//        mailSender.setUsername(dotenv.get("SMTP_USERNAME"));
//        mailSender.setPassword(dotenv.get("SMTP_APP_PASSWORD"));
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enalbe", "true");
//        props.put("mail.debug", "true");
//        return mailSender;
//    }

//    static {
//        // .env일 로드
////        Dotenv dotenv = Dotenv.load();
//
//        // SMTP 설정
//        System.setProperty("SMTP_HOST", dotenv.get("SMTP_HOST"));
//        System.setProperty("SMTP_PORT", dotenv.get("SMTP_PORT"));
//        System.setProperty("SMTP_USERNAME", dotenv.get("SMTP_USERNAME"));
//        System.setProperty("SMTP_APP_PASSWORD", dotenv.get("SMTP_APP_PASSWORD"));
//
//        // IMAP 설정
//        System.setProperty("IMAP_HOST", dotenv.get("IMAP_HOST"));
//        System.setProperty("IMAP_PORT", dotenv.get("IMAP_PORT"));
//        System.setProperty("IMAP_USERNAME", dotenv.get("IMAP_USERNAME"));
//        System.setProperty("IMAP_APP_PASSWORD", dotenv.get("IMAP_APP_PASSWORD"));
//
//        System.out.println("smtp 호스트 확인: "+dotenv.get("SMTP_HOST"));
//    }
//}
