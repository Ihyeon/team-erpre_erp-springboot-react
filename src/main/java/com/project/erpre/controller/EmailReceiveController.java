package com.project.erpre.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.erpre.model.dto.EmailReceiveDTO;
import com.project.erpre.model.entity.EmailReceive;
import com.project.erpre.repository.EmailReceiveRepository;
import com.project.erpre.service.EmailReceiveService;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "http://localhost:8787")
public class EmailReceiveController {

  @Autowired
  private EmailReceiveService emailReceiveService;

  @Autowired
  private EmailReceiveRepository emailReceiveRepository;

  @GetMapping("/receive/{employeeEmail}")
  public List<EmailReceiveDTO> getEmailsDirectlyFromIMAP(@RequestParam String username,
      @RequestParam String password, @PathVariable String employeeEmail) {
    return emailReceiveService.fetchEmailsFromIMAP(employeeEmail);

    // // 받은메일 Gmail IMAP 서버에서 받아와서 DB 저장
    // @GetMapping("/receive/{employeeId}")
    // public void receiveEmails(@RequestParam String username, @RequestParam String
    // password,
    // @PathVariable String employeeId) {
    // emailReceiveService.fetchAndSaveEmails(username, password, employeeId);
    // }

    // 받은메일 출력
    // @GetMapping("/receive/view/{employeeId}")
    // public ResponseEntity<List<EmailReceiveDTO>> getAllReceivedEmails() {
    // List<EmailReceive> emails = emailReceiveRepository.findAll();
    // List<EmailReceiveDTO> emailDTOs = emails.stream()
    // .map(EmailReceiveDTO::new) // 엔티티에서 DTO로 매핑
    // .collect(Collectors.toList());
    // return ResponseEntity.ok(emailDTOs);
    // }

    // @GetMapping("/receive/read/{employeeId}")
    // public List<EmailReceive> getEmailReceives(@PathVariable String employeeId) {
    // return emailReceiveService.getEmailReceiveByEmployeeId(employeeId);
    // }
  }
}