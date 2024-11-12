package com.project.erpre.repository;

import com.project.erpre.model.entity.EmailFileReceive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailReceiveFileRepository extends JpaRepository<EmailFileReceive, Integer> {
}
