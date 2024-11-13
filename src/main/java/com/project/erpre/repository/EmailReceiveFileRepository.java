package com.project.erpre.repository;

import com.project.erpre.model.entity.EmailFileReceive;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailReceiveFileRepository extends JpaRepository<EmailFileReceive, Integer> {

  List<EmailFileReceive> findByEmailNmR(Integer emailNmR);

}
