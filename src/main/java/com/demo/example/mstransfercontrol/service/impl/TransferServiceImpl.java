package com.demo.example.mstransfercontrol.service.impl;

import com.demo.example.mstransfercontrol.dto.TransferRequest;
import com.demo.example.mstransfercontrol.dto.TransferResponse;
import com.demo.example.mstransfercontrol.mapper.TransferMapper;
import com.demo.example.mstransfercontrol.entity.Account;
import com.demo.example.mstransfercontrol.repository.AccountRepository;
import com.demo.example.mstransfercontrol.repository.TransferRepository;
import com.demo.example.mstransfercontrol.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class TransferServiceImpl implements TransferService {

    private static final BigDecimal DAILY_LIMIT = new BigDecimal("10000.00");

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;

    @Override
    public TransferResponse validateAndTransfer(TransferRequest request) {
        Account account = accountRepository
                .findByAccountNumber(request.getSourceAccount())
                .orElseThrow(() -> new NoSuchElementException("Cuenta origen no existe"));

        if (!"ACTIVE".equals(account.getStatus())) {
            throw new IllegalStateException("Cuenta origen no esta activa");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monto debe ser mayor a cero");
        }

        LocalDate transferDate = LocalDate.now();
        LocalDateTime startDate = transferDate.atStartOfDay();
        LocalDateTime endDate = transferDate.plusDays(1).atStartOfDay();

        BigDecimal totalToday = transferRepository
                .totalTransferredToday(request.getSourceAccount(),startDate,endDate);

        if (totalToday.add(request.getAmount()).compareTo(DAILY_LIMIT) > 0) {
            throw new IllegalArgumentException("Limite diario excedido");
        }

        transferRepository.save(transferMapper.toTransferEntity(request));

        return transferMapper.toApprovedResponse(request);
    }
}

