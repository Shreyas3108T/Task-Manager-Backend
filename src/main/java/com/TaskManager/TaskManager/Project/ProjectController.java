package com.TaskManager.TaskManager.Project;

import com.TaskManager.TaskManager.RequestTypes.AddToProjectRequest;
import com.TaskManager.TaskManager.Response.Response;
import com.TaskManager.TaskManager.User.Users;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/v1")
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping(path="/project")
    public ResponseEntity<?> CreateProject(@Valid @RequestBody  Project project, HttpServletRequest request){
        System.out.print(project);
        Response<?> response = projectService.CreateProjectValidateInput(project) ?
                               projectService.CreateNewProject(project,(Users) request.getAttribute("AuthUser")) :
                               new Response<>(HttpStatus.BAD_REQUEST.value(), false,"name is missing from body","name is missing");
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path="/project")
    public ResponseEntity<?> GetProjects(HttpServletRequest request) {
        List<Project> projects = projectService.findProjectByOwner(((Users) request.getAttribute("AuthUser")).getId());
        Response<?> response = new Response<>(200,true,"here is a list of Projects",projects);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path="/AddToProject")
    public ResponseEntity<Response<?>> AddToProject(HttpServletRequest request, @RequestBody AddToProjectRequest requestBody){
        Response<?> response = projectService.AddToProjectInputValidation(requestBody) ?
                projectService.AddToProject(requestBody.getId(),requestBody.getProjectId(),(Users) request.getAttribute("AuthUser")) :
                new Response<>(HttpStatus.BAD_REQUEST.value(), false,"id or ProjectId is missing","request body is not complete");
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @GetMapping(path = "/projectPartOf")
    public ResponseEntity<Response<?>> ProjectsPartOf(HttpServletRequest request){
        List<Project> projects= projectService.FindProjectsContainingUser(((Users) request.getAttribute("AuthUser")).getId());
        Response<List<Project>> response = new Response<>(200,true,"List of Projects",projects);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = "/project/{id}")
    public ResponseEntity<Response<?>> ProjectById(HttpServletRequest request,@PathVariable("id") String projectId){
        Response<?> response = projectService.ProjectById(projectId, (Users) request.getAttribute("AuthUser"));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = "/project/{id}")
    public ResponseEntity<Response<?>> DeleteProject(HttpServletRequest request,@PathVariable("id") String id){
        Response<?> response = projectService.DeleteProjectById(id,(Users) request.getAttribute("AuthUser"));
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
