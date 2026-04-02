package com.sofkau.exception;

public class ContractNotBelongToEmployeeException extends RuntimeException {
    public ContractNotBelongToEmployeeException(String message) {
        super(message);
    }
}
