package com.company.safetylaw.config;

import com.company.safetylaw.security.DevelopmentLoginProvider;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import java.io.IOException;

@Configuration
public class WebConfiguration {
    @Bean
    SecurityFilterChain security(HttpSecurity http, DevelopmentLoginProvider loginProvider)
            throws Exception {
        var requestCache = new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName(null);
        requestCache.setRequestMatcher(
                request ->
                        "GET".equals(request.getMethod())
                                && !request.getServletPath().startsWith("/api/")
                                && !request.getServletPath().startsWith("/actuator/"));
        var loginEntryPoint = new LoginUrlAuthenticationEntryPoint("/login");
        var trustResolver = new AuthenticationTrustResolverImpl();

        http.authenticationProvider(loginProvider)
                .authorizeHttpRequests(
                        auth ->
                                auth.dispatcherTypeMatchers(DispatcherType.ERROR)
                                        .permitAll()
                                        .requestMatchers(
                                                "/",
                                                "/index",
                                                "/index.html",
                                                "/login",
                                                "/login/employee",
                                                "/css/**",
                                                "/js/**",
                                                "/favicon.svg",
                                                "/error",
                                                "/api/health",
                                                "/actuator/health")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .requestCache(cache -> cache.requestCache(requestCache))
                .formLogin(
                        form ->
                                form.loginPage("/login")
                                        .loginProcessingUrl("/login/employee")
                                        .usernameParameter("employeeCode")
                                        .defaultSuccessUrl("/dashboard", false)
                                        .failureHandler(
                                                (request, response, exception) -> {
                                                    String reason =
                                                            exception instanceof DisabledException
                                                                    ? "disabled"
                                                                    : exception
                                                                                    instanceof
                                                                                    InternalAuthenticationServiceException
                                                                            ? "unavailable"
                                                                            : "user";
                                                    response.sendRedirect(
                                                            request.getContextPath()
                                                                    + "/login?error="
                                                                    + reason);
                                                }))
                .sessionManagement(
                        session -> session.sessionFixation(fixation -> fixation.changeSessionId()))
                .logout(
                        logout ->
                                logout.logoutUrl("/logout")
                                        .logoutSuccessUrl("/login?logout")
                                        .invalidateHttpSession(true)
                                        .clearAuthentication(true)
                                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(
                        errors ->
                                errors.authenticationEntryPoint(
                                                (request, response, exception) -> {
                                                    if (request.getServletPath()
                                                            .startsWith("/api/")) {
                                                        jsonError(response, 401, "로그인이 필요합니다.");
                                                    } else {
                                                        loginEntryPoint.commence(
                                                                request, response, exception);
                                                    }
                                                })
                                        .accessDeniedHandler(
                                                (request, response, exception) -> {
                                                    var authentication =
                                                            SecurityContextHolder.getContext()
                                                                    .getAuthentication();
                                                    boolean anonymous =
                                                            authentication == null
                                                                    || trustResolver.isAnonymous(
                                                                            authentication);
                                                    if (request.getServletPath()
                                                            .startsWith("/api/")) {
                                                        jsonError(
                                                                response,
                                                                anonymous ? 401 : 403,
                                                                anonymous
                                                                        ? "로그인이 필요합니다."
                                                                        : "요청을 확인할 수 없습니다. 화면을"
                                                                              + " 새로고침해 주세요.");
                                                    } else {
                                                        response.sendRedirect(
                                                                request.getContextPath()
                                                                        + "/login?expired");
                                                    }
                                                }));
        // Thymeleaf forms and the common API client send CSRF tokens; keep CSRF enabled.
        return http.build();
    }

    private static void jsonError(HttpServletResponse response, int status, String detail)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"detail\":\"" + detail + "\"}");
    }
}
