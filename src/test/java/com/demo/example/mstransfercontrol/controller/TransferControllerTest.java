package com.demo.example.mstransfercontrol.controller;

import com.demo.example.mstransfercontrol.dto.TransferRequest;
import com.demo.example.mstransfercontrol.dto.TransferResponse;
import com.demo.example.mstransfercontrol.service.TransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferController transferController;

    @Test
    void deberiaInvocarElServicioConLaSolicitudYRetornarLaRespuesta() {
        // Arrange
        TransferRequest request = TransferRequest.builder()
                .customerId("CUS-001")
                .sourceAccount("00123456789")
                .targetAccount("00987654321")
                .amount(new BigDecimal("100.00"))
                .build();

        TransferResponse expectedResponse = TransferResponse.builder()
                .status("APPROVED")
                .sourceAccount(request.getSourceAccount())
                .targetAccount(request.getTargetAccount())
                .amount(request.getAmount())
                .build();

        Map<String, String> headers = Map.of("channel", "web");

        when(transferService.validateAndTransfer(request))
                .thenReturn(expectedResponse);

        // Act
        TransferResponse result = transferController.transfer(headers, request);

        // Assert
        assertSame(expectedResponse, result);
        verify(transferService).validateAndTransfer(eq(request));
    }
}
