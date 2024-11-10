package com.project.erpre.repository;
import com.project.erpre.model.entity.EmailFileSend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailFileRepository extends JpaRepository<EmailFileSend, Integer> {
}
