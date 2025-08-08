package com.company.Project.exceptions;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import jakarta.validation.ConstraintViolation;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {AlreadyExistsException.class})
    public ResponseEntity<Object> handleAlreadyExistsException(AlreadyExistsException e){
        HttpStatus httpStatus=HttpStatus.CONFLICT;
        ErrorResponse errorResponse=new ErrorResponse(e.getMessage(),httpStatus,LocalDateTime.now());
        return new ResponseEntity<>(errorResponse,httpStatus);
    }
    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e){
        HttpStatus httpStatus=HttpStatus.NOT_FOUND;
        ErrorResponse response = new ErrorResponse(e.getMessage(), httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(response, httpStatus);
    }
    @ExceptionHandler(value = {AddressOwnershipException.class})
    public ResponseEntity<Object> handleAddressOwnershipException(AddressOwnershipException e){
        HttpStatus httpStatus=HttpStatus.FORBIDDEN;
        ErrorResponse response = new ErrorResponse(e.getMessage(), httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(response, httpStatus);
    }
    @ExceptionHandler(value = {OutOfStockException.class})
    public ResponseEntity<Object> handleOutOfStockException(OutOfStockException e){
        HttpStatus httpStatus=HttpStatus.CONFLICT;
        ErrorResponse response = new ErrorResponse(e.getMessage(), httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(response, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        System.out.println("=== MethodArgumentNotValidException CAUGHT! ===");
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        System.out.println("Validation errors: " + errors);
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // Handle ConstraintViolationException (for entity-level validation)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, List<String>>> handleConstraintViolationException(ConstraintViolationException ex) {
        System.out.println("=== ConstraintViolationException CAUGHT! ===");
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        System.out.println("Constraint violation errors: " + errors);
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }




}
