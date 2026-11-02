package com.demo.example.mstransfercontrol.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    private Long id;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "customer_id")
    private String customerId;

    private String status; // ACTIVE | BLOCKED
}

