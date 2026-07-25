package com.sak.wifi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){

        //your_uploads file which is equivalent to uploads_example here

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

}
