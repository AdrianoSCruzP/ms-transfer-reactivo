package com.demo.example.mstransfercontrol.service.impl;

import com.demo.example.mstransfercontrol.dto.TransferRequest;
import com.demo.example.mstransfercontrol.dto.TransferResponse;
import com.demo.example.mstransfercontrol.entity.Account;
import com.demo.example.mstransfercontrol.entity.Transfer;
import com.demo.example.mstransfercontrol.mapper.TransferMapper;
import com.demo.example.mstransfercontrol.mapper.impl.TransferMapperImpl;
import com.demo.example.mstransfercontrol.repository.AccountRepository;
import com.demo.example.mstransfercontrol.repository.TransferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransferRepository transferRepository;

    @Spy
    private TransferMapper transferMapper = new TransferMapperImpl();

    @InjectMocks
    private TransferServiceImpl transferServiceImpl;

    @Test
    void deberiaLanzarNoSuchElementExceptionCuandoLaCuentaOrigenNoExiste() {
        // Arrange
        String cuentaOrigen = "00123456789";
        TransferRequest request = TransferRequest.builder()
                .sourceAccount(cuentaOrigen)
                .build();

        when(accountRepository.findByAccountNumber(cuentaOrigen))
                .thenReturn(Optional.empty());

        // Act
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> transferServiceImpl.validateAndTransfer(request)
        );

        // Assert
        assertEquals("Cuenta origen no existe", exception.getMessage());
        verify(accountRepository).findByAccountNumber(cuentaOrigen);
        verifyNoInteractions(transferRepository);
    }

    @Test
    void deberiaRechazarTransferenciaCuandoLaCuentaOrigenNoEstaActiva() {
        // Arrange
        String cuentaOrigen = "00123456789";
        Account account = Account.builder()
                .id(1L)
                .accountNumber(cuentaOrigen)
                .customerId("CUS-001")
                .status("BLOCKED")
                .build();
        TransferRequest request = crearRequest(cuentaOrigen, new BigDecimal("100.00"));

        when(accountRepository.findByAccountNumber(cuentaOrigen))
                .thenReturn(Optional.of(account));

        // Act
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> transferServiceImpl.validateAndTransfer(request)
        );

        // Assert
        assertEquals("Cuenta origen no esta activa", exception.getMessage());
        verify(accountRepository).findByAccountNumber(cuentaOrigen);
        verifyNoInteractions(transferRepository);
    }

    @ParameterizedTest
    @CsvSource({"0", "-1", "-100.50"})
    void deberiaRechazarTransferenciaCuandoElMontoNoEsMayorQueCero(BigDecimal monto) {
        // Arrange
        String cuentaOrigen = "00123456789";
        Account account = crearCuenta(cuentaOrigen, "ACTIVE");
        TransferRequest request = crearRequest(cuentaOrigen, monto);

        when(accountRepository.findByAccountNumber(cuentaOrigen))
                .thenReturn(Optional.of(account));

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transferServiceImpl.validateAndTransfer(request)
        );

        // Assert
        assertEquals("Monto debe ser mayor a cero", exception.getMessage());
        verify(accountRepository).findByAccountNumber(cuentaOrigen);
        verifyNoInteractions(transferRepository);
    }

    @Test
    void deberiaRechazarTransferenciaCuandoLaSumaDeTransferenciasDelDiaSuperaElLimite() {
        // Arrange
        String cuentaOrigen = "00123456789";
        TransferRequest request = crearRequest(cuentaOrigen, new BigDecimal("100.00"));
        configurarCuentaYTotal(cuentaOrigen, new BigDecimal("9900.01"));

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transferServiceImpl.validateAndTransfer(request)
        );

        // Assert
        assertEquals("Limite diario excedido", exception.getMessage());
        verify(transferRepository).totalTransferredToday(
                eq(cuentaOrigen), any(LocalDateTime.class), any(LocalDateTime.class)
        );
        verifyNoInteractions(transferMapper);
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    void deberiaPermitirTransferenciaCuandoLaSumaDeTransferenciasDelDiaEsExactamente10000() {
        // Arrange
        String cuentaOrigen = "00123456789";
        BigDecimal monto = new BigDecimal("100.00");
        TransferRequest request = crearRequest(cuentaOrigen, monto);
        configurarCuentaYTotal(cuentaOrigen, new BigDecimal("9900.00"));
        Transfer transfer = Transfer.builder().amount(monto).build();
        TransferResponse response = crearRespuestaAprobada(cuentaOrigen, monto);
        doReturn(transfer).when(transferMapper).toTransferEntity(request);
        doReturn(response).when(transferMapper).toApprovedResponse(request);

        // Act
        TransferResponse result = transferServiceImpl.validateAndTransfer(request);

        // Assert
        assertEquals("APPROVED", result.getStatus());
        verify(transferRepository).save(transfer);
    }

    @Test
    void deberiaRegistrarTransferenciaConEstadoApprovedCuandoTodasLasReglasSeCumplen() {
        // Arrange
        String cuentaOrigen = "00123456789";
        BigDecimal monto = new BigDecimal("250.00");
        TransferRequest request = crearRequest(cuentaOrigen, monto);
        configurarCuentaYTotal(cuentaOrigen, BigDecimal.ZERO);
        Transfer transfer = Transfer.builder()
                .accountNumber(cuentaOrigen)
                .amount(monto)
                .build();
        TransferResponse response = crearRespuestaAprobada(cuentaOrigen, monto);
        doReturn(transfer).when(transferMapper).toTransferEntity(request);
        doReturn(response).when(transferMapper).toApprovedResponse(request);

        // Act
        TransferResponse result = transferServiceImpl.validateAndTransfer(request);

        // Assert
        assertEquals("APPROVED", result.getStatus());
        assertEquals(cuentaOrigen, result.getSourceAccount());
        assertEquals(monto, result.getAmount());
        verify(transferMapper).toTransferEntity(request);
        verify(transferRepository).save(transfer);
    }

    @Test
    void noDeberiaInvocarSaveCuandoLaCuentaOrigenNoExiste() {
        // Arrange
        String cuentaOrigen = "00123456789";
        TransferRequest request = crearRequest(cuentaOrigen, new BigDecimal("100.00"));
        when(accountRepository.findByAccountNumber(cuentaOrigen))
                .thenReturn(Optional.empty());

        // Act
        assertThrows(
                NoSuchElementException.class,
                () -> transferServiceImpl.validateAndTransfer(request)
        );

        // Assert
        verifyNoInteractions(transferRepository);
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    void noDeberiaInvocarSaveCuandoLaCuentaOrigenNoEstaActiva() {
        // Arrange
        String cuentaOrigen = "00123456789";
        TransferRequest request = crearRequest(cuentaOrigen, new BigDecimal("100.00"));
        when(accountRepository.findByAccountNumber(cuentaOrigen))
                .thenReturn(Optional.of(crearCuenta(cuentaOrigen, "BLOCKED")));

        // Act
        assertThrows(
                IllegalStateException.class,
                () -> transferServiceImpl.validateAndTransfer(request)
        );

        // Assert
        verifyNoInteractions(transferRepository);
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @ParameterizedTest
    @CsvSource({"0", "-1", "-100.50"})
    void noDeberiaInvocarSaveCuandoElMontoNoEsMayorQueCero(BigDecimal monto) {
        // Arrange
        String cuentaOrigen = "00123456789";
        TransferRequest request = crearRequest(cuentaOrigen, monto);
        when(accountRepository.findByAccountNumber(cuentaOrigen))
                .thenReturn(Optional.of(crearCuenta(cuentaOrigen, "ACTIVE")));

        // Act
        assertThrows(
                IllegalArgumentException.class,
                () -> transferServiceImpl.validateAndTransfer(request)
        );

        // Assert
        verifyNoInteractions(transferRepository);
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    void noDeberiaInvocarSaveCuandoLaSumaDeTransferenciasDelDiaSuperaElLimite() {
        // Arrange
        String cuentaOrigen = "00123456789";
        TransferRequest request = crearRequest(cuentaOrigen, new BigDecimal("100.00"));
        configurarCuentaYTotal(cuentaOrigen, new BigDecimal("9900.01"));

        // Act
        assertThrows(
                IllegalArgumentException.class,
                () -> transferServiceImpl.validateAndTransfer(request)
        );

        // Assert
        verifyNoInteractions(transferMapper);
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    private Account crearCuenta(String cuentaOrigen, String estado) {
        return Account.builder()
                .id(1L)
                .accountNumber(cuentaOrigen)
                .customerId("CUS-001")
                .status(estado)
                .build();
    }

    private TransferRequest crearRequest(String cuentaOrigen, BigDecimal monto) {
        return TransferRequest.builder()
                .customerId("CUS-001")
                .sourceAccount(cuentaOrigen)
                .targetAccount("00987654321")
                .amount(monto)
                .build();
    }

    private TransferResponse crearRespuestaAprobada(String cuentaOrigen, BigDecimal monto) {
        return TransferResponse.builder()
                .status("APPROVED")
                .sourceAccount(cuentaOrigen)
                .targetAccount("00987654321")
                .amount(monto)
                .build();
    }

    private void configurarCuentaYTotal(String cuentaOrigen, BigDecimal total) {
        when(accountRepository.findByAccountNumber(cuentaOrigen))
                .thenReturn(Optional.of(crearCuenta(cuentaOrigen, "ACTIVE")));
        when(transferRepository.totalTransferredToday(
                eq(cuentaOrigen), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(total);
    }
}
