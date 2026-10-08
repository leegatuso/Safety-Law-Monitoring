package com.company.safetylaw.service;

import static com.company.safetylaw.support.Checks.found;

import com.company.safetylaw.domain.LawCandidate;
import com.company.safetylaw.mapper.CandidateMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CandidateService {
    private final CandidateMapper mapper;

    public CandidateService(CandidateMapper mapper) {
        this.mapper = mapper;
    }

    public List<LawCandidate> list(String status) {
        return mapper.selectStatus(status);
    }

    @Transactional
    public void action(long id, String action) {
        var c = found(mapper.selectById(id));
        c.setStatus(
                switch (action) {
                    case "dismiss" -> "무시됨";
                    case "restore" -> "신규";
                    case "registered" -> "등록됨";
                    default -> throw new IllegalArgumentException("잘못된 작업입니다.");
                });
        mapper.update(c);
    }

    @Transactional
    public void restore(List<Long> ids) {
        for (long id : ids) action(id, "restore");
    }
}
