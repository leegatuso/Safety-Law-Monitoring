package com.company.safetylaw.mapper;

import com.company.safetylaw.domain.LawCandidate;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CandidateMapper {
    LawCandidate selectById(@Param("id") long id);

    List<LawCandidate> selectAll();

    int insert(LawCandidate value);

    int update(LawCandidate value);

    int deleteById(@Param("id") long id);

    List<LawCandidate> selectStatus(@Param("status") String status);

    LawCandidate selectIdentity(@Param("source") String source, @Param("external") String external);
}
