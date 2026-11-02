package com.demo.example.mstransfercontrol.controller;

import com.demo.example.mstransfercontrol.dto.TransferRequest;
import com.demo.example.mstransfercontrol.dto.TransferResponse;
import com.demo.example.mstransfercontrol.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/transfers/control")
    public TransferResponse transfer(@RequestHeader Map<String, String> headers,
                                     @RequestBody TransferRequest request) {
        return transferService.validateAndTransfer(request);
    }

}
