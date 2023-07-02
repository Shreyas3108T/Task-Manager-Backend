package com.TaskManager.TaskManager.Project;

import com.TaskManager.TaskManager.RequestTypes.AddToProjectRequest;
import com.TaskManager.TaskManager.Response.Response;
import com.TaskManager.TaskManager.Task.TaskService;
import com.TaskManager.TaskManager.User.UserService;
import com.TaskManager.TaskManager.User.Users;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final TaskService taskService;

    @Autowired
    public ProjectService(ProjectRepository projectRepository,UserService userService,TaskService taskService){
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.taskService = taskService;
    }

    public void SaveProject(Project project){
        projectRepository.save(project);
    }

    public Response CreateNewProject(Project project,Users user){
        if(projectRepository.findProjectByNameAndOwner(project.getName(),user.getId()).isPresent()){
            System.out.println(projectRepository.findProjectByNameAndOwner(project.getName(),user.getId()));
            return new Response<>(HttpStatus.NOT_ACCEPTABLE.value(), false,"The Project Already exists",HttpStatus.NOT_ACCEPTABLE);
        }
        project.setOwner(user.getId());
        SaveProject(project);
        return new Response<>(HttpStatus.OK.value(), true,"The Project Created",HttpStatus.OK);

    }

    public List<Project> findProjectByOwner(Long Id){
        return projectRepository.findByOwner(Id);
    }

    public Optional<Project> findProjectById(String Id) {
        return projectRepository.findById(Id);
    }
    public void UpdateProjectUserList(Project project,Long id){
        List<Long> userList = project.getUsers();
        userList.add(id);
        project.setUsers(userList);
        projectRepository.save(project);
    }

    public List<Project> FindProjectsContainingUser(Long id){
        List<Project> projects=projectRepository.findByUsersContaining(id);
        return  projects;
    }

    public Response<?> AddToProject(Long userId, String ProjectId,Users AuthUser){
        Optional<Users> UserToBeAddedOptional = userService.findUserById(userId);
        if(UserToBeAddedOptional.isEmpty()){
            return new Response<>(HttpStatus.UNAUTHORIZED.value(), false,"No user with the given id present",HttpStatus.UNAUTHORIZED);
        }
        Users UserToBeAdded = UserToBeAddedOptional.get();
        Optional<Project> ProjectOptional = findProjectById(ProjectId);
        if(ProjectOptional.isEmpty()){
            return new Response<>(HttpStatus.UNAUTHORIZED.value(), false,"No project found",HttpStatus.UNAUTHORIZED);
        }
        Project project = ProjectOptional.get();
        if(!CheckOwnerOfProject(AuthUser,project)){
            return new Response<>(HttpStatus.UNAUTHORIZED.value(),false,"Not Authorized to make the request",HttpStatus.UNAUTHORIZED);
        }
        if(UserAlreadyInTheProject(UserToBeAdded,project)){
            return new Response<>(HttpStatus.CONFLICT.value(), false,"User Already in the Project",HttpStatus.CONFLICT);
        }
        UpdateProjectUserList(project,UserToBeAdded.getId());
        return new Response<>(HttpStatus.OK.value(), true,"User Added to project successfully",HttpStatus.OK);
    }

    public boolean CheckOwnerOfProject(Users user,Project project){
        return Objects.equals(user.getId(),project.getOwner());
    }

    public boolean CheckAccessToProject(Users user,Project project){
        return Objects.equals(user.getId(),project.getOwner()) || project.getUsers().contains(user.getId());
    }

    public boolean UserAlreadyInTheProject(Users user,Project project){
        return project.getUsers().contains(user.getId());
    }

    public boolean CreateProjectValidateInput(Project project) {
        return project.getName() != null && !project.getName().isEmpty();
    }

    public boolean AddToProjectInputValidation(AddToProjectRequest requestBody) {
        return requestBody.getId() != null && requestBody.getProjectId() != null;
    }

    public Response<?> ProjectById(String projectId,Users user) {
        Optional<Project> projectOptional = findProjectById(projectId);
        if(projectOptional.isEmpty()){
            return new Response<>(HttpStatus.UNAUTHORIZED.value(), false,"No project found",HttpStatus.UNAUTHORIZED);
        }
        Project project = projectOptional.get();
        if(!CheckAccessToProject(user,project)){
            return new Response<>(HttpStatus.UNAUTHORIZED.value(), false,"Access to project denied",HttpStatus.UNAUTHORIZED);
        }
        return new Response<>(HttpStatus.OK.value(),true,"Project Details",project);
    }

    public Response<?> DeleteProjectById(String ProjectId,Users user){
        Optional<Project> projectOptinal = projectRepository.findById(ProjectId);
        if(projectOptinal.isEmpty()){
            return new Response<>(HttpStatus.NOT_FOUND.value(),false,"No Project Found","No Project Found");
        }
        System.out.println("==========="+user);
        System.out.println(projectOptinal);
        Project project = projectOptinal.get();
        if(CheckOwnerOfProject(user,project)){
            projectRepository.deleteProjectById(ProjectId);
            taskService.deleteAllByProjectId(ProjectId);
            return new Response<>(HttpStatus.OK.value(), true,"successfully deleted ther project","success");
        }
        return new Response<>(HttpStatus.UNAUTHORIZED.value(),false,"Not Authorized to make the request","NO access");
    }
}
