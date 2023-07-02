package com.TaskManager.TaskManager.Project;

import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "Projects")
public class Project {
    @Id
    private String id;
    @NotNull
    private String name;

    private Long owner;
    private List<Long> users  = new ArrayList<>();

    public Project() {
    }

    public Project(String name, Long owner) {
        this.name = name;
        this.owner = owner;
        this.users = new ArrayList<>();
    }
    public Project(String name){
        this.name = name;
    }

    public String  getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owner=" + owner +
                ", users=" + users +
                '}';
    }
}
