package com.ooo.its.config;

import com.ooo.its.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private AdminAuthInterceptor adminAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/folders","/user/**")
                .excludePathPatterns("/login", "/logout", "/user/login")
                .excludePathPatterns("/css/**", "/js/**", "/img/**");


        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/dashboard", "/admin/**")
                .excludePathPatterns("/adminlogin", "/bbback")
                .excludePathPatterns("/css/**", "/js/**", "/img/**");
    }
}