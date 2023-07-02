package com.TaskManager.TaskManager.Task;

import com.TaskManager.TaskManager.Project.Project;
import com.TaskManager.TaskManager.Project.ProjectService;
import com.TaskManager.TaskManager.Response.Response;
import com.TaskManager.TaskManager.User.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectService projectService;

    @Autowired
    @Lazy
    public TaskService(TaskRepository taskRepository,ProjectService projectService){
        this.taskRepository = taskRepository;
        this.projectService = projectService;
    }
    public void createTask(Task task){
        taskRepository.save(task);
    }
    public List<Task> GetTasksByProjectId(String id){
        List<Task> tasks = taskRepository.findAllByProjectId(id);
        return tasks;
    }

    public Optional<Task> GetTaskById(String Id){
        Optional<Task> task = taskRepository.findById(Id);
        return task;
    }
    public void AssignUser(Task task){
        taskRepository.save(task);
    }
    public void StatusUpdate(Task task){
        taskRepository.save(task);
    }
    public List<Task> GetAllAssignedTask(Long id){
        return taskRepository.findAllByAssignedUser(id);
    }

    public void deleteAllByProjectId(String ProjectId){
        taskRepository.deleteAllByProjectId(ProjectId);
    }

    public void deleteTaskById(String TaskId){
        taskRepository.deleteTaskById(TaskId);
    }

    public Response<String> deleteTask(String TaskId, Users user){
        Optional<Task> taskOptional = GetTaskById(TaskId);
        if(taskOptional.isEmpty()){
            return new Response<>(HttpStatus.NOT_FOUND.value(), false,"No Task Found","Task Not Found");
        }
        Task task = taskOptional.get();
        String ProjectId = task.getProjectId();
        Project project = projectService.findProjectById(ProjectId).get();
        if(!projectService.CheckOwnerOfProject(user,project)){
            return new Response<>(HttpStatus.UNAUTHORIZED.value(), false,"Not authorized to delete the project","Unauthorized");
        }
        deleteTaskById(TaskId);
        return new Response<>(HttpStatus.OK.value(), true,"Task Deleted Succesfully","Successfully");
    }
}
