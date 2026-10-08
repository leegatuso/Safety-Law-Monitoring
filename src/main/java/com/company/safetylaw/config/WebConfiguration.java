package com.company.safetylaw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebConfiguration {
    @Bean
    InMemoryUserDetailsManager users(
            @Value("${app.username}") String username,
            @Value("${app.password}") String password,
            @Value("${server.address}") String address) {
        if (username.isBlank() != password.isBlank())
            throw new IllegalStateException("DASHBOARD_USERNAME과 DASHBOARD_PASSWORD를 함께 설정하세요.");
        if (!address.equals("127.0.0.1") && username.isBlank())
            throw new IllegalStateException("외부 접속 시 DASHBOARD_USERNAME/PASSWORD가 필요합니다.");
        var users = new InMemoryUserDetailsManager();
        if (!username.isBlank())
            users.createUser(
                    User.withUsername(username)
                            .password("{noop}" + password)
                            .roles("USER")
                            .build());
        return users;
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http, @Value("${app.username}") String username)
            throws Exception {
        // Same-origin UI sends the Thymeleaf CSRF token, including multipart uploads.
        http.authorizeHttpRequests(
                        auth -> {
                            auth.requestMatchers("/api/health", "/actuator/health").permitAll();
                            if (username.isBlank()) auth.anyRequest().permitAll();
                            else auth.anyRequest().authenticated();
                        })
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
