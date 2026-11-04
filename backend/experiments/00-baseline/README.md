# 00. 베이스라인 측정

## 목표
인덱스·캐시 최적화를 적용하기 전 현재 애플리케이션의 기준 성능을 측정한다. 이 결과는 Week 1 이후 DB 인덱스와 커넥션 풀 실험의 비교 기준으로 사용한다.

## 사전 조건
- [ ] MySQL / Prometheus / Grafana 실행
- [ ] Spring Boot 앱 실행 및 상품 데이터 적재 완료
- [ ] /actuator/health 정상 확인
- [ ] Prometheus에서 앱 타깃이 UP인지 확인
- [ ] k6 실행 환경 확인

## 실험 시나리오
### 1. 단건 조회
k6 run -e BASE_URL=http://localhost:8080 -e API=single -e RATE=100 -e DURATION=1m loadtest/baseline.js

### 2. 카테고리 목록 조회 (category_id 인덱스 없음)
k6 run -e BASE_URL=http://localhost:8080 -e API=list -e RATE=100 -e DURATION=1m loadtest/baseline.js

Docker로 k6를 실행하는 경우 BASE_URL의 호스트로 host.docker.internal을 사용한다.

## 측정 조건
| 항목 | 값 |
|---|---|
| 측정 일시 | |
| OS / CPU / RAM | |
| Java / Spring Boot | Java 17 / 3.5.x |
| MySQL | 8.4 |
| 상품 데이터 건수 | 100,000 |
| Hikari maximum-pool-size | 10 |
| k6 RATE / DURATION | |
| 워밍업 시간 | |
| 반복 횟수 | 최소 3회 권장 |

## 결과
| API | 목표 도착률 | 실제 처리량(TPS) | p50 | p95 | p99 | 오류율 |
|---|---:|---:|---:|---:|---:|---:|
| GET /products/{id} | | | | | | |
| GET /products?category= | | | | | | |

## 관측 지표
- [ ] JVM Heap / GC
- [ ] HTTP 요청 수 및 응답 시간
- [ ] HikariCP Active / Idle / Pending
- [ ] MySQL CPU 및 Slow Query
- [ ] k6 dropped_iterations 여부

## 분석
- 가장 먼저 한계에 도달한 자원은 무엇인가?
- 목록 조회에서 인덱스 부재가 실행 계획과 응답 시간에 어떤 영향을 주는가?
- 목표 도착률과 실제 처리량에 차이가 있는가?
- 측정 결과에 영향을 줄 수 있는 환경 요인은 무엇인가?

Grafana 캡처, k6 출력, 실행 계획, 측정 조건과 분석을 이 문서에 첨부한다.
