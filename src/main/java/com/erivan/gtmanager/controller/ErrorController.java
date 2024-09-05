package com.erivan.gtmanager.controller;

import com.erivan.gtmanager.dto.UserManagementResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<UserManagementResult> defaultErrorController(ResponseStatusException e, HttpServletRequest request){
        return ResponseEntity.status(e.getStatusCode()).body(new UserManagementResult(e.getReason()));
    }
}
