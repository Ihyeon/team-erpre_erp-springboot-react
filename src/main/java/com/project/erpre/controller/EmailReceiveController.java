package com.project.erpre.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.erpre.service.EmailReceiveService;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "http://localhost:8787")
public class EmailReceiveController {

  @Autowired
  private EmailReceiveService emailReceiveService;

  @GetMapping("/receive")
  public void receiveEmails(@RequestParam String username, @RequestParam String password) {
    emailReceiveService.fetchAndSaveEmails(username, password);
  }
}
