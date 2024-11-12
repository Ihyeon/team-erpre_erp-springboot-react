package com.project.erpre.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.project.erpre.model.entity.EmailSend;

public interface EmailSendRepository extends JpaRepository<EmailSend, Integer> {

  // 보낸메일 내역 조회 (보낸메일함)
  List<EmailSend> findByEmployeeId(String employeeId);

}
