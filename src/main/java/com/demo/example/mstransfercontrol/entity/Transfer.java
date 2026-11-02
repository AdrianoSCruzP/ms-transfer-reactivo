package com.demo.example.mstransfercontrol.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_destination")
    private String accountDestination;

    @Column(name = "customer_id")
    private String customerId;

    private String status; // successful | rejected

    private BigDecimal amount;

    @Column(name = "transfer_date")
    private LocalDateTime transferDate;

    @PrePersist
    public void prePersist() {
        if (transferDate == null) {
            transferDate = LocalDateTime.now();
        }
    }

}
