package com.company.safetylaw.service;

import static com.company.safetylaw.support.Checks.*;

import com.company.safetylaw.domain.*;
import com.company.safetylaw.mapper.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MappingService {
    private final MappingMapper mappings;
    private final DocumentMapper documents;
    private final LawMapper laws;
    private final DocumentService history;

    public MappingService(
            MappingMapper mappings,
            DocumentMapper documents,
            LawMapper laws,
            DocumentService history) {
        this.mappings = mappings;
        this.documents = documents;
        this.laws = laws;
        this.history = history;
    }

    public List<DocumentLawMapping> list(Long documentId, Long lawId) {
        var rows = mappings.selectList(documentId, lawId);
        for (var m : rows) {
            m.setDocumentTitle(found(documents.selectById(m.getDocumentId())).getTitle());
            m.setTrackedLawName(found(laws.selectById(m.getTrackedLawId())).getName());
        }
        return rows;
    }

    @Transactional
    public DocumentLawMapping add(long documentId, long lawId, String note) {
        var document = found(documents.selectForUpdate(documentId));
        found(laws.selectById(lawId));
        history.recordBaseline(document);
        var m = new DocumentLawMapping();
        m.setDocumentId(documentId);
        m.setTrackedLawId(lawId);
        m.setNote(note);
        m.setCreatedAt(now());
        mappings.insert(m);
        history.recordCurrent(document, "근거 법령 연결");
        return m;
    }

    @Transactional
    public void delete(long id) {
        var m = found(mappings.selectById(id));
        var document = found(documents.selectForUpdate(m.getDocumentId()));
        history.recordBaseline(document);
        mappings.deleteById(id);
        history.recordCurrent(document, "근거 법령 연결 해제");
    }
}
