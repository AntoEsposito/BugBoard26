package com.bugboard26.coreservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configurazione Spring MVC.
 * Registra JwtInterceptor su tutti gli endpoint, escludendo
 * il path pubblico per i file statici (immagini uploadate).
 * Serve i file dalla directory di upload tramite /api/uploads/**.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer
{
    private final JwtInterceptor jwtInterceptor;

    @Value("${upload.dir}")
    private String uploadDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/uploads/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
