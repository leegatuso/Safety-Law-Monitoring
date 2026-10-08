CREATE TABLE slm_document_revision (
 id NUMBER(19) PRIMARY KEY,
 document_id NUMBER(19),
 title VARCHAR2(255 CHAR) NOT NULL,
 doc_type VARCHAR2(16 CHAR),
 doc_number VARCHAR2(64 CHAR),
 revision_no VARCHAR2(32 CHAR),
 revision_date VARCHAR2(16 CHAR),
 owner VARCHAR2(64 CHAR),
 file_link VARCHAR2(512 CHAR),
 note CLOB,
 tags VARCHAR2(512 CHAR),
 change_reason CLOB,
 changed_by VARCHAR2(64 CHAR),
 changed_at VARCHAR2(40 CHAR) NOT NULL,
 mapped_law_ids CLOB,
 CONSTRAINT slm_doc_revision_doc_fk FOREIGN KEY (document_id) REFERENCES slm_document(id) ON DELETE SET NULL
);
CREATE INDEX slm_doc_revision_doc_idx ON slm_document_revision(document_id,changed_at);
