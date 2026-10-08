package com.company.safetylaw.mapper;

import com.company.safetylaw.domain.AppSetting;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SettingsMapper {
    AppSetting selectById(@Param("id") String id);

    List<AppSetting> selectAll();

    int insert(AppSetting value);

    int update(AppSetting value);

    int deleteById(@Param("id") String id);
}
