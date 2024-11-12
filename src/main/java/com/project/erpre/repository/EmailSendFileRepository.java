package com.project.erpre.repository;

import com.project.erpre.model.entity.EmailFileSend;
import com.project.erpre.model.entity.EmailSend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailSendFileRepository extends JpaRepository<EmailFileSend, Integer> {

  // 보낸메일 첨부파일 목록 조회
  List<EmailFileSend> findByEmailNmS(EmailSend emailNmS);

  // 보낸메일 첨부파일 내역 다운로드 (보낸메일함)

}
