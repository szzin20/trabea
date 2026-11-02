package com.exacc.trabea.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessRuleException extends RuntimeException {
    private final HttpStatus status;

    public BusinessRuleException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
