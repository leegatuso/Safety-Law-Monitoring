package com.company.safetylaw.mapper;

import com.company.safetylaw.domain.Law;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LawMapper {
    Law selectById(@Param("id") long id);

    List<Law> selectAll();

    int insert(Law value);

    int update(Law value);

    int deleteById(@Param("id") long id);

    List<Law> selectList(@Param("active") boolean active, @Param("source") String source);

    Law selectIdentity(
            @Param("source") String source,
            @Param("external") String external,
            @Param("master") String master);

    int deactivate(@Param("id") long id);
}
