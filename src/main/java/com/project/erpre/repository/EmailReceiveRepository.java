package com.project.erpre.repository;

import com.project.erpre.model.entity.EmailReceive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailReceiveRepository extends JpaRepository<EmailReceive, Integer> {
}
