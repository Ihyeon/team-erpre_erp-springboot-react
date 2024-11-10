package com.project.erpre.service;

import com.project.erpre.model.dto.AndroidDispatchDTO;
import com.project.erpre.model.entity.Dispatch;
import com.project.erpre.model.entity.OrderDetail;
import com.project.erpre.model.entity.Product;
import com.project.erpre.repository.DispatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DispatchService {

    @Autowired
    private DispatchRepository dispatchRepository;

    public List<AndroidDispatchDTO> getInProgressDispatches(Integer warehouseNo) {
        List<Dispatch> dispatchList = dispatchRepository.findInProgressDispatchesByWarehouseNo(warehouseNo);
        List<AndroidDispatchDTO> result = new ArrayList<>();

        for (Dispatch dispatch : dispatchList) {
            OrderDetail orderDetail = dispatch.getOrderDetail();
            Product product = orderDetail.getProduct();

            AndroidDispatchDTO dto = new AndroidDispatchDTO(
                    dispatch.getDispatchNo(),
                    product.getProductNm(),
                    orderDetail.getOrderDQty()
            );
            result.add(dto);
        }
        return result;
    }

    public boolean updateDispatchStatus(Integer dispatchNo) { //성공 여부를 boolean 값으로 반환
        Optional<Dispatch> optionalDispatch = dispatchRepository.findById(dispatchNo);
        if (optionalDispatch.isPresent()) {
            Dispatch dispatch = optionalDispatch.get();
            dispatch.setDispatchStatus("complete"); // dispatchStatus를 "complete"로 변경
            dispatchRepository.save(dispatch); // 변경 사항 저장
            return true;
        } else {
            return false; // 해당 dispatchNo가 없는 경우
        }
    }


}