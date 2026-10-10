package com.company.safetylaw.security;

import com.company.safetylaw.domain.EmployeeInfo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record LoginUser(EmployeeInfo employee) implements UserDetails {
    @Override
    public String getUsername() {
        return employee.employeeCode();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public String getDisplayName() {
        return employee.name() == null || employee.name().isBlank()
                ? getUsername()
                : employee.name();
    }

    public String getDepartmentName() {
        return employee.departmentName();
    }
}
