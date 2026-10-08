package com.company.safetylaw.mapper;

import com.company.safetylaw.domain.KoshaGuide;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GuideMapper {
    KoshaGuide selectById(@Param("id") long id);

    List<KoshaGuide> selectAll();

    int insert(KoshaGuide value);

    int update(KoshaGuide value);

    int deleteById(@Param("id") long id);

    KoshaGuide selectCode(@Param("code") String code);

    List<KoshaGuide> selectList(@Param("field") String field);

    List<KoshaGuide> search(@Param("query") String query);

    List<KoshaGuide> selectPending(@Param("limit") int limit);

    long countPending();
}
