package com.TaskManager.TaskManager.Task;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task,String> {
    List<Task> findAllByProjectId(String id);
    List<Task> findAllByAssignedUser(Long id);
    void deleteAllByProjectId(String projectId);

    void deleteTaskById(String TaskId);
}
