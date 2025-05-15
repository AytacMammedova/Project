package com.company.Project.exceptions;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
@RequiredArgsConstructor
@Data
public class ErrorResponse {
    private final String message;
    private final HttpStatus httpStatus;
    private final LocalDateTime localDateTime;
}
