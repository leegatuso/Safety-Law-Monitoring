package com.company.safetylaw.service;

import com.company.safetylaw.domain.EmployeeInfo;
import com.company.safetylaw.mapper.EmployeeMapper;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmployeeService {
    private final EmployeeMapper employees;

    public EmployeeService(EmployeeMapper employees) {
        this.employees = employees;
    }

    public EmployeeInfo findForEmployeeLogin(String employeeCode) {
        return unique(employees.findByEmployeeCode(identifier(employeeCode)));
    }

    /** 실제 SSO 검증을 마친 뒤 호출할 조회 지점. 외부 HTTP 경로로 노출하지 않는다. */
    public EmployeeInfo findForVerifiedPid(String pid) {
        return unique(employees.findByPid(identifier(pid)));
    }

    private String identifier(String value) {
        if (value == null
                || value.isBlank()
                || value.length() > 128
                || value.chars().anyMatch(Character::isISOControl)) {
            throw new BadCredentialsException("사번 또는 사용자 식별자를 확인하세요.");
        }
        return value.strip();
    }

    private EmployeeInfo unique(List<EmployeeInfo> matches) {
        // 이메일 ID가 여러 사람과 일치해도 임의의 첫 번째 사번으로 로그인하지 않는다.
        if (matches.size() != 1
                || matches.get(0).employeeCode() == null
                || matches.get(0).employeeCode().isBlank()) {
            throw new BadCredentialsException("인사 정보에서 사용자를 식별할 수 없습니다.");
        }
        return matches.get(0);
    }
}
