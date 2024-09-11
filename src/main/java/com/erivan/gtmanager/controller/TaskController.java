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
        var authUser = getAuthenticatedUser();

        task = taskService.createTask(authUser.id(), task);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    // Get all tasks for the authenticated user
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        var authUser = getAuthenticatedUser();
        List<TaskDTO> tasks = taskService.getAllTasksFromUserId(authUser.id());
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable String taskId) {
        var authUser = getAuthenticatedUser();

        var task = taskService.getTaskById(authUser.id(), taskId);
        return ResponseEntity.ok(task);        
    }

    // Update a specific task by ID
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable String taskId, @RequestBody TaskDTO task) {
        var authUser = getAuthenticatedUser();

        var updatedTask = taskService.updateTask(authUser.id(), taskId, task);
        return ResponseEntity.ok(updatedTask); 
    }

    // Delete a specific task by ID
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        var authUser = getAuthenticatedUser();

        taskService.deleteTask(authUser.id(), taskId);
        return ResponseEntity.ok().build();
    }

    private UserAuth getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserAuth) authentication.getPrincipal();
    }
    
}

