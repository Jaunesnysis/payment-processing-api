package com.payments.payment_processing_api.controller;

import com.payments.payment_processing_api.dto.PaymentRequest;
import com.payments.payment_processing_api.dto.PaymentResponse;
import com.payments.payment_processing_api.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.initiatePayment(request));
    }

    @PostMapping("/{paymentRef}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable String paymentRef) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentRef));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(paymentService.getPaymentsByAccount(accountNumber));
    }
}