package com.payments.payment_processing_api.repository;

import com.payments.payment_processing_api.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findBySenderAccountNumber(String accountNumber);
    List<Payment> findByReceiverAccountNumber(String accountNumber);
    Optional<Payment> findByPaymentRef(String paymentRef);
    boolean existsByPaymentRef(String paymentRef);
}