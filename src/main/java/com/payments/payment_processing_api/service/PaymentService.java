package com.payments.payment_processing_api.service;

import com.payments.payment_processing_api.dto.PaymentRequest;
import com.payments.payment_processing_api.dto.PaymentResponse;
import com.payments.payment_processing_api.exception.AccountNotFoundException;
import com.payments.payment_processing_api.exception.InsufficientFundsException;
import com.payments.payment_processing_api.model.Account;
import com.payments.payment_processing_api.model.Payment;
import com.payments.payment_processing_api.model.Payment.PaymentStatus;
import com.payments.payment_processing_api.repository.AccountRepository;
import com.payments.payment_processing_api.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {

        // Idempotency check
        if (paymentRepository.existsByPaymentRef(request.getPaymentRef())) {
            throw new IllegalArgumentException("Payment with this reference already exists: " + request.getPaymentRef());
        }

        Account sender = accountRepository.findByAccountNumber(request.getSenderAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(request.getSenderAccountNumber()));

        Account receiver = accountRepository.findByAccountNumber(request.getReceiverAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(request.getReceiverAccountNumber()));

        Payment payment = new Payment();
        payment.setPaymentRef(request.getPaymentRef());
        payment.setSenderAccountNumber(request.getSenderAccountNumber());
        payment.setReceiverAccountNumber(request.getReceiverAccountNumber());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setStatus(PaymentStatus.PROCESSING);

        // Insufficient funds check
        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Insufficient funds");
            paymentRepository.save(payment);
            throw new InsufficientFundsException(request.getSenderAccountNumber());
        }

        // Debit sender, credit receiver
        sender.setBalance(sender.getBalance().subtract(request.getAmount()));
        receiver.setBalance(receiver.getBalance().add(request.getAmount()));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCompletedAt(LocalDateTime.now());

        return toResponse(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponse refundPayment(String paymentRef) {
        Payment original = paymentRepository.findByPaymentRef(paymentRef)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentRef));

        if (original.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalArgumentException("Only completed payments can be refunded");
        }

        Account sender = accountRepository.findByAccountNumber(original.getSenderAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(original.getSenderAccountNumber()));

        Account receiver = accountRepository.findByAccountNumber(original.getReceiverAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(original.getReceiverAccountNumber()));

        // Reverse the transaction
        receiver.setBalance(receiver.getBalance().subtract(original.getAmount()));
        sender.setBalance(sender.getBalance().add(original.getAmount()));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        original.setStatus(PaymentStatus.REFUNDED);
        original.setCompletedAt(LocalDateTime.now());

        return toResponse(paymentRepository.save(original));
    }

    public List<PaymentResponse> getPaymentsByAccount(String accountNumber) {
        List<Payment> sent = paymentRepository.findBySenderAccountNumber(accountNumber);
        List<Payment> received = paymentRepository.findByReceiverAccountNumber(accountNumber);
        sent.addAll(received);
        return sent.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setPaymentRef(payment.getPaymentRef());
        response.setSenderAccountNumber(payment.getSenderAccountNumber());
        response.setReceiverAccountNumber(payment.getReceiverAccountNumber());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setStatus(payment.getStatus());
        response.setFailureReason(payment.getFailureReason());
        response.setCreatedAt(payment.getCreatedAt());
        response.setCompletedAt(payment.getCompletedAt());
        return response;
    }
}