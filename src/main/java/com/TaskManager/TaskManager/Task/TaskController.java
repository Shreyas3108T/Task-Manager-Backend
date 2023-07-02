package com.TaskManager.TaskManager.Task;

import com.TaskManager.TaskManager.Project.Project;
import com.TaskManager.TaskManager.Project.ProjectService;
import com.TaskManager.TaskManager.Response.Response;
import com.TaskManager.TaskManager.User.UserService;
import com.TaskManager.TaskManager.User.Users;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(path = "/v1")
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;
    private final ProjectService projectService;

    @Autowired
    public TaskController(TaskService taskService,UserService userService,ProjectService projectService){
        this.taskService = taskService;
        this.userService = userService;
        this.projectService = projectService;
    }
    @PostMapping(path = "/Task")
    public ResponseEntity<Response> CreateTask(@RequestBody Task task, HttpServletRequest request) throws ParseException, JOSEException {
        String BearerToken = request.getHeader("Authorization");
        String Subject = userService.verifyJWT(BearerToken);
        if(Subject != "No Token" && Subject != "null"){
            Optional<Users> user = userService.findUserByEmail(Subject);
            Users UserInfo = user.orElse(null);
            Optional<Project> project = projectService.findProjectById(task.getProjectId());
            Project projectInfo = project.orElse(null);
            if(Objects.equals(projectInfo.getOwner(), UserInfo.getId())){
                task.setStatus(TaskStatus.CREATED);
                taskService.createTask(task);
                Response<Task> response = new Response<>(200,true,"Task Created Successfully",task);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }
        Response<String> response = new Response<>(401,false,"Unable to create Task","Not authorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @GetMapping(path="/Task")
    public ResponseEntity<Response> GetAllTasks(HttpServletRequest request,@RequestParam String ProjectId) throws ParseException, JOSEException {
        String BearerToken = request.getHeader("Authorization");
        String Subject = userService.verifyJWT(BearerToken);
        if(Subject != "No Token" && Subject != "null") {
            Optional<Users> user = userService.findUserByEmail(Subject);
            Users UserInfo = user.orElse(null);
            Optional<Project> project = projectService.findProjectById(ProjectId);
            if(project.isPresent()){
                Project projectInfo = project.get();
                if(projectInfo.getUsers().contains(UserInfo.getId()) || Objects.equals(UserInfo.getId(),projectInfo.getOwner())){
                    List<Task> tasks = taskService.GetTasksByProjectId(ProjectId);
                    Response<List<Task>> response = new Response<>(200,true,"List of all the tasks in a project",tasks);
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }

            }
        }
        Response<String> response = new Response<>(401,false,"Access issue","Not authorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    @PostMapping(path="/assignTask")
    public ResponseEntity<Response>  AssignTask(HttpServletRequest request,@RequestBody Map<String,String> requestBody) throws ParseException, JOSEException {
        String TaskId = requestBody.get("TaskId");
        Long UserId =  Long.parseLong(requestBody.get("UserId"));
        //here add a way to check if all the required body is given
        String BearerToken = request.getHeader("Authorization");
        String Subject = userService.verifyJWT(BearerToken);
        if(Subject != "No Token" && Subject != "null"){
            Optional<Users> user = userService.findUserByEmail(Subject);
            Users UserInfo = user.orElse(null);
            Optional<Task> task =  taskService.GetTaskById(TaskId);
            Optional<Users> AssigningUser = userService.findUserById(UserId);
            if(task.isPresent() && AssigningUser.isPresent()){
                Task TaskInfo =  task.get();
                Users assignedUserInfo = AssigningUser.get();
                String ProjectId = TaskInfo.getProjectId();
                Optional<Project> project = projectService.findProjectById(ProjectId);
                if(project.isPresent()){
                    Project projectInfo = project.get();
                    if(Objects.equals(projectInfo.getOwner(),UserInfo.getId()) && (projectInfo.getUsers().contains(assignedUserInfo.getId()) || Objects.equals(projectInfo.getOwner(),assignedUserInfo.getId()))){
                        TaskInfo.setAssignedUser(assignedUserInfo.getId());
                        taskService.AssignUser(TaskInfo);
                        Response<Task> response = new Response<>(200,true,"Task Assigned",TaskInfo);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                }
            }

        }
        Response<String> response = new Response<>(200,true,"Access issue","Access issue");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/TaskStatusUpdate")
    public ResponseEntity<Response> UpdateTaskStatus(HttpServletRequest request,@RequestBody Map<String,String> requestBody) throws ParseException, JOSEException {
        String TaskId = requestBody.get("TaskId");
        String Status = requestBody.get("Status");
        TaskStatus status = TaskStatus.valueOf(Status.toUpperCase());
        Optional<Task> task = taskService.GetTaskById(TaskId);
        String BearerToken = request.getHeader("Authorization");
        String Subject = userService.verifyJWT(BearerToken);
        if(Subject != "No Token" && Subject != "null"){
            Optional<Users> user = userService.findUserByEmail(Subject);
            Users UserInfo = user.orElse(null);
            if(task.isPresent()){
                Task taskinfo = task.get();
                if(Objects.equals(taskinfo.getAssignedUser(),UserInfo.getId())){
                    taskinfo.setStatus(status);
                    taskService.StatusUpdate(taskinfo);
                    Response<Task> response = new Response<>(200,true,"Status Update Success",taskinfo);
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
            }
        }
        Response<String> response = new Response<>(401,false,"UnAuthorized","Validation Fail");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @GetMapping(path = "/assignedTask")
    public ResponseEntity<Response> GetAllTaskAssignedToUser(HttpServletRequest request) throws ParseException, JOSEException {
        String BearerToken = request.getHeader("Authorization");
        String Subject = userService.verifyJWT(BearerToken);
        if(Subject != "No Token" && Subject != "null") {
            Optional<Users> user = userService.findUserByEmail(Subject);
            Users UserInfo = user.orElse(null);
            List<Task> tasks = taskService.GetAllAssignedTask(UserInfo.getId());
            Response<List<Task>> response = new Response<>(200,true,"List of task assgined",tasks);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        Response<String> response = new Response<>(401,false,"UnAuthorized","Validation Fail");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @GetMapping(path="/Task/{id}")
    public ResponseEntity<Response> GetTaskById(HttpServletRequest request,@PathVariable("id") String TaskId) throws ParseException, JOSEException {
        String BearerToken = request.getHeader("Authorization");
        String Subject = userService.verifyJWT(BearerToken);
        if(Subject == "No Token" && Subject != "null"){
            Response<String> response = new Response<>(201,false,"Token issue","Token issue");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        Optional<Users> user = userService.findUserByEmail(Subject);
        if(!user.isPresent()){
            Response<String> response = new Response<>(404,false,"No user found","No user");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Users userinfo = user.get();
        Optional<Task> task = taskService.GetTaskById(TaskId);
        if(!(task.isPresent())){
            Response<String> response = new Response<>(404,false,"No Task Found","No task");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Task taskinfo = task.get();
        Project project = projectService.findProjectById(taskinfo.getProjectId()).get();
        if(project.getUsers().contains(userinfo.getId()) || Objects.equals(project.getOwner(),userinfo.getId())){
            Response<Task> response = new Response<>(200,true,"Here is the project info",taskinfo);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        Response<String> response = new Response<>(401,false,"Not Access to the resource","No task");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

    }

    @DeleteMapping(path = "/Task/{id}")
    public ResponseEntity<Response> DeleteTask(HttpServletRequest request,@PathVariable("id") String TaskId){
       Response<String> response =  taskService.deleteTask(TaskId,(Users) request.getAttribute("AuthUser"));
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
