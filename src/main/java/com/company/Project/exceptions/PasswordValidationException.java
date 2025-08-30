package com.company.Project.exceptions;

public class PasswordValidationException extends  RuntimeException{
    public PasswordValidationException(String message) {
        super(message);
    }
}
