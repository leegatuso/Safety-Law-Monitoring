package com.company.safetylaw.service;

import static com.company.safetylaw.support.Checks.*;

import com.company.safetylaw.domain.*;
import com.company.safetylaw.dto.*;
import com.company.safetylaw.mapper.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RevisionService {
    private final RevisionMapper revisions;
    private final LawMapper laws;
    private final DocumentMapper documents;
    private final MappingMapper mappings;

    public RevisionService(
            RevisionMapper revisions,
            LawMapper laws,
            DocumentMapper documents,
            MappingMapper mappings) {
        this.revisions = revisions;
        this.laws = laws;
        this.documents = documents;
        this.mappings = mappings;
    }

    public List<LawRevision> list(String status, Long lawId, boolean mappedOnly, int limit) {
        var rows =
                revisions.selectList(
                        status, lawId, mappedOnly, Math.max(1, Math.min(limit, 10000)));
        return enrichAll(rows);
    }

    public List<LawRevision> pending(int limit) {
        return enrichAll(revisions.selectPending(limit));
    }

    public List<LawRevision> forLaws(Collection<Long> ids, String status, boolean pendingOnly) {
        var result = new ArrayList<LawRevision>();
        var unique = new ArrayList<>(new LinkedHashSet<>(ids));
        // Oracle IN 조건의 항목 수 제한 안에서 조회한다.
        for (int start = 0; start < unique.size(); start += 900)
            result.addAll(
                    revisions.selectForLaws(
                            unique.subList(start, Math.min(start + 900, unique.size())),
                            status,
                            pendingOnly));
        result.sort(
                Comparator.comparing(LawRevision::getDetectedAt)
                        .thenComparing(LawRevision::getId)
                        .reversed());
        return enrichAll(result);
    }

    private List<LawRevision> enrichAll(List<LawRevision> rows) {
        if (rows.isEmpty()) return rows;
        var lawMap =
                laws.selectAll().stream()
                        .collect(Collectors.toMap(Law::getId, Function.identity()));
        var docMap =
                documents.selectAll().stream()
                        .collect(Collectors.toMap(CompanyDocument::getId, Function.identity()));
        var links = mappings.selectAll();
        for (var row : rows) enrich(row, lawMap.get(row.getTrackedLawId()), links, docMap);
        return rows;
    }

    private void enrich(
            LawRevision r,
            Law law,
            List<DocumentLawMapping> links,
            Map<Long, CompanyDocument> docs) {
        if (law != null) {
            r.setTrackedLawName(law.getName());
            r.setTrackedLawCategory(law.getCategory());
            r.setTrackedLawSourceType(law.getSourceType());
            r.setTrackedLawExternalId(law.getExternalId());
            r.setTrackedLawDetailLink(law.getDetailLink());
        }
        r.setMappedDocuments(
                links.stream()
                        .filter(m -> m.getTrackedLawId().equals(r.getTrackedLawId()))
                        .map(m -> docs.get(m.getDocumentId()))
                        .filter(Objects::nonNull)
                        .map(CompanyDocument::getTitle)
                        .toList());
    }

    public LawRevision get(long id) {
        var r = found(revisions.selectById(id));
        var docs = new HashMap<Long, CompanyDocument>();
        var links = mappings.selectList(null, r.getTrackedLawId());
        for (var m : links) docs.put(m.getDocumentId(), documents.selectById(m.getDocumentId()));
        enrich(r, laws.selectById(r.getTrackedLawId()), links, docs);
        return r;
    }

    @Transactional
    public LawRevision review(long id, ReviewRequest request) {
        var r = found(revisions.selectById(id));
        if (request.reviewStatus() != null) {
            r.setReviewStatus(request.reviewStatus());
            r.setReviewedAt(now());
        }
        if (request.reviewer() != null) r.setReviewer(request.reviewer());
        if (request.note() != null) r.setNote(request.note());
        revisions.update(r);
        return get(id);
    }

    @Transactional
    public List<LawRevision> reviewBulk(BulkReviewRequest request) {
        return request.ids().stream()
                .distinct()
                .map(id -> review(id, new ReviewRequest(request.reviewStatus(), null, null)))
                .toList();
    }

    @Transactional
    public void delete(List<Long> ids) {
        for (long id : ids) {
            found(revisions.selectById(id));
            revisions.deleteById(id);
        }
    }
}
