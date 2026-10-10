CREATE SEQUENCE IF NOT EXISTS slm_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS slm_law (
    id NUMBER(19) PRIMARY KEY,
    source_type VARCHAR2(16 CHAR) NOT NULL,
    external_id VARCHAR2(64 CHAR) NOT NULL,
    master_id VARCHAR2(64 CHAR),
    name VARCHAR2(255 CHAR) NOT NULL,
    category VARCHAR2(64 CHAR),
    department VARCHAR2(128 CHAR),
    current_promulgation_no VARCHAR2(64 CHAR),
    current_promulgation_date VARCHAR2(16 CHAR),
    current_enforcement_date VARCHAR2(16 CHAR),
    detail_link VARCHAR2(512 CHAR),
    is_active NUMBER(1) NOT NULL CHECK (is_active IN (0, 1)),
    last_synced_at VARCHAR2(40 CHAR),
    created_at VARCHAR2(40 CHAR),
    updated_at VARCHAR2(40 CHAR),
    CONSTRAINT slm_law_uq UNIQUE (source_type, external_id)
);

CREATE TABLE IF NOT EXISTS slm_revision (
    id NUMBER(19) PRIMARY KEY,
    tracked_law_id NUMBER(19) NOT NULL,
    promulgation_no VARCHAR2(64 CHAR),
    promulgation_date VARCHAR2(16 CHAR),
    enforcement_date VARCHAR2(16 CHAR),
    previous_promulgation_no VARCHAR2(64 CHAR),
    previous_promulgation_date VARCHAR2(16 CHAR),
    previous_enforcement_date VARCHAR2(16 CHAR),
    detected_at VARCHAR2(40 CHAR),
    review_status VARCHAR2(16 CHAR) NOT NULL,
    reviewer VARCHAR2(64 CHAR),
    reviewed_at VARCHAR2(40 CHAR),
    note CLOB,
    raw_data CLOB,
    FOREIGN KEY (tracked_law_id) REFERENCES slm_law(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS slm_document (
    id NUMBER(19) PRIMARY KEY,
    doc_type VARCHAR2(16 CHAR) NOT NULL,
    doc_number VARCHAR2(64 CHAR),
    title VARCHAR2(255 CHAR) NOT NULL,
    revision_no VARCHAR2(32 CHAR),
    revision_date VARCHAR2(16 CHAR),
    owner VARCHAR2(64 CHAR),
    file_link VARCHAR2(512 CHAR),
    note CLOB,
    tags VARCHAR2(512 CHAR),
    created_at VARCHAR2(40 CHAR),
    updated_at VARCHAR2(40 CHAR)
);

CREATE TABLE IF NOT EXISTS slm_mapping (
    id NUMBER(19) PRIMARY KEY,
    document_id NUMBER(19) NOT NULL,
    tracked_law_id NUMBER(19) NOT NULL,
    note VARCHAR2(255 CHAR),
    created_at VARCHAR2(40 CHAR),
    CONSTRAINT slm_mapping_uq UNIQUE (document_id, tracked_law_id),
    FOREIGN KEY (document_id) REFERENCES slm_document(id) ON DELETE CASCADE,
    FOREIGN KEY (tracked_law_id) REFERENCES slm_law(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS slm_candidate (
    id NUMBER(19) PRIMARY KEY,
    source_type VARCHAR2(16 CHAR) NOT NULL,
    external_id VARCHAR2(64 CHAR) NOT NULL,
    master_id VARCHAR2(64 CHAR),
    name VARCHAR2(255 CHAR) NOT NULL,
    category VARCHAR2(64 CHAR),
    department VARCHAR2(128 CHAR),
    promulgation_no VARCHAR2(64 CHAR),
    promulgation_date VARCHAR2(16 CHAR),
    enforcement_date VARCHAR2(16 CHAR),
    detail_link VARCHAR2(512 CHAR),
    matched_keyword VARCHAR2(64 CHAR),
    status VARCHAR2(16 CHAR),
    first_seen_at VARCHAR2(40 CHAR),
    CONSTRAINT slm_candidate_uq UNIQUE (source_type, external_id)
);

CREATE TABLE IF NOT EXISTS slm_content (
    id NUMBER(19) PRIMARY KEY,
    source_type VARCHAR2(16 CHAR) NOT NULL,
    external_id VARCHAR2(64 CHAR) NOT NULL,
    master_id VARCHAR2(64 CHAR),
    name VARCHAR2(255 CHAR) NOT NULL,
    category VARCHAR2(64 CHAR),
    department VARCHAR2(128 CHAR),
    promulgation_no VARCHAR2(64 CHAR),
    promulgation_date VARCHAR2(16 CHAR),
    enforcement_date VARCHAR2(16 CHAR),
    detail_link VARCHAR2(512 CHAR),
    content CLOB,
    article_content_len NUMBER(19),
    cached_at VARCHAR2(40 CHAR),
    CONSTRAINT slm_content_uq UNIQUE (source_type, external_id)
);

CREATE TABLE IF NOT EXISTS slm_guide (
    id NUMBER(19) PRIMARY KEY,
    code VARCHAR2(64 CHAR),
    field VARCHAR2(64 CHAR),
    title VARCHAR2(512 CHAR) NOT NULL,
    issued_date VARCHAR2(16 CHAR),
    file_link VARCHAR2(512 CHAR),
    content CLOB,
    content_stale NUMBER(1) NOT NULL CHECK (content_stale IN (0, 1)),
    note CLOB,
    created_at VARCHAR2(40 CHAR),
    updated_at VARCHAR2(40 CHAR),
    CONSTRAINT slm_guide_uq UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS slm_setting (
    setting_key VARCHAR2(64 CHAR) PRIMARY KEY,
    setting_value CLOB
);

CREATE TABLE IF NOT EXISTS slm_news (
    id NUMBER(19) PRIMARY KEY,
    category VARCHAR2(16 CHAR) NOT NULL,
    source_name VARCHAR2(64 CHAR) NOT NULL,
    title VARCHAR2(512 CHAR) NOT NULL,
    link VARCHAR2(1024 CHAR) NOT NULL,
    guid VARCHAR2(512 CHAR) NOT NULL,
    published_at VARCHAR2(40 CHAR),
    fetched_at VARCHAR2(40 CHAR),
    is_demo NUMBER(1) NOT NULL CHECK (is_demo IN (0, 1)),
    is_archived NUMBER(1) NOT NULL CHECK (is_archived IN (0, 1)),
    CONSTRAINT slm_news_uq UNIQUE (category, guid)
);
CREATE INDEX IF NOT EXISTS slm_revision_law_idx ON slm_revision (tracked_law_id);
CREATE INDEX IF NOT EXISTS slm_mapping_law_idx ON slm_mapping (tracked_law_id);

CREATE TABLE IF NOT EXISTS slm_document_revision (
 id NUMBER(19) PRIMARY KEY,
 document_id NUMBER(19),
 title VARCHAR2(255 ) NOT NULL,
 doc_type VARCHAR2(16 ),
 doc_number VARCHAR2(64 ),
 revision_no VARCHAR2(32 ),
 revision_date VARCHAR2(16 ),
 owner VARCHAR2(64 ),
 file_link VARCHAR2(512 ),
 note CLOB,
 tags VARCHAR2(512 ),
 change_reason CLOB,
 changed_by VARCHAR2(64 ),
 changed_at VARCHAR2(40 ) NOT NULL,
 mapped_law_ids CLOB,
 CONSTRAINT slm_doc_revision_doc_fk FOREIGN KEY (document_id) REFERENCES slm_document(id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS slm_doc_revision_doc_idx ON slm_document_revision(document_id,changed_at);

-- H2 전용 예시 인사 뷰. Oracle에서는 기존 인사 뷰를 조회하며 생성/변경하지 않는다.
CREATE OR REPLACE VIEW IS_USER_INFO_SSO_V AS
SELECT CAST('LOCAL001' AS VARCHAR2(64)) AS EMPCD,
       CAST('로컬 사용자' AS VARCHAR2(100)) AS HNAME,
       CAST('개발 환경' AS VARCHAR2(100)) AS DEPTCDNM,
       CAST(NULL AS VARCHAR2(40)) AS HANDPHONE,
       CAST('local@example.invalid' AS VARCHAR2(200)) AS EMAIL
FROM dual;
