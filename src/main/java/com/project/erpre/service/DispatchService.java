package com.project.erpre.service;

import com.project.erpre.model.dto.AndroidDispatchDTO;
import com.project.erpre.model.entity.Dispatch;
import com.project.erpre.model.entity.OrderDetail;
import com.project.erpre.model.entity.Product;
import com.project.erpre.repository.DispatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            //
        }

        return result;
    }
}