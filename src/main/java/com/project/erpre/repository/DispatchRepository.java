package com.project.erpre.repository;

import com.project.erpre.model.dto.AndroidDispatchDTO;
import com.project.erpre.model.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispatchRepository extends JpaRepository<Dispatch, Integer> {

    @Query("SELECT d FROM Dispatch d " +
            "JOIN d.orderDetail od " +
            "JOIN od.product p " +
            "WHERE d.warehouse.warehouseNo = :warehouseNo " +
            "AND d.dispatchStatus = 'inProgress'")
    List<Dispatch> findInProgressDispatchesByWarehouseNo(@Param("warehouseNo") Integer warehouseNo);
}