# Book Search (PostgreSQL Only)

PostgreSQL의 Full Text Search(FTS)와 한국어 형태소 분석(textsearch_ko)을 활용해 도서를 검색하는 Spring Boot 애플리케이션입니다. 검색 엔진(Elasticsearch)이나
캐시/카운터 저장소(Redis) 없이 오직 PostgreSQL 하나로 검색과 인기 검색어 집계를 모두 해결하는 것을 목표로 했습니다.
또한 조회 모델이기에 일부러 JPA를 사용하지 않고 JDBC를 사용하였습니다. 현 프로젝트는 쿼리 모델만 담당한다는 가정으로 그리하였습니다.

- 베이스 URL: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger
- OpenAPI JSON: http://localhost:8080/api/v3/api-docs
  
## 가정 사항

읽기 전용 모델은 원본 데이터가 ETL이나 CDC를 거쳐 미리 평탄화된 형태로 제공된다 가정합니다.

## 프로젝트 설명

- 단일 데이터 스토어(PostgreSQL)로 검색(FTS)과 인기 검색어 집계를 함께 처리합니다.
- 한국어 검색을 위해 mecab-ko + mecab-ko-dic + textsearch_ko 확장을 사용하는 PostgreSQL 이미지를 사용합니다.
- 검색 파이프라인을 파서(Parser) → 플래너(Planner) → 실행기(Executor)로 분리하여 확장성을 고려했습니다.
- API는 도서 상세 조회, 질의 기반 검색, 인기 검색어 Top 10 조회를 제공합니다.

## 도메인 모델 설명

- 읽기 전용 모델(Read Model)
    - book_read_model 테이블에 도서 상세가 평탄화된 형태로 저장됩니다.
    - 검색 컬럼(search_vector)은 tsvector로 생성되며 GIN 인덱스를 추가하였습니다.
- 검색 키워드 집계
    - search_keywords 테이블에 키워드별 검색 횟수와 최근 검색 시간을 저장합니다.
    - 검색이 발생하면 ApplicationEvent를 발행하고, 비동기 핸들러가 증가 연산을 수행합니다.
- 주요 컴포넌트
    - Parser: SimpleQueryParser
        - 지원 연산자: OR(|), NOT(-). 공백은 토큰 구분자로 처리.
    - Planner: SimpleQueryPlanner
        - 쿼리 형태에 따라 SearchStrategy(SINGLE_TERM, OR_OPERATION, MUST_AND_NOT_OPERATION)를 결정.
    - Executor: PostgresFtsExpressionBuilder, PostgresFtsRepository, PostgresFtsQueryExecutor
        - Planner가 선택한 전략에 맞춰 to_tsquery('korean', ...) 표현식을 구성하고, ts_rank_cd로 랭킹합니다.
    - Facade: BookSearchFacade
        - 파싱 → 이벤트발행(인기검색어 집계) → 플래닝 → 실행까지 검색 오케스트레이션.
    - 인기 검색어: PostgresSearchKeywordRepository, KeywordSearchedEventHandler
        - INSERT ... ON CONFLICT으로 카운터를 안전하게 증가.

스키마 요약

- book_read_model
    - isbn13(PK), title, subtitle, description, image, author, translator, publisher, published_date, page_count
    - search_vector(tsvector, GENERATED ALWAYS), GIN 인덱스(idx_book_search_vector)
- search_keywords
    - keyword(PK), search_count(BIGINT), last_searched_at(TIMESTAMP)

## 실행 방법

- docker compose --profile local-dev up -d
    - 한국어 분석 확장을 포함한 Postgres 이미지를 사용합니다. 빌드 시간이 오래 걸려 미리 빌드하고 도커 허브에 푸시 해 두었습니다.
    - 접속 정보(기본값)
        - DB: jdbc:postgresql://localhost:5432/book, user: nilgil, password: secret

## API 문서 및 엔드포인트

- Swagger UI: http://localhost:8080/api/swagger
- OpenAPI: http://localhost:8080/api/v3/api-docs

주요 엔드포인트

- GET /api/books/{isbn13}
    - 예: /api/books/9788991000155
- GET /api/books/search?q={query}&page={0-based}&size={n}
    - 예: /api/books/search?q=java|스프링&page=0&size=10
    - OR: |, NOT: - 지원 (예: 스프링-입문)
- GET /api/books/search/top10
    - 최근 집계된 인기 검색어 상위 10개.

## 기술 스택 및 선택 이유

- Java 21, Spring Boot 3.5
    - Java 버전 중 최신 LTS를 사용하였습니다.
- Spring Web, Spring Data JDBC
    - 읽기 전용 모델에 적합하고 단순한 데이터 접근. JPA의 복잡한 영속성 컨텍스트 불필요.
- PostgreSQL 17 + textsearch_ko (mecab-ko)
    - 단일 DB로 검색과 통계를 모두 처리. 관리 비용 절감.
    - 한국어 형태소 분석 + GIN 인덱스로 실용적인 검색 성능 확보.
- springdoc-openapi-starter-webmvc-ui
    - 자동 문서화 및 손쉬운 UI 제공.
- Docker / Docker Compose
    - 재현 가능한 개발 환경, 손쉬운 배포/구동.
- Test: JUnit, H2(in-memory)
    - 단위 테스트 속도 확보. (주: FTS 고유 기능은 별도 통합 테스트가 필요할 수 있음)

## 아키텍처 결정 사항

- PostgreSQL 단일 스토어 전략
    - 검색(FTS)과 인기 검색어 집계를 모두 Postgres로 처리해 운영 복잡성과 비용을 낮춤.
- 한국어 검색
    - textsearch_ko 확장과 Generated Column(tsvector) + GIN 인덱스로 구현.
- 읽기 전용 모델
    - ETL/CDC로 사전에 평탄화된 read model(book_read_model)만 제공된다는 가정하에 설계.
- 검색 파이프라인 분리
    - Parser → Planner → Executor로 분리하여 검색 문법/전략/실행을 독립적으로 확장 가능.
- 이벤트 기반 인기 검색어 집계
    - 검색 시 ApplicationEvent를 발행하고 비동기 핸들러가 카운터 증가(경합 시 ON CONFLICT).
- Spring Data JDBC 채택
    - 단순 쿼리 중심, 러닝커브/오버헤드 최소화.

## 문제 해결 중 고민 과정

- 의도적으로 PostgreSQL만 사용
    - 인기 검색어는 Redis, 검색엔진은 Elasticsearch를 사용할 수도 있으나, 인프라 관리 비용과 운영 복잡도를 고려해 하나의 DB로 해결하는 방향을 선택했습니다.
    - 트래픽 규모가 크지 않거나 비용 제약이 있는 환경에서 현실적인 대안이 될 수 있습니다.
- 검색 품질과 복잡도의 균형
    - 한국어 형태소 분석을 적용해 품질을 확보하되, 쿼리 문법은 OR(|), NOT(-) 정도로 제한해 구현 복잡도를 제어.
- 데이터 모델링
    - 검색 대상 필드별 가중치(A/B/D)를 부여하고 ts_rank_cd로 정렬.
    - Generated Column으로 인덱스 일관성을 단순화(트리거보다 유지보수 용이).
- 일관성 vs. 성능
    - 인기 검색어는 비동기로 증가시켜 검색 경로(latency)에 영향 최소화. 집계는 eventual consistency 허용.

## 환경/구성 참고

- 컨텍스트 경로: /api
- 프로필
    - local: localhost DB에 연결. 앱 실행 시 schema.sql, search.sql 자동 적용.
    - local-dev: Docker Compose의 postgres 호스트로 연결(컨테이너 간 네트워크).
- Compose
    - postgres: nilgil183/pg_search_ko:1.0 (mecab-ko + textsearch_ko 포함)
    - search: 애플리케이션 컨테이너(프로필 local-dev)

## 한계와 향후 개선

- 현재 OR, NOT만 지원. 괄호, MUST(+), 정확도 향상 기능 추가 여지.
- 인기 검색어 API는 순위만 반환. 검색 횟수/기간별 통계, 상위 N 기간 필터 추가 가능.
- 통합 테스트에서 실제 PostgreSQL(FTS 포함)과의 E2E 검증 확대 필요.
- 고부하 환경에서는 Redis 캐시, Elasticsearch 도입을 검토(운영 비용/복잡도와 트레이드오프).

## 가정 사항

- 읽기 전용 모델은 원본 데이터가 ETL이나 CDC를 거쳐 미리 평탄화된 형태로 제공됩니다.
