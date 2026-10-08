package com.company.safetylaw.mapper;

import com.company.safetylaw.domain.DocumentRevision;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DocumentRevisionMapper {
    DocumentRevision selectById(@Param("id") long id);

    List<DocumentRevision> selectAll();

    int insert(DocumentRevision value);

    int update(DocumentRevision value);

    int deleteById(@Param("id") long id);

    List<DocumentRevision> selectHistory(
            @Param("documentId") Long documentId,
            @Param("query") String query,
            @Param("limit") int limit);
}
