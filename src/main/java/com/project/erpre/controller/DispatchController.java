package com.project.erpre.controller;

import com.project.erpre.model.dto.AndroidDispatchDTO;
import com.project.erpre.model.entity.Dispatch;
import com.project.erpre.service.DispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/android/api")
public class DispatchController {

    @Autowired
    private DispatchService dispatchService;

    @CrossOrigin(origins = "")
    @GetMapping("/get/dispatch/inProgress/{warehouseNo}")
    public List<AndroidDispatchDTO> getInProgressDispatches(@PathVariable Integer warehouseNo) {
        return dispatchService.getInProgressDispatches(warehouseNo);
        //
    }
}