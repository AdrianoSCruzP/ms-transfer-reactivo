package com.demo.example.mstransfercontrol.mapper;

import com.demo.example.mstransfercontrol.dto.TransferRequest;
import com.demo.example.mstransfercontrol.dto.TransferResponse;
import com.demo.example.mstransfercontrol.entity.Transfer;

public interface TransferMapper {

    TransferResponse toApprovedResponse(TransferRequest request);
    Transfer toTransferEntity(TransferRequest request);

}

