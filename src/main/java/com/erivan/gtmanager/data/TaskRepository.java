package com.erivan.gtmanager.data;

import com.erivan.gtmanager.data.entity.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, String> {
    Task findByUserId(String userId);
}
