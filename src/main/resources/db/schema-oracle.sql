CREATE SEQUENCE slm_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE slm_law (
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

CREATE TABLE slm_revision (
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

CREATE TABLE slm_document (
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

CREATE TABLE slm_mapping (
    id NUMBER(19) PRIMARY KEY,
    document_id NUMBER(19) NOT NULL,
    tracked_law_id NUMBER(19) NOT NULL,
    note VARCHAR2(255 CHAR),
    created_at VARCHAR2(40 CHAR),
    CONSTRAINT slm_mapping_uq UNIQUE (document_id, tracked_law_id),
    FOREIGN KEY (document_id) REFERENCES slm_document(id) ON DELETE CASCADE,
    FOREIGN KEY (tracked_law_id) REFERENCES slm_law(id) ON DELETE CASCADE
);

CREATE TABLE slm_candidate (
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

CREATE TABLE slm_content (
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

CREATE TABLE slm_guide (
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

CREATE TABLE slm_setting (
    setting_key VARCHAR2(64 CHAR) PRIMARY KEY,
    setting_value CLOB
);

CREATE TABLE slm_news (
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
CREATE INDEX slm_revision_law_idx ON slm_revision (tracked_law_id);
CREATE INDEX slm_mapping_law_idx ON slm_mapping (tracked_law_id);

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

-- 테이블 및 컬럼 설명 (Oracle 데이터 사전에 등록)
COMMENT ON TABLE slm_law IS '개정 모니터링 대상 법령 및 행정규칙';
COMMENT ON COLUMN slm_law.id IS '고유 식별자';
COMMENT ON COLUMN slm_law.source_type IS '자료 구분: law=법령, admrul=행정규칙';
COMMENT ON COLUMN slm_law.external_id IS '외부 법령 API의 자료 식별자';
COMMENT ON COLUMN slm_law.master_id IS '외부 법령 API의 마스터 식별자';
COMMENT ON COLUMN slm_law.name IS '법령 또는 행정규칙 명칭';
COMMENT ON COLUMN slm_law.category IS '법령 또는 행정규칙 분류';
COMMENT ON COLUMN slm_law.department IS '소관 부처';
COMMENT ON COLUMN slm_law.current_promulgation_no IS '현재 공포 번호';
COMMENT ON COLUMN slm_law.current_promulgation_date IS '현재 공포일';
COMMENT ON COLUMN slm_law.current_enforcement_date IS '현재 시행일';
COMMENT ON COLUMN slm_law.detail_link IS '외부 원문 상세 조회 URL';
COMMENT ON COLUMN slm_law.is_active IS '모니터링 활성 여부: 0=비활성, 1=활성';
COMMENT ON COLUMN slm_law.last_synced_at IS '마지막 동기화 시각 (UTC ISO 8601 문자열)';
COMMENT ON COLUMN slm_law.created_at IS '등록 시각 (UTC ISO 8601 문자열)';
COMMENT ON COLUMN slm_law.updated_at IS '마지막 수정 시각 (UTC ISO 8601 문자열)';

COMMENT ON TABLE slm_revision IS '법령 및 행정규칙 개정 감지 이력과 검토 결과';
COMMENT ON COLUMN slm_revision.id IS '고유 식별자';
COMMENT ON COLUMN slm_revision.tracked_law_id IS '모니터링 대상 법령 식별자 (slm_law.id)';
COMMENT ON COLUMN slm_revision.promulgation_no IS '공포 번호';
COMMENT ON COLUMN slm_revision.promulgation_date IS '공포일';
COMMENT ON COLUMN slm_revision.enforcement_date IS '시행일';
COMMENT ON COLUMN slm_revision.previous_promulgation_no IS '개정 감지 직전 공포 번호';
COMMENT ON COLUMN slm_revision.previous_promulgation_date IS '개정 감지 직전 공포일';
COMMENT ON COLUMN slm_revision.previous_enforcement_date IS '개정 감지 직전 시행일';
COMMENT ON COLUMN slm_revision.detected_at IS '개정 감지 시각 (UTC ISO 8601 문자열)';
COMMENT ON COLUMN slm_revision.review_status IS '개정 검토 상태: 미검토, 검토중, 반영완료, 해당없음';
COMMENT ON COLUMN slm_revision.reviewer IS '개정 검토 담당자';
COMMENT ON COLUMN slm_revision.reviewed_at IS '검토 처리 시각 (UTC ISO 8601 문자열)';
COMMENT ON COLUMN slm_revision.note IS '비고 및 메모';
COMMENT ON COLUMN slm_revision.raw_data IS '개정 감지 시 수집한 원본 자료';

COMMENT ON TABLE slm_document IS '사내 절차서, 지침서, 작업표준 등 사규';
COMMENT ON COLUMN slm_document.id IS '고유 식별자';
COMMENT ON COLUMN slm_document.doc_type IS '사규 종류: 절차서, 지침서, 작업표준, 기타';
COMMENT ON COLUMN slm_document.doc_number IS '사규 문서 번호';
COMMENT ON COLUMN slm_document.title IS '사규 제목';
COMMENT ON COLUMN slm_document.revision_no IS '사규 개정 번호';
COMMENT ON COLUMN slm_document.revision_date IS '사규 개정일';
COMMENT ON COLUMN slm_document.owner IS '사규 관리 담당자';
COMMENT ON COLUMN slm_document.file_link IS '파일 또는 원문 URL';
COMMENT ON COLUMN slm_document.note IS '비고 및 메모';
COMMENT ON COLUMN slm_document.tags IS '검색 및 관련 법령 매칭용 태그';
COMMENT ON COLUMN slm_document.created_at IS '등록 시각 (UTC ISO 8601 문자열)';
COMMENT ON COLUMN slm_document.updated_at IS '마지막 수정 시각 (UTC ISO 8601 문자열)';

COMMENT ON TABLE slm_mapping IS '사규와 모니터링 대상 법령의 연결 관계';
COMMENT ON COLUMN slm_mapping.id IS '고유 식별자';
COMMENT ON COLUMN slm_mapping.document_id IS '사규 식별자 (slm_document.id)';
COMMENT ON COLUMN slm_mapping.tracked_law_id IS '모니터링 대상 법령 식별자 (slm_law.id)';
COMMENT ON COLUMN slm_mapping.note IS '사규와 법령의 연결 사유 및 메모';
COMMENT ON COLUMN slm_mapping.created_at IS '등록 시각 (UTC ISO 8601 문자열)';

COMMENT ON TABLE slm_candidate IS '키워드 검색으로 발견한 신규 법령 및 행정규칙 후보';
COMMENT ON COLUMN slm_candidate.id IS '고유 식별자';
COMMENT ON COLUMN slm_candidate.source_type IS '자료 구분: law=법령, admrul=행정규칙';
COMMENT ON COLUMN slm_candidate.external_id IS '외부 법령 API의 자료 식별자';
COMMENT ON COLUMN slm_candidate.master_id IS '외부 법령 API의 마스터 식별자';
COMMENT ON COLUMN slm_candidate.name IS '법령 또는 행정규칙 명칭';
COMMENT ON COLUMN slm_candidate.category IS '법령 또는 행정규칙 분류';
COMMENT ON COLUMN slm_candidate.department IS '소관 부처';
COMMENT ON COLUMN slm_candidate.promulgation_no IS '공포 번호';
COMMENT ON COLUMN slm_candidate.promulgation_date IS '공포일';
COMMENT ON COLUMN slm_candidate.enforcement_date IS '시행일';
COMMENT ON COLUMN slm_candidate.detail_link IS '외부 원문 상세 조회 URL';
COMMENT ON COLUMN slm_candidate.matched_keyword IS '신규 후보 발견에 사용된 검색 키워드';
COMMENT ON COLUMN slm_candidate.status IS '신규 후보 처리 상태: 신규, 등록됨, 무시됨';
COMMENT ON COLUMN slm_candidate.first_seen_at IS '최초 발견 시각 (UTC ISO 8601 문자열)';

COMMENT ON TABLE slm_content IS '법령 및 행정규칙 본문 검색용 캐시';
COMMENT ON COLUMN slm_content.id IS '고유 식별자';
COMMENT ON COLUMN slm_content.source_type IS '자료 구분: law=법령, admrul=행정규칙';
COMMENT ON COLUMN slm_content.external_id IS '외부 법령 API의 자료 식별자';
COMMENT ON COLUMN slm_content.master_id IS '외부 법령 API의 마스터 식별자';
COMMENT ON COLUMN slm_content.name IS '법령 또는 행정규칙 명칭';
COMMENT ON COLUMN slm_content.category IS '법령 또는 행정규칙 분류';
COMMENT ON COLUMN slm_content.department IS '소관 부처';
COMMENT ON COLUMN slm_content.promulgation_no IS '공포 번호';
COMMENT ON COLUMN slm_content.promulgation_date IS '공포일';
COMMENT ON COLUMN slm_content.enforcement_date IS '시행일';
COMMENT ON COLUMN slm_content.detail_link IS '외부 원문 상세 조회 URL';
COMMENT ON COLUMN slm_content.content IS '검색용 원문 본문';
COMMENT ON COLUMN slm_content.article_content_len IS '부가 자료를 덧붙이기 전 조문 본문의 문자 수';
COMMENT ON COLUMN slm_content.cached_at IS '본문 캐시 저장 시각 (UTC ISO 8601 문자열)';

COMMENT ON TABLE slm_guide IS 'KOSHA 기술지원규정 및 가이드와 본문 캐시';
COMMENT ON COLUMN slm_guide.id IS '고유 식별자';
COMMENT ON COLUMN slm_guide.code IS 'KOSHA 가이드 코드';
COMMENT ON COLUMN slm_guide.field IS 'KOSHA 가이드 분야';
COMMENT ON COLUMN slm_guide.title IS 'KOSHA 가이드 제목';
COMMENT ON COLUMN slm_guide.issued_date IS 'KOSHA 가이드 발행일';
COMMENT ON COLUMN slm_guide.file_link IS '파일 또는 원문 URL';
COMMENT ON COLUMN slm_guide.content IS '검색용 원문 본문';
COMMENT ON COLUMN slm_guide.content_stale IS '본문 캐시 갱신 필요 여부: 0=불필요, 1=필요';
COMMENT ON COLUMN slm_guide.note IS '비고 및 메모';
COMMENT ON COLUMN slm_guide.created_at IS '등록 시각 (UTC ISO 8601 문자열)';
COMMENT ON COLUMN slm_guide.updated_at IS '마지막 수정 시각 (UTC ISO 8601 문자열)';

COMMENT ON TABLE slm_setting IS '애플리케이션 설정 키와 값';
COMMENT ON COLUMN slm_setting.setting_key IS '설정 항목 키';
COMMENT ON COLUMN slm_setting.setting_value IS '설정 항목 값';

COMMENT ON TABLE slm_news IS '안전보건 뉴스 수집 및 보관 정보';
COMMENT ON COLUMN slm_news.id IS '고유 식별자';
COMMENT ON COLUMN slm_news.category IS '뉴스 구분: moel=고용노동부, kosha=안전보건공단, accident=사고 뉴스';
COMMENT ON COLUMN slm_news.source_name IS '뉴스 제공 기관 또는 언론사';
COMMENT ON COLUMN slm_news.title IS '뉴스 제목';
COMMENT ON COLUMN slm_news.link IS '뉴스 원문 URL';
COMMENT ON COLUMN slm_news.guid IS '뉴스 피드 항목의 고유 식별자';
COMMENT ON COLUMN slm_news.published_at IS '뉴스 발행 시각';
COMMENT ON COLUMN slm_news.fetched_at IS '뉴스 수집 시각 (UTC ISO 8601 문자열)';
COMMENT ON COLUMN slm_news.is_demo IS '데모 뉴스 여부: 0=실제, 1=데모';
COMMENT ON COLUMN slm_news.is_archived IS '뉴스 보관 여부: 0=미보관, 1=보관';

COMMENT ON TABLE slm_document_revision IS '사규 등록, 수정, 삭제 시점의 문서 및 연결 법령 스냅샷';
COMMENT ON COLUMN slm_document_revision.id IS '고유 식별자';
COMMENT ON COLUMN slm_document_revision.document_id IS '원본 사규 식별자 (slm_document.id, 사규 삭제 시 NULL)';
COMMENT ON COLUMN slm_document_revision.title IS '변경 당시 사규 제목';
COMMENT ON COLUMN slm_document_revision.doc_type IS '사규 종류: 절차서, 지침서, 작업표준, 기타';
COMMENT ON COLUMN slm_document_revision.doc_number IS '사규 문서 번호';
COMMENT ON COLUMN slm_document_revision.revision_no IS '사규 개정 번호';
COMMENT ON COLUMN slm_document_revision.revision_date IS '사규 개정일';
COMMENT ON COLUMN slm_document_revision.owner IS '사규 관리 담당자';
COMMENT ON COLUMN slm_document_revision.file_link IS '파일 또는 원문 URL';
COMMENT ON COLUMN slm_document_revision.note IS '비고 및 메모';
COMMENT ON COLUMN slm_document_revision.tags IS '검색 및 관련 법령 매칭용 태그';
COMMENT ON COLUMN slm_document_revision.change_reason IS '사규 등록, 수정 또는 삭제 사유';
COMMENT ON COLUMN slm_document_revision.changed_by IS '사규 변경 작업자';
COMMENT ON COLUMN slm_document_revision.changed_at IS '사규 변경 이력 기록 시각 (UTC ISO 8601 문자열)';
COMMENT ON COLUMN slm_document_revision.mapped_law_ids IS '변경 당시 연결된 법령 식별자 목록 (쉼표 구분)';
