package com.demo.example.mstransfercontrol.repository;

import com.demo.example.mstransfercontrol.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Integer> {

    @Query("""
       SELECT COALESCE(SUM(t.amount), 0)
       FROM Transfer t
       WHERE t.accountNumber = :accountNumber
         AND t.status = 'successful'
         AND t.transferDate >= :startDate
         AND t.transferDate < :endDate
       """)
    BigDecimal totalTransferredToday(
            @Param("accountNumber") String accountNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
