package com.project.erpre.repository;

import com.project.erpre.model.entity.EmailReceive;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface EmailReceiveRepository extends JpaRepository<EmailReceive, Integer> {

    // 받은 메일 내역 조회 (받은메일함)
    List<EmailReceive> findByEmployeeId(String employeeId);

}
