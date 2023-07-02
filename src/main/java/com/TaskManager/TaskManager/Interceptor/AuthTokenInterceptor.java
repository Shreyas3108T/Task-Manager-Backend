package com.TaskManager.TaskManager.Interceptor;


import com.TaskManager.TaskManager.Response.Response;
import com.TaskManager.TaskManager.User.UserService;
import com.TaskManager.TaskManager.User.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.HandlerInterceptor;

import java.text.ParseException;
import java.util.Optional;

@Component
@Order(2)
public class AuthTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;


    public AuthTokenInterceptor(ObjectMapper objectMapper,UserService userService){
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String BearerToken = request.getHeader("Authorization");
        if(BearerToken == null || BearerToken.length()<10){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Token is null");
            return false;
        }
        System.out.print("============Step1 clear==================");

        String Token = BearerToken.substring(7);
        SignedJWT signedToken = SignedJWT.parse(Token);
        boolean verify = signedToken.verify(new MACVerifier("sULe2VBvR0PvjtUMHcMJaF2F2WE4OmfgsC5pK73qXe0="));
        if(!verify){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"invalid Token");

            return false;
        }
        JWTClaimsSet claimsSet = signedToken.getJWTClaimsSet();
        String email = claimsSet.getSubject();
        Optional<Users> user = userService.findUserByEmail(email);
        if(user.isEmpty()){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"user no longer in the system");

            return false;
        }
        Users UserInfo = user.get();

        request.setAttribute("AuthUser",UserInfo);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false; // Stop further processing of the request
        }
        System.out.print("here");
        return true;
    }

}
