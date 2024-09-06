package com.erivan.gtmanager.data.entity;

import com.erivan.gtmanager.data.TaskStatus;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "tasks")
public class Task {

    @BsonId()
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String id;
    private String title;
    private String description;
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private TaskStatus status;

    // Constructor
    public Task(String id, String title, String description, String userId, LocalDateTime createdAt, LocalDateTime dueDate, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.createdAt = createdAt;
        this.dueDate = dueDate;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

}
