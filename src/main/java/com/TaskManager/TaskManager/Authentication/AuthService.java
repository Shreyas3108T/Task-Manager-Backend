package com.TaskManager.TaskManager.Authentication;


import com.TaskManager.TaskManager.User.UserService;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class AuthService {
    private final UserService userService;

    @Autowired
    public AuthService(UserService userService){
        this.userService = userService;
    }




}
