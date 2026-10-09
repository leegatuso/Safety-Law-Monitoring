# 안전보건 정보 모니터링 · Spring Boot / Thymeleaf

원본 Safety-Law-Monitoring-Sys-v2의 화면과 API를 Java로 이전한 프로젝트입니다.
`D:\IJ\cp-dashboard`를 기준으로 Java 17, Spring Boot 3.5.15, Gradle 8.14.5,
MyBatis 3.0.5, Oracle JDBC를 사용합니다. 화면은 **Thymeleaf + HTML/CSS/JavaScript**이며
React, Vite, Node 빌드 과정이 없습니다.

## 상세 문서

- [기능 설명](docs/FEATURES.md): 메뉴별 사용법, 동기화 조건, 설정, 다른 PC로 이전하는 방법
- [구조 설명](docs/ARCHITECTURE.md): 프로젝트 구성, 클래스 역할, 테이블 관계, API, 저장·실행 흐름

## 실행

JDK 17이 필요합니다. IntelliJ에서 이 폴더를 Gradle 프로젝트로 열고 Project SDK와 Gradle JVM을 17로 설정하세요.

로컬 검증(Oracle 없이 실행):

```powershell
.\gradlew.bat bootRun --args="--spring.profiles.active=local --server.port=8080"
```

브라우저에서 http://127.0.0.1:8080 에 접속합니다. 로컬 검증 데이터는 `data/local.mv.db`,
첨부 PDF는 `data/kosha/`에 저장됩니다. 로컬 프로필의 법령은 예시이며 최신 법령 정보가 아닙니다.
로컬에서는 외부 뉴스 수집을 건너뜁니다. 화면의 데이터 입력과 검토 상태는 재시작해도 유지됩니다.

## Oracle 운영 설정

1. 별도의 Oracle 사용자/스키마를 준비합니다.
2. `src/main/resources/db/schema-oracle.sql`을 해당 스키마에서 **최초 1회** 실행합니다.
   기존 cpboard 테이블과 겹치지 않도록 `SLM_` 접두사를 사용합니다.
3. 아래 환경변수를 설정하고 Oracle 모드로 실행합니다.

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-17.0.18'
$env:SAFETY_LAW_DB_URL='jdbc:oracle:thin:@DB_HOST:1521/SERVICE_NAME'
$env:SAFETY_LAW_DB_USERNAME='safety_law'
$env:SAFETY_LAW_DB_PASSWORD='실제 DB 비밀번호'
$env:LAW_API_OC='국가법령정보센터 인증키'
$env:KOSHA_GUIDE_API_KEY='공공데이터포털 인증키'
.\gradlew.bat bootRun
```

인증키는 화면의 설정에서도 변경할 수 있습니다. DB 비밀번호는 소스에 저장하지 않습니다.
기본 프로필은 Oracle이며 스키마를 자동 생성/삭제하지 않습니다. 기존 Python SQLite 데이터는
자동으로 이관되지 않습니다. 실제 기존 DB와 첨부파일을 확보한 뒤 백업·이관·건수 대조가 필요합니다.

사내 다른 PC에서 접속할 때는 `SERVER_ADDRESS=0.0.0.0`과 `DASHBOARD_USERNAME`,
`DASHBOARD_PASSWORD`를 함께 설정하세요. 외부 바인딩에서 로그인 정보가 없으면 기동을 차단합니다.
운영 서버에서는 HTTPS 리버스 프록시 뒤에 배치하세요. 기본 localhost 실행은 로그인 없이 사용할 수 있고,
모든 변경 요청에는 CSRF 토큰이 필요합니다.

## Linux 배포

```bash
# JDK 17, Oracle 접속 환경변수 및 로그인 환경변수 설정 후
sh gradlew bootJar
java -jar build/libs/safety-law-monitoring-0.1.0-SNAPSHOT.jar
```

`SERVER_PORT` 기본값은 8080입니다. `SAFETY_LAW_FILES_DIR`로 PDF 영구 저장 경로를 지정할 수 있습니다.
Oracle 데이터와 PDF 폴더를 함께 백업하세요. 단일 서버 프로세스 기준으로 스케줄러가 실행됩니다.
배포는 Spring Boot JAR를 기준으로 합니다. 이전 Python 코드·설치 도구·배포 설정은 제거했습니다.

## 이전된 기능

- 법령·행정규칙 검색, 등록, 추적 취소/재등록
- 최초 확인 및 개정 감지, 이전 공포/시행 정보, 일괄 검토 상태 변경
- 신규 고시 후보 검색, 무시/복원, 부처·키워드·기준일 필터
- 사규 CRUD, 문서-법령 매핑, 키워드 기반 영향 분석
- 사규 저장·근거 법령 연결의 트랜잭션 처리, 사규 자체의 개정 이력 보존
- 법령 본문 캐시, 본문 검색, 조문 링크, 전체 캐시 백그라운드 처리
- KOSHA 가이드 수동/일괄 등록, API 동기화, PDF 업로드·다운로드·본문 추출
- RSS/Atom 뉴스 수집, 날짜/제목 검색, 보관 및 기간별 정리
- 설정 저장, 첫 실행 도움말, 대시보드 요약, 자동 동기화

법령 동기화 기본 주기는 24시간(`AUTO_SYNC_DELAY_MS=86400000`), 뉴스는 3시간(`NEWS_FETCH_DELAY_MS=10800000`)입니다.
전체 본문 캐시는 설정에서 켜면 서버가 실행 중인 날의 오전 1시(Asia/Seoul)에 실행합니다.
일부 목록·문서 영향 계산은 부서 규모의 데이터셋을 메모리에서 처리하며, 대규모 운영 전에는 SQL 페이지네이션 및 부하 검증이 필요합니다.
날짜/시각은 기존 API 호환을 위해 UTC ISO 문자열로 저장하고 브라우저에서 현지 시간으로 표시합니다.

## 구조

```text
src/main/java/com/company/safetylaw/
├── SafetyLawApplication.java   Spring Boot 실행 클래스
├── config/                     보안 및 애플리케이션 설정
├── controller/                 화면 라우팅 및 REST API
├── service/                    법령·사규·개정·가이드·뉴스별 업무 처리
├── mapper/                     업무별 MyBatis 인터페이스
├── domain/                     테이블별 JavaBean
├── dto/                        입력 검증 DTO와 조회 결과
├── scheduler/                  정기 동기화·캐시 작업 호출
├── client/                     외부 HTTP 및 법령 API 연동
├── exception/                  공통 API 예외 처리
└── support/                    공통 데이터 처리·뉴스 중복 판별 유틸리티
src/main/resources/
├── application.yaml            공통·Oracle 실행 설정
├── application-local.yaml      로컬 H2 실행 설정
├── mapper/                     업무별 SQL XML
├── templates/
│   ├── fragments/layout.html   공통 헤더·메뉴·도움말
│   └── pages/                  메뉴별 Thymeleaf 화면
├── static/
│   ├── css/style.css           스타일시트
│   ├── js/                     화면별 JavaScript
│   └── favicon.svg             아이콘
├── db/                         Oracle/H2 스키마 및 증분 변경 SQL
└── data/demo-laws.json          로컬 예시 법령
```

Spring Boot 표준 리소스 경로를 사용하며 별도의 프런트엔드 복사 작업은 없습니다.
IntelliJ에서 Gradle 프로젝트를 새로고침한 뒤 `Tasks → build → bootJar`로 JAR를 만들 수 있습니다.
`bootJar`로 실행 가능한 JAR를 생성합니다. 테스트 소스와 테스트 전용 의존성은 제거했습니다.
실행 중 템플릿/JS/CSS를 수정하면 재빌드·재시작하세요.

## 메뉴·테이블 구성

| 메뉴 | URL | 사용하는 테이블 |
| --- | --- | --- |
| 대시보드 | `/dashboard` | 각 업무 테이블 집계 |
| 법령 마스터 | `/laws` | `slm_law` |
| 법령 개정 이력 | `/revisions` | `slm_revision` |
| 사규 | `/documents` | `slm_document`, `slm_mapping` |
| 사규 개정 이력 | `/document-revisions` | `slm_document_revision` |
| 법령 영향 | `/document-impacts` | 사규·법령·매핑·법령 개정·본문 조회 |
| KOSHA 가이드 | `/kosha-guides` | `slm_guide` |
| 안전보건 뉴스 | `/news` | `slm_news` |
| 키워드 검색 | `/search` | `slm_content` |
| 설정 | `/settings` | `slm_setting` |

신규 고시 후보는 `slm_candidate`를 사용합니다. 대시보드와 검색·영향 화면은 업무 데이터를
조회하므로 화면 수만큼 중복 테이블을 만들지 않습니다. `repository` 계층 없이
`Controller → Service → Mapper → SQL XML`로 구성했습니다.

### 기존 DB에 적용

기존 Oracle 스키마가 있다면 `src/main/resources/db/migration/V2__document_revision_oracle.sql`을
**한 번 실행한 다음** 새 애플리케이션을 시작하세요. 기존 스키마에 전체 `schema-oracle.sql`을
재실행하지 마세요. 신규 Oracle 설치는 전체 스키마만 실행하면 됩니다.
로컬 H2는 다음 애플리케이션 시작 시 `CREATE TABLE IF NOT EXISTS`로 이력 테이블을 추가합니다.

사규 이력은 등록·수정·근거 법령 연결 변경·삭제 시 저장됩니다. 기존 사규의 첫 수정에서는
수정 전 상태도 함께 보존합니다. 사규를 삭제해도 이력은 유지됩니다. 과거 변경 내용을
소급 복원하거나 링크된 파일의 복사본을 보관하는 기능은 아닙니다.
구체적인 책임과 저장 흐름은 [구조 안내](docs/ARCHITECTURE.md)에 정리했습니다.

이번 구조 변경에서는 요청에 따라 빌드·테스트·실행 검증을 수행하지 않았습니다.
실행 중인 이전 JAR에는 이 변경이 반영되지 않으며, 새 JAR 생성 후 재시작해야 적용됩니다.
