package com.company.safetylaw.security;

import com.company.safetylaw.service.EmployeeService;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/** SSO 연동 전 개발 환경에서만 사용하는 사번 입력 로그인. */
@Component
public class DevelopmentLoginProvider implements AuthenticationProvider {
    private final EmployeeService employees;
    private final boolean enabled;

    public DevelopmentLoginProvider(
            EmployeeService employees,
            @Value("${app.auth.development-login-enabled:false}") boolean enabled) {
        this.employees = employees;
        this.enabled = enabled;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        if (!enabled) throw new DisabledException("사번 입력 로그인이 비활성화되어 있습니다.");
        try {
            var user = new LoginUser(employees.findForEmployeeLogin(authentication.getName()));
            return UsernamePasswordAuthenticationToken.authenticated(
                    user, null, user.getAuthorities());
        } catch (DataAccessException ex) {
            LoggerFactory.getLogger(DevelopmentLoginProvider.class)
                    .warn("Employee directory lookup failed: {}", ex.getClass().getSimpleName());
            throw new InternalAuthenticationServiceException("인사 정보를 조회하지 못했습니다.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
