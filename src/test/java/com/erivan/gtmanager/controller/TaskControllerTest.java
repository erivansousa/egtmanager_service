package com.erivan.gtmanager.controller;

import com.erivan.gtmanager.dto.TaskDTO;
import com.erivan.gtmanager.security.UserAuth;
import com.erivan.gtmanager.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @Test
    @DisplayName("when receive a get all tasks request should call the task service once and return its result")
    void whenRequestAllTasksFromUserIdShouldCallServiceReturnItsResult() {
        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // Mock task service response
        List<TaskDTO> tasks = List.of(
                new TaskDTO("1", "Task 1", "Description 1", LocalDateTime.now(), "TODO"),
                new TaskDTO("2", "Task 2", "Description 2", LocalDateTime.now(), "IN_PROGRESS")
        );

        // Stub mocks
        when(authentication.getPrincipal()).thenReturn(new UserAuth("12334", "blabla", "t"));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(taskService.getAllTasksFromUserId("12334")).thenReturn(tasks);

        // Call the controller method
        ResponseEntity<List<TaskDTO>> response = taskController.getAllTasks();

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
        verify(taskService, times(1)).getAllTasksFromUserId("12334");
    }

    @Test
    @DisplayName("when receive a get all tasks request should call the task service once and return its result")
    void createTask() {
        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // Stub mocks
        when(authentication.getPrincipal()).thenReturn(new UserAuth("12334", "blabla", "t"));
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Mock task service response
        TaskDTO task = new TaskDTO("1", "Task 1", "Description 1", LocalDateTime.now().plusDays(7), "TODO");
        when(taskService.createTask(eq("12334"), any(TaskDTO.class))).thenReturn(task);

        // Call the controller method
        ResponseEntity<TaskDTO> response = taskController.createTask(task);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(task, response.getBody());
        verify(taskService, times(1)).createTask("12334", task);
    }

    @Test
    @DisplayName("when receive a get task by id request, should call the task service once and return its result")
    void getTaskById() {
        // Mock
        TaskDTO task = new TaskDTO("1", "Task 1", "Description 1", LocalDateTime.now().plusDays(7), "TODO");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        //stub
        when(authentication.getPrincipal()).thenReturn(new UserAuth("12334", "blabla", "t"));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(taskService.getTaskById("12334", "1")).thenReturn(task);

        // Call the controller method
        ResponseEntity<TaskDTO> response = taskController.getTaskById("1");

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task, response.getBody());
        verify(taskService, times(1)).getTaskById("12334", "1");
    }

    @Test
    @DisplayName("when receive a get task by id request and could not find the task, should return http status 404")
    void getTaskByIdCanNotFindTask() {
        // Mock
        TaskDTO task = new TaskDTO("1", "Task 1", "Description 1", LocalDateTime.now().plusDays(7), "TODO");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        //stub
        when(taskService.getTaskById("12334", "1")).thenReturn(null);
        when(authentication.getPrincipal()).thenReturn(new UserAuth("12334", "blabla", "t"));
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Call the controller method
        ResponseEntity<TaskDTO> response = taskController.getTaskById("1");

        // Verify the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskService, times(1)).getTaskById("12334", "1");
    }

}
