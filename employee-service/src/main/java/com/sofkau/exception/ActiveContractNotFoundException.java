package com.sofkau.exception;

public class ActiveContractNotFoundException extends RuntimeException {
    public ActiveContractNotFoundException(String message) {
        super(message);
    }
}
