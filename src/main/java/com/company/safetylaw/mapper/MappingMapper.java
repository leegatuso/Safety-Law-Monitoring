package com.company.safetylaw.mapper;

import com.company.safetylaw.domain.DocumentLawMapping;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MappingMapper {
    DocumentLawMapping selectById(@Param("id") long id);

    List<DocumentLawMapping> selectAll();

    int insert(DocumentLawMapping value);

    int update(DocumentLawMapping value);

    int deleteById(@Param("id") long id);

    List<DocumentLawMapping> selectList(
            @Param("documentId") Long documentId, @Param("lawId") Long lawId);

    int deleteForDocument(@Param("documentId") long documentId);
}
