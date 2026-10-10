package com.company.safetylaw.mapper;

import com.company.safetylaw.domain.EmployeeInfo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    List<EmployeeInfo> findByEmployeeCode(@Param("employeeCode") String employeeCode);

    List<EmployeeInfo> findByPid(@Param("pid") String pid);
}
