package com.company.safetylaw.mapper;

import com.company.safetylaw.domain.CompanyDocument;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DocumentMapper {
    CompanyDocument selectById(@Param("id") long id);

    List<CompanyDocument> selectAll();

    int insert(CompanyDocument value);

    int update(CompanyDocument value);

    int deleteById(@Param("id") long id);

    List<CompanyDocument> selectList(@Param("type") String type);

    CompanyDocument selectForUpdate(@Param("id") long id);
}
