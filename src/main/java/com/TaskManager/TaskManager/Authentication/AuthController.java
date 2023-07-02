package com.TaskManager.TaskManager.Authentication;

import com.TaskManager.TaskManager.RequestTypes.LoginRequest;
import com.TaskManager.TaskManager.Response.Response;
import com.TaskManager.TaskManager.User.Users;
import com.TaskManager.TaskManager.User.UserService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping(path = "/v1")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping(path = "/signup")
    public ResponseEntity<Response<?>> SignUp(@RequestBody Users users) throws JOSEException {
        System.out.println("heello   "+users.getPassword());
        Response<?> response = userService.CreateNewUser(users);
        return  ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<Response<?>> LogIn(@RequestBody LoginRequest requestBody) throws JOSEException {
        Response<?> response = userService.LoginUser(requestBody.getEmail(),requestBody.getPassword());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = "/validateToken")
    public ResponseEntity<Response<Boolean>> validateToken(HttpServletRequest request){
        Response<Boolean> response = new Response<>(HttpStatus.OK.value(), true,"success",true);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
