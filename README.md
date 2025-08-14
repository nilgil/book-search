# Book Search (PostgreSQL Only)

PostgreSQL의 Full Text Search(FTS)와 한국어 형태소 분석 [textsearch_ko](https://github.com/i0seph/textsearch_ko)를 활용해 도서를 검색하는 Spring Boot 애플리케이션입니다. 
검색 엔진(Elasticsearch)이나 캐시/카운터 저장소(Redis) 없이 오직 PostgreSQL 하나로 검색과 인기 검색어 집계를 모두 해결하는 것을 목표로 했습니다.
<br><br>

## 프로젝트 요약

- 단일 데이터 스토어(PostgreSQL)로 일반 조회, 검색(FTS), 인기 검색어 집계를 함께 처리합니다.
- 한국어 검색을 위해 PostgreSQL17 이미지에 mecab-ko + mecab-ko-dic + textsearch_ko 확장을 추가하여 빌드한 이미지를 사용합니다.
    - [nilgil183/pg_search_ko](https://hub.docker.com/layers/nilgil183/pg_search_ko/latest/images/sha256-03a3f8206a135bb00fdcba3720eafef3b35cf729685ad9780e70ea3d62c50359) : 프로젝트 내 postgres/Dockerfile을 빌드한 결과입니다. 빌드가 오래 걸려 미리 빌드하고 도커 허브에 푸시 해 두었습니다.
- 검색 파이프라인을 파서(Parser) → 플래너(Planner) → 실행기(Executor)로 분리하여 각각 확장될 수 있도록 설계하였습니다.
- API는 도서 상세 조회, 질의 기반 검색, 인기 검색어 Top 10 조회를 제공합니다.
<br>

## 가정 사항

도서 조회에 사용된 읽기 전용 모델은 원본 데이터가 ETL이나 CDC를 거쳐 미리 평탄화된 형태로 제공된다고 가정합니다.
<br><br>

## 도메인 모델 설명

- `BookRow`(읽기 전용 모델) : book_read_model 테이블에 도서 상세가 평탄화된 형태로 저장됩니다.
- `Isbn13` : isbn13, isbn10 형태의 인풋을 모두 지원하는 값 객체입니다. 내부적으로 isbn13 형태로 데이터를 관리하며, DB에는 isbn13 형태로 연동합니다.
- 쿼리 모델 : [Apache Lucene](https://github.com/apache/lucene)의 BooleanQuery 등 쿼리 모델들을 참고하여 현재 구조에 맞게 간소화하여 활용했습니다.
<br>

### 주요 컴포넌트
- Parser: `QueryParser`
    - 문자열 형태에 따라 `TermQuery`, `Clause` 등의 형태로 정제.
- Planner: `QueryPlanner`
    - 쿼리 형태에 따라 `SearchStrategy`(`SINGLE_TERM`, `OR_OPERATION`, `MUST_AND_NOT_OPERATION`)를 결정.
- Executor: `PostgresFtsExpressionBuilder`, `PostgresFtsRepository`, `PostgresFtsQueryExecutor`
    - Planner가 선택한 전략에 맞춰 to_tsquery('korean', ...) 표현식을 구성하고, ts_rank_cd로 랭킹합니다.
- Facade: `BookSearchFacade`
    - 파싱 → 이벤트발행(인기검색어 집계) → 플래닝 → 실행까지 검색 오케스트레이션.
- 인기 검색어: `SearchKeywordRepository`, `KeywordSearchedEventHandler`
    - `INSERT ... ON CONFLICT`으로 카운터를 안전하게 증가.
<br>

## 실행 방법
`docker compose --profile local-dev up -d`
- DB 접속 정보
    - url: `jdbc:postgresql://localhost:5432/book`
    - user: `nilgil`
    - password: `secret`
<br>

## API 문서 및 엔드포인트

- Base Url: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger
- OpenAPI: http://localhost:8080/api/v3/api-docs
<br>

### 주요 엔드포인트

- `GET /api/books/{isbn13}`
    - 예: `/api/books/9788991000155`
- `GET /api/books/search?q={query}&page={0-based}&size={n}`
    - 예: `/api/books/search?q=java|스프링&page=0&size=10`
    - OR: |, NOT: - 지원 (예: 스프링-입문)
- `GET /api/books/search/top10`
    - 최근 집계된 인기 검색어 상위 10개.
<br>

## 기술 스택 및 선택 이유

### Spring Data JDBC
- 읽기 전용 모델에 적합하고 단순한 데이터 접근에 어울립니다. JPA에 비해 성능적으로 우수합니다.
- 쿼리 중심, 조회만 하는 경우 JPA의 영속성 컨텍스트의 이점이 많이 줄어 평소에는 잘 사용하지 않지만 상황에 맞게 JDBC를 사용하고자 했습니다.

### PostgreSQL
- 단일 데이터 스토어로 원천 데이터 관리, 검색, 통계를 모두 처리할 수 있어 운영 관점에서 복잡성과 비용을 낮출 수 있다는 이점이 있습니다.
- 트래픽이 폭발적으로 늘어나기 전까지 버티는 용도로 나쁘지 않은 것 같고, 다른 방안들은 이미 많은 예제들이 존재하기에 선택한 방안입니다.

### FTS + textsearch_ko
- PostgreSQL의 기본 FTS는 한국어를 지원하지 않기에 한국어 형태소 분석을 지원하기 위해 여러 확장 검토
- FTS가 지원되는, 검색 엔진의 역할을 완벽히 수행할 수 있는 확장 중 선택 -> like 성능을 개선한 pg_bigm과 같은 방식은 배제
<br>

## 아키텍처 결정 사항

### 멀티 모듈 미사용
고의적으로 사용하지 않았습니다. 기술 의존성 또는 레이어 기준으로 나눌 만한 포인트들은 몇 있지만 제가 보기에 현재 프로젝트는 너무 작고, 어떻게 확장될지 확실치 않은 상황에서 섣부른 판단을 하지 않으려는 의도를 담았습니다.

### Search의 모듈성
- 도서 쿼리를 처리하는 메인 모듈에서 Search 관련 코드들을 격리해야겠다는 생각으로 메인 모듈에서 서치 모듈을 가져다 쓴다는 느낌으로 설계하였습니다.
    - 당장은 멀티 모듈을 구성하지 않았지만 모듈 분리를 한다면 가장 먼저 분리되어야 할 영역입니다.
- 검색 파이프라인 분리 : Parser → Planner → Executor로 흐름을 구분하여 검색 문법/전략/실행을 독립적으로 확장 가능하도록 하였습니다.
<br>

## 문제 해결 중 고민 과정

### 쿼리 모델 추상화
앞으로의 우리의 쿼리 DSL이 확장될 것을 고려하여 DSL을 우리만의 추상화된 쿼리 모델로 잘 정제할 필요가 있었습니다. 정말 간단하게 요구 사항만 처리할까도 생각했지만 확장 가능성이 너무 높은 영역이라고 생각이 들었습니다. 그래서 여러 오픈소스들을 찾아보며 어떤 방식으로 자신들만의 검색 DSL을 추상화하는지 확인해 보았습니다. Antlr를 사용하는 오픈소스도 있었는데 처음엔 저도 이걸 써 보려 하다 지금 요구 사항에 비해 너무 과한 복잡도가 포함되는 것 같다는 생각을 하여 배제하였습니다. 이후에는 이진 트리 구조로 간단하게 쿼리를 만들까도 생각했으나, 검색 엔진에서 표준이라 불리는 Apache Lucene의 BooleanQuery, Clause, Occur와 같은 개념들이 확장성을 보장하면서도 간단하게 바로 반영할 수 있겠다는 생각이 들어 택하게 해당 모델들을 간소화하여 내재화하게 되었습니다.

### 이벤트 기반 인기 검색어 집계
인기 검색어 집계를 어디서 해야 할지 고민되었습니다. 검색어 집계는 개별 검색어로 파싱된 이후 처리해야 합니다. 먼저 AOP를 사용하여 QueryParser에 After로 붙어서 쿼리 파싱 후 집계하도록 붙일까 생각했습니다. 그런데 검색어를 집계하는 것이 파서에 붙는 게 맞나? 라는 생각을 하게 되었고, 검색이라는 인터페이스를 제공하는 BookSearchFacade에서 처리하는 것이 좋겠다고 결론을 내렸습니다.  

검색어 집계가 검색에 영향을 주지 않았으면 했습니다. BookSearchFacade 내에서 키워드 검색이 되었다는 이벤트를 발행하고, 검색 요청에 영향을 주지 않도록 비동기 핸들러가 이벤트를 받아 카운터 증가하도록 하였습니다. 집계는 eventual consistency를 허용해도 괜찮은 영역이라 판단하였습니다.

### 검색 성능을 위한 가중치 부여
더 좋은 검색 결과를 제공하기 위해 컬럼별로 가중치를 다르게 할당하였습니다. 가중치 기본값 A=1.0,B=0.4,C=0.2,D=0.1 을 변경하지 않고 사용자가 검색으로 찾고자 하는 영역들을 중요도에 따라 그룹을 구분하였습니다. 제가 생각한 중요도의 결과는 title > author >= subtitle > description > publisher > translator 입니다.
<br><br>

## 한계와 향후 개선

- 현재 OR, NOT만 지원. 괄호, MUST(+) 등 기능 추가 여지.
- 현재 고정된 가중치에 의해 검색됨. 검색 시 가중치 전달하는 동적 기능 확장 가능.
- 인기 검색어 API는 순위만 반환. 검색 횟수/기간별 통계, 상위 N 기간 필터 추가 가능.
- 통합 테스트에서 실제 PostgreSQL(FTS 포함)과의 E2E 검증 확대 필요.
- 고부하 환경에서는 Redis 캐시, Elasticsearch 도입을 검토(운영 비용/복잡도와 트레이드오프).
