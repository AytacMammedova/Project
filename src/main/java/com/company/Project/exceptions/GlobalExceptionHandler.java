package com.company.Project.exceptions;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return new ResponseEntity<>(errors, httpStatus);
    }


}
