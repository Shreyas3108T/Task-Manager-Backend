package com.TaskManager.TaskManager.User;

import com.TaskManager.TaskManager.Response.Response;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path="v1")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping(path = "/user")
    public ResponseEntity<Response> GetAllUser()  {
        List<Users> users = userService.AllUsers();
        Response<List<Users>> response = new Response<>(200,true,"List of all users",users);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @GetMapping(path="/user/{id}")
    public ResponseEntity<Response> GetUserFromId( @PathVariable("id") Long UserId){
        Optional<Users> user2 = userService.findUserById(UserId);
        if(!user2.isPresent()){
            Response<String> response = new Response<>(404,false,"No user with given id found","No user");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Users user2info = user2.get();
        Response<Users> response = new Response<>(200,true,"user information",user2info);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = "/currentUserInfo")
    public ResponseEntity<Response<Users>> CurrentUserInfo(HttpServletRequest request){
        Users user = (Users) request.getAttribute("AuthUser");
        Response<Users> response = new Response<>(200,true,"UserData",user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

