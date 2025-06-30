package com.example.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    // 允许所有前端域名
    config.addAllowedOrigin("http://localhost:8080");
    config.addAllowedOrigin("http://localhost:8081");
    config.addAllowedOrigin("http://localhost:8082");
    config.addAllowedOrigin("http://localhost:8083");
    config.addAllowedOrigin("http://localhost:8084");
    config.addAllowedOrigin("http://localhost:8085");
    config.addAllowedMethod("*"); // 允许所有方法
    config.addAllowedHeader("*"); // 允许所有头
    config.setAllowCredentials(true); // 允许携带认证信息
    config.setMaxAge(3600L); // 预检请求的有效期，单位为秒

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
  }
}