package com.bugboard26.coreservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configurazione Spring MVC.
 * Registra JwtInterceptor su tutti gli endpoint, escludendo
 * il path pubblico per i file statici (immagini uploadate).
 */
@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer
{
    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/uploads/**");
    }
}
