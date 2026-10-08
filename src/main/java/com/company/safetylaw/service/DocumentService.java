package com.company.safetylaw.service;

import static com.company.safetylaw.support.Checks.*;

import com.company.safetylaw.domain.*;
import com.company.safetylaw.dto.*;
import com.company.safetylaw.mapper.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    private final DocumentMapper documents;
    private final MappingMapper mappings;
    private final LawMapper laws;
    private final DocumentRevisionMapper history;
    private final ContentMapper contents;
    private final RevisionService revisions;

    public DocumentService(
            DocumentMapper documents,
            MappingMapper mappings,
            LawMapper laws,
            DocumentRevisionMapper history,
            ContentMapper contents,
            RevisionService revisions) {
        this.documents = documents;
        this.mappings = mappings;
        this.laws = laws;
        this.history = history;
        this.contents = contents;
        this.revisions = revisions;
    }

    public List<CompanyDocument> list(String type) {
        var links = mappings.selectAll();
        var lawNames =
                laws.selectAll().stream().collect(Collectors.toMap(Law::getId, Law::getName));
        var rows = documents.selectList(type);
        for (var d : rows) {
            var names =
                    links.stream()
                            .filter(m -> m.getDocumentId().equals(d.getId()))
                            .map(m -> lawNames.get(m.getTrackedLawId()))
                            .filter(Objects::nonNull)
                            .toList();
            d.setMappedLawCount(names.size());
            d.setMappedLaws(names);
        }
        return rows;
    }

    public CompanyDocument get(long id) {
        return found(documents.selectById(id));
    }

    @Transactional
    public CompanyDocument save(Long id, DocumentSaveRequest request) {
        if (!List.of("절차서", "지침서", "작업표준", "기타").contains(request.toEntity().getDocType()))
            throw new IllegalArgumentException("사규 종류를 확인하세요.");
        link(request.fileLink());
        var previous = id == null ? null : found(documents.selectForUpdate(id));
        var oldLinks = id == null ? List.<DocumentLawMapping>of() : mappings.selectList(id, null);
        if (previous != null && history.selectHistory(id, null, 1).isEmpty())
            record(
                    previous,
                    oldLinks.stream().map(DocumentLawMapping::getTrackedLawId).toList(),
                    "이력 관리 시작 시점의 기존 사규",
                    request.changedBy());
        var d = request.toEntity();
        d.setUpdatedAt(now());
        if (previous == null) {
            d.setCreatedAt(d.getUpdatedAt());
            documents.insert(d);
        } else {
            d.setId(id);
            d.setCreatedAt(previous.getCreatedAt());
            documents.update(d);
        }
        var selected =
                request.lawIds() == null
                        ? oldLinks.stream().map(DocumentLawMapping::getTrackedLawId).toList()
                        : request.lawIds().stream().distinct().toList();
        if (request.lawIds() != null) {
            for (long lawId : selected) found(laws.selectById(lawId));
            var existingIds =
                    oldLinks.stream()
                            .map(DocumentLawMapping::getTrackedLawId)
                            .collect(Collectors.toSet());
            for (var existing : oldLinks) {
                if (!selected.contains(existing.getTrackedLawId())) {
                    mappings.deleteById(existing.getId());
                }
            }
            for (long lawId : selected) {
                if (existingIds.contains(lawId)) continue;
                var m = new DocumentLawMapping();
                m.setDocumentId(d.getId());
                m.setTrackedLawId(lawId);
                m.setCreatedAt(now());
                mappings.insert(m);
            }
        }
        record(
                d,
                selected,
                text(request.changeReason()).isBlank()
                        ? (previous == null ? "최초 등록" : "사규 수정")
                        : request.changeReason(),
                request.changedBy());
        return d;
    }

    private void record(CompanyDocument d, List<Long> lawIds, String reason, String changedBy) {
        var r = new DocumentRevision();
        r.setDocumentId(d.getId());
        r.setTitle(d.getTitle());
        r.setDocType(d.getDocType());
        r.setDocNumber(d.getDocNumber());
        r.setRevisionNo(d.getRevisionNo());
        r.setRevisionDate(d.getRevisionDate());
        r.setOwner(d.getOwner());
        r.setFileLink(d.getFileLink());
        r.setNote(d.getNote());
        r.setTags(d.getTags());
        r.setChangeReason(reason);
        r.setChangedBy(changedBy);
        r.setChangedAt(now());
        r.setMappedLawIds(lawIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        history.insert(r);
    }

    void recordBaseline(CompanyDocument document) {
        if (history.selectHistory(document.getId(), null, 1).isEmpty())
            recordCurrent(document, "이력 관리 시작 시점의 기존 사규");
    }

    void recordCurrent(CompanyDocument document, String reason) {
        var ids =
                mappings.selectList(document.getId(), null).stream()
                        .map(DocumentLawMapping::getTrackedLawId)
                        .toList();
        record(document, ids, reason, null);
    }

    @Transactional
    public void delete(long id) {
        var document = found(documents.selectForUpdate(id));
        recordCurrent(document, "사규 삭제");
        documents.deleteById(id);
    }

    public List<DocumentRevision> history(Long id, String query, int limit) {
        return history.selectHistory(id, query, Math.max(1, Math.min(limit, 1000)));
    }

    public List<DocumentImpact> impacts(String status, boolean pendingOnly) {
        var active = laws.selectList(true, null);
        var links = mappings.selectAll();
        var results = new ArrayList<DocumentImpact>();
        var activeIds = active.stream().map(Law::getId).collect(Collectors.toSet());
        for (var d : documents.selectAll()) {
            var ids =
                    links.stream()
                            .filter(m -> m.getDocumentId().equals(d.getId()))
                            .map(DocumentLawMapping::getTrackedLawId)
                            .collect(Collectors.toSet());
            var tagged = new HashSet<Long>();
            for (String tag : text(d.getTags()).split("[,#\\s]+")) {
                if (tag.isBlank()) continue;
                tagged.addAll(contents.selectMatchingLawIds(tag));
            }
            var applicable = new HashSet<>(ids);
            applicable.addAll(tagged);
            applicable.retainAll(activeIds);
            var changes = revisions.forLaws(applicable, status, pendingOnly);
            for (var change : changes) {
                change.setMatchedBy(ids.contains(change.getTrackedLawId()) ? "mapping" : "tag");
            }
            if (!changes.isEmpty()) {
                results.add(new DocumentImpact(d.getId(), d.getTitle(), d.getDocType(), changes));
            }
        }
        return results;
    }
}
