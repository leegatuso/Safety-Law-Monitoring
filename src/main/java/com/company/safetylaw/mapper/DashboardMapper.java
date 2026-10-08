package com.company.safetylaw.mapper;

import com.company.safetylaw.dto.DashboardCounts;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DashboardMapper {
    DashboardCounts counts();
}
