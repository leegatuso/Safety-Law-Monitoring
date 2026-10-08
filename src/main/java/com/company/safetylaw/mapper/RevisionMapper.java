package com.company.safetylaw.mapper;

import com.company.safetylaw.domain.LawRevision;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RevisionMapper {
    LawRevision selectById(@Param("id") long id);

    List<LawRevision> selectAll();

    int insert(LawRevision value);

    int update(LawRevision value);

    int deleteById(@Param("id") long id);

    List<LawRevision> selectList(
            @Param("status") String status,
            @Param("lawId") Long lawId,
            @Param("mappedOnly") boolean mappedOnly,
            @Param("limit") int limit);

    List<LawRevision> selectPending(@Param("limit") int limit);

    List<LawRevision> selectForLaws(
            @Param("lawIds") java.util.Collection<Long> lawIds,
            @Param("status") String status,
            @Param("pendingOnly") boolean pendingOnly);
}
