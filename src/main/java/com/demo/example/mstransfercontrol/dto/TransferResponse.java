package com.demo.example.mstransfercontrol.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {
    private String status;
    private String sourceAccount;
    private String targetAccount;
    private BigDecimal amount;
}

