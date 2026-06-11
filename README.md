# 업무 승인 프로세스 REST API

실무에서 직접 설계·구현한 다단계 업무 승인 프로세스를 Spring Boot REST API로 재구현한 포트폴리오 프로젝트입니다.

---

## 만든 이유

한국수력원자력 프로젝트에서 승인 절차가 전혀 정의되지 않은 상태에서, 현업과 협의하여 승인 흐름을 직접 설계하고 구현했습니다.
당시 Spring Framework + JSP 환경이었던 것을 Spring Boot + REST API 방식으로 다시 만들었습니다.

---

## 기술 스택

| | |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Build | Maven |
| Database | H2 (인메모리) |
| ORM | Spring Data JPA |
| API 문서 | Swagger UI |

---

## 승인 흐름

업무 항목은 아래 순서로만 상태가 바뀝니다. 순서를 건너뛰거나 권한이 없는 사용자가 처리하면 에러가 납니다.

```
작성(DRAFT)
  → 승인 요청(SUBMITTED)          [신청자]
  → 1단계 검토 중(STAGE1_REVIEW)   [1단계 승인자]
  → 1단계 승인(STAGE1_APPROVED)   [1단계 승인자]
  → 2단계 검토 중(STAGE2_REVIEW)   [2단계 승인자]
  → 최종 승인(APPROVED)           [2단계 승인자]

검토 중 단계에서는 반려(REJECTED) 가능
```

---

## API 목록

**업무 항목**

| 메서드 | URL | 설명 |
|--------|-----|------|
| POST | `/api/work-items` | 업무 항목 생성 |
| GET | `/api/work-items` | 목록 조회 |
| GET | `/api/work-items/{id}` | 단건 조회 |

**승인 처리**

| 메서드 | URL | 설명 |
|--------|-----|------|
| POST | `/api/approvals/{id}/submit` | 승인 요청 |
| POST | `/api/approvals/{id}/review` | 검토 시작 |
| POST | `/api/approvals/{id}/approve` | 승인 |
| POST | `/api/approvals/{id}/reject` | 반려 |
| GET | `/api/approvals/{id}/history` | 승인 이력 조회 |

---

## 실행 방법

```bash
# 빌드
mvn clean package

# 실행
java -jar target/approval-system-0.0.1-SNAPSHOT.jar
```

실행 후 접속:
- Swagger UI (API 테스트): http://localhost:8080/swagger-ui.html
- H2 DB 콘솔: http://localhost:8080/h2-console

---

## 테스트용 초기 데이터

앱 실행 시 아래 사용자가 자동 생성됩니다.

| userId | 이름 | 역할 |
|--------|------|------|
| 1 | 홍길동 | 신청자 |
| 2 | 김철수 | 1단계 승인자 |
| 3 | 이영희 | 2단계 승인자 |

```bash
# 순서대로 실행하면 전체 흐름 확인 가능

# 1. 업무 항목 생성
curl -X POST http://localhost:8080/api/work-items \
  -H "Content-Type: application/json" \
  -d '{"title":"설계 도면 검토 요청","content":"2차 도면 승인 요청건","applicantId":1}'

# 2. 승인 요청
curl -X POST http://localhost:8080/api/approvals/1/submit \
  -H "Content-Type: application/json" \
  -d '{"userId":1}'

# 3. 1단계 검토 시작
curl -X POST http://localhost:8080/api/approvals/1/review \
  -H "Content-Type: application/json" \
  -d '{"userId":2}'

# 4. 1단계 승인
curl -X POST http://localhost:8080/api/approvals/1/approve \
  -H "Content-Type: application/json" \
  -d '{"approverId":2,"comment":"검토 완료, 이상 없음"}'

# 5. 최종 승인
curl -X POST http://localhost:8080/api/approvals/1/approve \
  -H "Content-Type: application/json" \
  -d '{"approverId":3,"comment":"최종 승인"}'

# 6. 이력 조회
curl http://localhost:8080/api/approvals/1/history
```
