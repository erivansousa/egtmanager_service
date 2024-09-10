package com.erivan.gtmanager.dto;

import com.erivan.gtmanager.data.entity.Task;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TaskDTO(
        String id,
        String title,
        String description,
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        LocalDateTime dueDate,
        String status
) {

    public static TaskDTO fromTask(Task task){
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus().name()
        );
    }
}
