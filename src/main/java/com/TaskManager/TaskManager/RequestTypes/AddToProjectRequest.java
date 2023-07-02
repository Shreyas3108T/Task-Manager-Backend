package com.TaskManager.TaskManager.RequestTypes;

public class AddToProjectRequest {

    private final Long id;
    private final String ProjectId;


    public AddToProjectRequest(Long id,String ProjectId){
        this.id = id;
        this.ProjectId = ProjectId;
    }

    public Long getId() {
        return id;
    }

    public String getProjectId() {
        return ProjectId;
    }

    @Override
    public String toString() {
        return "AddToProjectRequest = {" +
                "id=" + id +
                ", ProjectId='" + ProjectId + '\'' +
                '}';
    }
}
