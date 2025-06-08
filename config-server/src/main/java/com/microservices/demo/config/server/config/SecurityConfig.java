package com.microservices.demo.config.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Created by ManitKumarPanda on 07/06/25.
 */

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/actuator/**", "/encrypt/**", "/decrypt/**").permitAll()
            .anyRequest().authenticated()
        )
        .httpBasic(httpBasic -> {
          // default config, no extra customization
        })
        .csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }

}
