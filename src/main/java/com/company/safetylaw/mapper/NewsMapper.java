package com.company.safetylaw.mapper;

import com.company.safetylaw.domain.NewsItem;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NewsMapper {
    NewsItem selectById(@Param("id") long id);

    List<NewsItem> selectAll();

    int insert(NewsItem value);

    int update(NewsItem value);

    int deleteById(@Param("id") long id);

    List<NewsItem> selectCategory(@Param("category") String category, @Param("limit") int limit);

    NewsItem selectGuid(@Param("category") String category, @Param("guid") String guid);

    int deleteExpired(@Param("cutoff") String cutoff);

    List<NewsItem> selectDedupeWindow(@Param("cutoff") String cutoff);

    List<NewsItem> selectSearch(
            @Param("category") String category,
            @Param("query") String query,
            @Param("from") String from,
            @Param("to") String to,
            @Param("archived") Boolean archived,
            @Param("issues") boolean issues,
            @Param("limit") int limit);

    int countSearch(
            @Param("category") String category,
            @Param("query") String query,
            @Param("from") String from,
            @Param("to") String to,
            @Param("archived") Boolean archived,
            @Param("issues") boolean issues);
}
