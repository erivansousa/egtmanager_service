package com.erivan.gtmanager.service;

import com.erivan.gtmanager.data.TaskRepository;
import com.erivan.gtmanager.data.TaskStatus;
import com.erivan.gtmanager.data.entity.Task;
import com.erivan.gtmanager.dto.TaskDTO;
import com.erivan.gtmanager.error.TaskNotFoundException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    TaskRepository taskRepository;

    public List<TaskDTO> getAllTasksFromUserId(String userId) {
        var tasks = taskRepository.findAllByUserId(userId);

        List<TaskDTO> dtoTasks = new ArrayList<TaskDTO>(List.of());
        tasks.forEach((task) -> {
            dtoTasks.add(
                    new TaskDTO(
                            task.getId(),
                            task.getTitle(),
                            task.getDescription(),
                            task.getDueDate(),
                            task.getStatus().name()
                    )
            );
        });
        return dtoTasks;
    }

    public TaskDTO createTask(String userId, TaskDTO taskDto) {
        Task task = new Task(
                new ObjectId().toHexString(),
                taskDto.title(),
                taskDto.description(),
                userId,
                LocalDateTime.now(),
                taskDto.dueDate(),
                TaskStatus.valueOf(taskDto.status())
        );
        var result = taskRepository.save(task);
        return TaskDTO.fromTask(result);
    }

    public TaskDTO getTaskById(String userId, String taskId) {
        var result = taskRepository.findByIdAndUserId(taskId, userId);
        return result.map(TaskDTO::fromTask).orElse(null);
    }

    public TaskDTO updateTask(String userId, String taskId, TaskDTO task) {
        //check task on database
        var repoTask = taskRepository.findByIdAndUserId(taskId, userId);
        if (repoTask.isEmpty()) {
            throw new TaskNotFoundException();
        }

        //update task attibutes
        var newTask = repoTask.map(t -> {
            t.setTitle(task.title());
            t.setDescription(task.description());
            t.setDueDate(task.dueDate());
            t.setStatus(TaskStatus.valueOf(task.status()));
            return t;
        }).orElse(repoTask.get());

        taskRepository.save(newTask);

        return TaskDTO.fromTask(newTask);
    }
}
