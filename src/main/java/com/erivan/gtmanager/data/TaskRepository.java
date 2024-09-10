package com.erivan.gtmanager.data;

import com.erivan.gtmanager.data.entity.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findAllByUserId(String userId);
    Optional<Task> findByIdAndUserId(String id, String userId);
    boolean existsByIdAndUserId(String id, String userId);
}
