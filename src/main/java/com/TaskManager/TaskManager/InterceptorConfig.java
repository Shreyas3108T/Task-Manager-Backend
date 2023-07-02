package com.TaskManager.TaskManager;

import com.TaskManager.TaskManager.Interceptor.AuthTokenInterceptor;
import com.TaskManager.TaskManager.Interceptor.CorsInterceptor;
import com.TaskManager.TaskManager.User.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;
    private final UserService userService;


    public InterceptorConfig(ObjectMapper objectMapper, UserService userService){
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(corsInterceptor()).addPathPatterns("/**").order(Ordered.HIGHEST_PRECEDENCE);
        registry.addInterceptor(authTokenInterceptor()).addPathPatterns("/**").excludePathPatterns("/v1/login","/v1/signup").order(Ordered.LOWEST_PRECEDENCE);
    }

    public AuthTokenInterceptor authTokenInterceptor() {
        return new AuthTokenInterceptor(objectMapper,userService);
    }

    public CorsInterceptor corsInterceptor(){
        return new CorsInterceptor();
    }



}
