package com.demo.example.mstransfercontrol.mapper.impl;

import com.demo.example.mstransfercontrol.dto.TransferRequest;
import com.demo.example.mstransfercontrol.dto.TransferResponse;
import com.demo.example.mstransfercontrol.mapper.TransferMapper;
import com.demo.example.mstransfercontrol.entity.Transfer;
import org.springframework.stereotype.Component;

@Component
public class TransferMapperImpl implements TransferMapper {

    public TransferResponse toApprovedResponse(TransferRequest request) {
        return TransferResponse.builder()
                .status("APPROVED")
                .sourceAccount(request.getSourceAccount())
                .targetAccount(request.getTargetAccount())
                .amount(request.getAmount())
                .build();
    }

    public Transfer toTransferEntity(TransferRequest request) {
        return Transfer.builder()
                .customerId(request.getCustomerId())
                .status("successful")
                .accountNumber(request.getSourceAccount())
                .accountDestination(request.getTargetAccount())
                .amount(request.getAmount())
                .build();
    }

}
