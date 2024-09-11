package com.erivan.gtmanager.service;

import com.erivan.gtmanager.data.TaskRepository;
import com.erivan.gtmanager.data.TaskStatus;
import com.erivan.gtmanager.data.entity.Task;
import com.erivan.gtmanager.dto.TaskDTO;
import com.erivan.gtmanager.error.TaskNotFoundException;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getAllTasksFromUserIdShouldReturnEmptyListWhenNoTasksFound() {
        String userId = "testUserId";
        when(taskRepository.findAllByUserId(userId)).thenReturn(List.of());

        List<TaskDTO> result = taskService.getAllTasksFromUserId(userId);

        assertEquals(0, result.size());
    }

    @Test
    void getAllTasksFromUserIdShouldReturnListOfTaskDTOsWhenTasksFound() {
        String userId = "testUserId";
        Task task1 = new Task(new ObjectId().toHexString(), "Task 1", "Description 1", userId, LocalDateTime.now(), LocalDateTime.now(), TaskStatus.TODO);
        Task task2 = new Task(new ObjectId().toHexString(), "Task 2", "Description 2", userId, LocalDateTime.now(), LocalDateTime.now(), TaskStatus.IN_PROGRESS);
        when(taskRepository.findAllByUserId(userId)).thenReturn(List.of(task1, task2));

        List<TaskDTO> result = taskService.getAllTasksFromUserId(userId);

        assertEquals(2, result.size());
    }

    @Test
    void getAllTasksFromUserIdShouldReturnTaskDTOsWithCorrectData() {
        String userId = "testUserId";
        Task task = new Task(new ObjectId().toHexString(), "Task 1", "Description 1", userId, LocalDateTime.now(), LocalDateTime.now(), TaskStatus.TODO);
        when(taskRepository.findAllByUserId(userId)).thenReturn(List.of(task));

        List<TaskDTO> result = taskService.getAllTasksFromUserId(userId);

        assertEquals("Task 1", result.get(0).title());
        assertEquals("Description 1", result.get(0).description());
        assertEquals(TaskStatus.TODO.name(), result.get(0).status());
    }

    @Test
    void createTaskShouldReturnTaskDTOWithCorrectTitle() {
        String userId = "testUserId";
        TaskDTO taskDto = new TaskDTO(null, "Test Task", "Test Description", LocalDateTime.now(), TaskStatus.TODO.name());
        Task savedTask = new Task(new ObjectId().toHexString(), "Test Task", "Test Description", userId, LocalDateTime.now(), LocalDateTime.now(), TaskStatus.TODO);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskDTO result = taskService.createTask(userId, taskDto);

        assertEquals("Test Task", result.title());
    }

    @Test
    void createTaskShouldReturnTaskDTOWithCorrectDescription() {
        String userId = "testUserId";
        TaskDTO taskDto = new TaskDTO(null, "Test Task", "Test Description", LocalDateTime.now(), TaskStatus.TODO.name());
        Task savedTask = new Task(new ObjectId().toHexString(), "Test Task", "Test Description", userId, LocalDateTime.now(), LocalDateTime.now(), TaskStatus.TODO);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskDTO result = taskService.createTask(userId, taskDto);

        assertEquals("Test Description", result.description());
    }

    @Test
    void createTaskShouldReturnTaskDTOWithCorrectStatus() {
        String userId = "testUserId";
        TaskDTO taskDto = new TaskDTO(null, "Test Task", "Test Description", LocalDateTime.now(), TaskStatus.TODO.name());
        Task savedTask = new Task(new ObjectId().toHexString(), "Test Task", "Test Description", userId, LocalDateTime.now(), LocalDateTime.now(), TaskStatus.TODO);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskDTO result = taskService.createTask(userId, taskDto);

        assertEquals(TaskStatus.TODO.name(), result.status());
    }

    @Test
    void createTaskShouldReturnTaskDTOWithCorrectDueDate() {
        String userId = "testUserId";
        LocalDateTime dueDate = LocalDateTime.now();
        TaskDTO taskDto = new TaskDTO(null, "Test Task", "Test Description", dueDate, TaskStatus.TODO.name());
        Task savedTask = new Task(new ObjectId().toHexString(), "Test Task", "Test Description", userId, LocalDateTime.now(), dueDate, TaskStatus.TODO);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskDTO result = taskService.createTask(userId, taskDto);

        assertEquals(dueDate, result.dueDate());
    }

    @Test
    void getTaskByIdShouldReturnTaskDTOWhenTaskFound() {
        String userId = "testUserId";
        String taskId = "testTaskId";
        Task task = new Task(taskId, "Task 1", "Description 1", userId, LocalDateTime.now(), LocalDateTime.now(), TaskStatus.TODO);
        when(taskRepository.findByIdAndUserId(taskId, userId)).thenReturn(Optional.of(task));

        TaskDTO result = taskService.getTaskById(userId, taskId);

        assertEquals("Task 1", result.title());
    }

    @Test
    void getTaskByIdShouldThrowTaskNotFoundExceptionWhenTaskNotFound() {
        String userId = "testUserId";
        String taskId = "nonExistentTaskId";
        when(taskRepository.findByIdAndUserId(taskId, userId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(userId, taskId));
    }

    @Test
    void updateTaskShouldThrowTaskNotFoundExceptionWhenTaskNotFound() {
        String userId = "testUserId";
        String taskId = "nonExistentTaskId";
        TaskDTO taskDto = new TaskDTO(taskId, "Updated Task", "Updated Description", LocalDateTime.now(), TaskStatus.IN_PROGRESS.name());
        when(taskRepository.findByIdAndUserId(taskId, userId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(userId, taskId, taskDto));
    }

    @Test
    void updateTaskShouldUpdateTaskTitleCorrectly() {
        String userId = "testUserId";
        String taskId = "testTaskId";
        Task existingTask = new Task(taskId, "Task 1", "Description 1", userId, LocalDateTime.now(), LocalDateTime.now(), TaskStatus.TODO);
        TaskDTO taskDto = new TaskDTO(taskId, "Updated Task", "Updated Description", LocalDateTime.now(), TaskStatus.IN_PROGRESS.name());
        when(taskRepository.findByIdAndUserId(taskId, userId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        TaskDTO result = taskService.updateTask(userId, taskId, taskDto);

        assertEquals("Updated Task", result.title());
    }

    @Test
    void updateTaskShouldUpdateTaskDescriptionCorrectly() {
        String userId = "testUserId";
        String taskId = "testTaskId";
        Task existingTask = new Task(taskId, "Task 1", "Description 1", userId, LocalDateTime.now(), LocalDateTime.now(), TaskStatus.TODO);
        TaskDTO taskDto = new TaskDTO(taskId, "Updated Task", "Updated Description", LocalDateTime.now(), TaskStatus.IN_PROGRESS.name());
        when(taskRepository.findByIdAndUserId(taskId, userId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        TaskDTO result = taskService.updateTask(userId, taskId, taskDto);

        assertEquals("Updated Description", result.description());
    }

    @Test
    void updateTaskShouldUpdateTaskDueDateCorrectly() {
        String userId = "testUserId";
        String taskId = "testTaskId";
        LocalDateTime updatedDueDate = LocalDateTime.now().plusDays(1);
        Task existingTask = new Task(taskId, "Task 1", "Description 1", userId, LocalDateTime.now(), LocalDateTime.now(), TaskStatus.TODO);
        TaskDTO taskDto = new TaskDTO(taskId, "Updated Task", "Updated Description", updatedDueDate, TaskStatus.IN_PROGRESS.name());
        when(taskRepository.findByIdAndUserId(taskId, userId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        TaskDTO result = taskService.updateTask(userId, taskId, taskDto);

        assertEquals(updatedDueDate, result.dueDate());
    }

    @Test
    void updateTaskShouldUpdateTaskStatusCorrectly() {
        String userId = "testUserId";
        String taskId = "testTaskId";
        Task existingTask = new Task(taskId, "Task 1", "Description 1", userId, LocalDateTime.now(), LocalDateTime.now(), TaskStatus.TODO);
        TaskDTO taskDto = new TaskDTO(taskId, "Updated Task", "Updated Description", LocalDateTime.now(), TaskStatus.IN_PROGRESS.name());
        when(taskRepository.findByIdAndUserId(taskId, userId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        TaskDTO result = taskService.updateTask(userId, taskId, taskDto);

        assertEquals(TaskStatus.IN_PROGRESS.name(), result.status());
    }

    @Test
    void deleteTaskShouldCallRepositoryDeleteByIdAndUserId() {
        String userId = "testUserId";
        String taskId = "testTaskId";

        taskService.deleteTask(userId, taskId);

        verify(taskRepository).deleteByIdAndUserId(taskId, userId);
    }
}
