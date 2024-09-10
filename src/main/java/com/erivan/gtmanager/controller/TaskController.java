package com.erivan.gtmanager.controller;

import com.erivan.gtmanager.dto.TaskDTO;
import com.erivan.gtmanager.error.TaskNotFoundException;
import com.erivan.gtmanager.security.UserAuth;
import com.erivan.gtmanager.service.TaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final Logger logger = LogManager.getLogger();

    @Autowired
    private TaskService taskService;

    // Create a new task
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO task) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAuth authUser = (UserAuth) authentication.getPrincipal();

        task = taskService.createTask(authUser.id(), task);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    // Get all tasks for the authenticated user
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAuth authUser = (UserAuth) authentication.getPrincipal();
        List<TaskDTO> tasks = taskService.getAllTasksFromUserId(authUser.id());
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAuth authUser = (UserAuth) authentication.getPrincipal();

        TaskDTO task = taskService.getTaskById(authUser.id(), id);
        if (task != null) {
            return new ResponseEntity<>(task, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Update a specific task by ID
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable String id, @RequestBody TaskDTO task) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAuth authUser = (UserAuth) authentication.getPrincipal();

        try {
            TaskDTO updatedTask = taskService.updateTask(authUser.id(), id, task);
            return ResponseEntity.ok(updatedTask);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
/*
    // Delete a specific task by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }*/
}

