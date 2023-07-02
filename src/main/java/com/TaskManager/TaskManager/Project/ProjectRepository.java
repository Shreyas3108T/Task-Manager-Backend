package com.TaskManager.TaskManager.Project;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project,String> {
    List<Project> findByOwner(Long ownerId);
    List<Project> findByUsersContaining(Long id);
    Optional<Project> findProjectByNameAndOwner(String name, Long owner);
    void deleteProjectById(String id);
}
