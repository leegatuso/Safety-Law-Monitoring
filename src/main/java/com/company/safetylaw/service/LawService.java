package com.company.safetylaw.service;

import static com.company.safetylaw.support.Checks.*;

import com.company.safetylaw.client.LawClient;
import com.company.safetylaw.domain.*;
import com.company.safetylaw.dto.*;
import com.company.safetylaw.mapper.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class LawService {
    private final LawMapper laws;
    private final LawClient client;

    public LawService(LawMapper laws, LawClient client) {
        this.laws = laws;
        this.client = client;
    }

    public List<LawSnapshot> search(String source, String query) {
        if (query.isBlank()) throw new IllegalArgumentException("검색어를 입력하세요.");
        return client.search(source, query, 1);
    }

    public List<Law> list(boolean active, String source) {
        return laws.selectList(active, source);
    }

    public Law get(long id) {
        return found(laws.selectById(id));
    }

    @Transactional
    public Law register(LawCreateRequest request) {
        if (!List.of("law", "admrul").contains(request.sourceType()))
            throw new IllegalArgumentException("법령 구분을 확인하세요.");
        link(request.detailLink());
        var old =
                laws.selectIdentity(request.sourceType(), request.externalId(), request.masterId());
        if (old != null && Boolean.TRUE.equals(old.getIsActive()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 추적 중인 법령입니다.");
        var law = request.toEntity();
        law.setIsActive(true);
        law.setUpdatedAt(now());
        law.setLastSyncedAt(null);
        if (old == null) {
            law.setCreatedAt(law.getUpdatedAt());
            laws.insert(law);
        } else {
            law.setId(old.getId());
            law.setCreatedAt(old.getCreatedAt());
            laws.update(law);
        }
        return law;
    }

    @Transactional
    public void deactivate(long id) {
        get(id);
        laws.deactivate(id);
    }
}
