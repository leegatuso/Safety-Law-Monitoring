package com.company.safetylaw.domain;

import java.io.Serializable;

/** 인사 뷰 조회 결과. 이 시스템에서는 인사 정보를 수정하지 않는다. */
public record EmployeeInfo(
        String employeeCode, String name, String departmentName, String phoneNumber, String email)
        implements Serializable {}
