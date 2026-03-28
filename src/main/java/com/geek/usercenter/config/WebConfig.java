package com.geek.usercenter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        String os = System.getProperty("os.name");
        if(os.toLowerCase().startsWith("win")){
            registry.addResourceHandler("/upload/**")
                    .addResourceLocations("file:/D:/code/user-center/");
        }else{
            registry.addResourceHandler("/upload/**")
                    .addResourceLocations("file:/usr/local/user-center/upload/");
        }

    }
}

