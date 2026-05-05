package com.payments.payment_processing_api.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String accountNumber) {
        super("Insufficient funds in account: " + accountNumber);
    }
}