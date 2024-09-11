package com.erivan.gtmanager.controller;

import com.erivan.gtmanager.dto.UserManagementResult;
import com.erivan.gtmanager.error.TaskNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<UserManagementResult> handleTaskNotFound(TaskNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new UserManagementResult(e.getMessage())); 
    }
    
}
