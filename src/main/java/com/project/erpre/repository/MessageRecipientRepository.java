package com.project.erpre.repository;

import com.project.erpre.model.entity.MessageRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRecipientRepository extends JpaRepository<MessageRecipient, Long> {
}
