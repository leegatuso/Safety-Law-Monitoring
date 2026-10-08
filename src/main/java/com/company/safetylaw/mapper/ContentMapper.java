package com.company.safetylaw.mapper;

import com.company.safetylaw.domain.LawContent;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContentMapper {
    LawContent selectById(@Param("id") long id);

    List<LawContent> selectAll();

    int insert(LawContent value);

    int update(LawContent value);

    int deleteById(@Param("id") long id);

    LawContent selectIdentity(@Param("source") String source, @Param("external") String external);

    List<LawContent> search(
            @Param("source") String source,
            @Param("query") String query,
            @Param("limit") int limit);

    long count();

    String lastCachedAt();

    List<Long> selectMatchingLawIds(@Param("query") String query);
}
