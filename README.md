# Backend Study

백엔드 기술의 내부 동작을 이해하고, 부하·장애 상황을 직접 재현해 개선 효과를 수치로 확인하는 실습형 스터디입니다.

## 목표

- Java/Spring 기반 서비스의 병목과 장애 원인을 설명할 수 있다.
- DB, Redis, Kafka의 정합성·성능·장애 대응 트레이드오프를 실험으로 검증한다.
- TPS, p95/p99, 오류율, 커넥션 대기, Consumer Lag 등 측정값으로 개선 효과를 증명한다.
- 실험 코드와 재현 절차, 결과, 기술적 판단 근거를 포트폴리오로 남긴다.

> 학습 원칙: 개념 20% · 구현/실험 55% · 결과 분석/문서화 25%. 수치 자체보다 실험 조건과 해석의 신뢰성을 우선한다.

## 기술 스택 및 환경

| 구분 | 기본 환경 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.x |
| Build | Gradle Wrapper |
| Database | MySQL 8 |
| Cache / Coordination | Redis 7 |
| Messaging | Kafka |
| Metrics | Micrometer + Prometheus + Grafana |
| Load test | k6 |
| Container | Docker Compose |
| Test | JUnit 5, Mockito, Spring Boot Test, Testcontainers |
| Orchestration | kind 또는 minikube (후반부) |

- 기본 실습은 Java 17로 통일한다. Virtual Thread 비교 실험만 JDK 21 이상 별도 실행 환경에서 수행한다.
- 로컬 환경에서 재현할 수 있도록 의존 서비스는 Docker Compose로 실행한다.
- 각 실험은 가능하면 동일한 데이터·하드웨어·부하 조건으로 3회 이상 반복한다.
- 부하 테스트 전후의 데이터 초기화 방법과 실행 명령을 기록한다.

## 학습 로드맵

주당 5~7시간(하루 약 1시간, 주 5~7일)을 기준으로 한다. 각 주차는 이해도와 실험 결과에 따라 늘려도 된다. 전체 목표 기간은 약 14~18주이며, 핵심 구간(DB~관측성)에 시간을 우선 배분한다.

### Week 0 — 기반 환경 및 베이스라인 (2~3일)

- [ ] Spring Boot 기본 애플리케이션 생성 및 Actuator 설정
- [ ] Docker Compose로 MySQL, Redis, Prometheus, Grafana 실행
- [ ] Micrometer 메트릭 노출: JVM, HTTP, HikariCP
- [ ] k6 기본 부하 스크립트 작성
- [ ] 기준 시나리오의 TPS, p50/p95/p99, 오류율 측정
- [ ] 실험 환경(PC 사양, JVM 옵션, DB 설정, 데이터 건수) 기록

**산출물:** 실행 가능한 로컬 환경, Compose 파일, k6 스크립트, 베이스라인 결과

### Week 1 — DB 성능과 커넥션 풀

- [ ] 인덱스 유무 및 단일/복합 인덱스 성능 비교
- [ ] EXPLAIN으로 실행 계획 읽기, 필요하면 EXPLAIN ANALYZE 확인
- [ ] N+1 문제 재현 및 해결 전후 쿼리 수·응답 시간 비교
- [ ] HikariCP 풀 크기를 바꿔가며 TPS, 대기 시간, DB 부하 측정
- [ ] 커넥션 풀 고갈과 Pending Connection 관찰
- [ ] 트랜잭션 격리 수준과 Dirty/Non-repeatable/Phantom Read 실험
- [ ] 재고 차감 동시성: 비관적 락 vs 낙관적 락 vs 원자적 UPDATE
- [ ] 데드락 재현, 락 대기 및 정합성 확인

**핵심 질문:** 풀 크기를 늘렸는데 TPS가 오르지 않는다면 병목은 어디에 있는가?

**산출물:** 실행 계획 비교, 동시성 테스트, 풀 크기별 성능 표

### Week 2 — Redis 캐시와 캐시 장애

- [ ] Cache-aside 구현 및 TTL 설정
- [ ] Cache Hit/Miss 비율과 DB 부하 측정
- [ ] Cache Stampede 재현 및 대응: TTL jitter, 동시 갱신 제어
- [ ] Cache Penetration 재현 및 대응: Null 캐싱, Bloom Filter 개념/적용 조건
- [ ] 캐시 무효화와 DB-캐시 정합성 전략 비교
- [ ] TTL·키 설계에 따른 메모리 사용량 관찰

**핵심 질문:** 캐시 적중률이 높아도 데이터 정합성이 깨질 수 있는 이유는 무엇인가?

**산출물:** 캐시 적용 전후 성능 및 정합성 비교

### Week 3 — Redis 원자 연산과 분산 제어

- [ ] Redisson 분산락으로 재고 차감 구현
- [ ] DB 락과 Redis 분산락의 처리량·대기 시간·장애 특성 비교
- [ ] 락 TTL 만료, 작업 지연, 소유권 확인, 중복 해제 상황 테스트
- [ ] Fixed Window / Sliding Window / Token Bucket Rate Limiter 구현 또는 비교
- [ ] 선착순 쿠폰 수량 제어: Lua Script와 Sorted Set 등 자료구조 비교
- [ ] Redis 중단 실험: 캐시 기능과 핵심 기능의 Fallback 정책 검증
- [ ] MULTI/EXEC와 Lua Script의 원자성 및 동작 차이 정리

**핵심 질문:** Redis 락을 사용하면 DB 락보다 항상 안전하거나 빠른가?

**산출물:** 분산락 비교 보고서, Rate Limiter 구현, Redis 장애 대응 문서

### Week 4 — Kafka 기본과 전달 보장

- [ ] Broker, Topic, Partition, Consumer Group 구성
- [ ] 주문 이벤트 발행 및 소비
- [ ] Partition 수와 Consumer 수에 따른 처리량 변화
- [ ] Partition Key에 따른 분산 및 메시지 순서 확인
- [ ] Consumer 강제 종료 후 재시작 및 Rebalance 관찰
- [ ] Auto Commit / 수동 Commit 시점별 유실·중복 가능성 실험
- [ ] At-most-once / At-least-once / Exactly-once 의미와 범위 정리

**핵심 질문:** Kafka에서 순서는 어디까지 보장되며 병렬성은 무엇으로 제한되는가?

**산출물:** 메시지 흐름 코드, 종료·재시작 실험 로그

### Week 5 — Kafka 신뢰성 및 Outbox

- [ ] 멱등 컨슈머 및 중복 메시지 처리
- [ ] Retry Topic 또는 재시도 정책, DLQ 설계
- [ ] DB 커밋 후 메시지 발행 실패 상황 재현
- [ ] Outbox 패턴 구현 및 재처리 검증
- [ ] Outbox Polling과 CDC 방식의 개념·트레이드오프 비교
- [ ] Producer acks, linger.ms, batch.size 변경에 따른 처리량 비교
- [ ] Consumer Lag 측정 및 적체 해소 실험

**핵심 질문:** DB 트랜잭션과 메시지 발행 사이의 이중 쓰기 문제를 어떻게 다룰 것인가?

**산출물:** Outbox 구현, 중복/재시도/DLQ 테스트, Producer 튜닝 결과

### Week 6 — 장애 전파 방지와 요청 처리

- [ ] 외부 API 지연·오류를 가정한 Timeout 적용
- [ ] Retry, Circuit Breaker, Bulkhead 적용 및 동작 검증
- [ ] Retry Storm 재현: 재시도가 장애를 증폭하는 과정 관찰
- [ ] Tomcat 스레드 풀 포화 및 요청 대기 관찰
- [ ] 플랫폼 스레드와 Virtual Thread 비교 (JDK 21+ 별도 실행)
- [ ] Nginx로 Spring Boot 인스턴스 2~3대 로드밸런싱
- [ ] 다중 인스턴스에서 세션/상태 관리 방식 비교
- [ ] Graceful Shutdown 중 진행 중인 요청 처리 확인
- [ ] HTTP Keep-Alive 및 연결/읽기 Timeout 영향 확인

**핵심 질문:** 한 의존 서비스의 지연이 전체 서비스의 스레드와 커넥션을 어떻게 고갈시키는가?

**산출물:** 장애 주입 시나리오, 복구 결과, 스레드/요청 처리 비교

### Week 7 — 관측성과 병목 분석

- [ ] k6 Load / Stress / Spike / Soak 시나리오 작성
- [ ] Grafana 대시보드 구성: TPS, p95/p99, 오류율, CPU, Heap/GC
- [ ] HikariCP Active/Idle/Pending 및 DB 지표 시각화
- [ ] JFR로 CPU·메모리·스레드 병목 분석 (필요 시 async-profiler 추가)
- [ ] OpenTelemetry Tracing으로 요청 경로 및 지연 구간 추적
- [ ] 부하 단계별 최초 병목 지점과 한계 확인
- [ ] 개선 전후 동일 조건 재측정

**산출물:** “부하가 증가할 때 어디가 먼저 한계에 도달하는가” 분석 보고서

### Week 8 — Kubernetes 기초

- [ ] kind 또는 minikube 로컬 클러스터 구성
- [ ] Deployment / Service / ConfigMap / Secret 배포
- [ ] Liveness / Readiness Probe 정상·오설정 사례 비교
- [ ] Resource Request / Limit 설정
- [ ] Rolling Update와 배포 중 요청 처리 확인
- [ ] 로컬 Docker Compose와 Kubernetes 배포 구조 차이 정리

**산출물:** 배포 매니페스트와 Probe 실험 기록

### Week 9 — Kubernetes 스케일링과 장애

- [ ] HPA 적용 후 k6 부하로 Scale-out 관찰
- [ ] 스케일 아웃 반응 지연 및 최대 처리량 한계 분석
- [ ] CPU Limit Throttling 재현
- [ ] JVM 컨테이너 메모리 설정 및 OOMKilled 재현
- [ ] Pod 강제 종료 시 요청 유실 여부 확인
- [ ] Graceful Shutdown 및 종료 유예 시간 검증
- [ ] (선택) KEDA로 Kafka Consumer Lag 기반 스케일링

**산출물:** 스케일링·자원 제한·Pod 종료 실험 보고서

### Week 10~12 — 종합 프로젝트: 선착순 쿠폰 발급 시스템

**목표:** 순간적인 대량 요청에서도 쿠폰 수량 초과 발급과 사용자 중복 발급을 방지하고, 장애 상황에서 복구 가능한 시스템을 만든다.

- [ ] 요구사항 및 정합성 기준 정의: 총 발급량, 사용자당 발급 횟수, 중복 요청 처리
- [ ] 초기 설계 및 데이터 모델 작성
- [ ] 동기식 DB 처리로 첫 버전 구현 및 기준 부하 측정
- [ ] Redis 원자 연산 또는 Lua Script로 선착순 수량 제어
- [ ] Kafka 비동기 처리 및 DB 영속화
- [ ] 중복 메시지·재시도·DLQ·Outbox 적용 범위 결정
- [ ] 부하 테스트로 병목 발견 및 단계별 개선
- [ ] Redis/Kafka/애플리케이션 장애와 재시작 시나리오 검증
- [ ] 최종 아키텍처, 성능 결과, 트레이드오프 및 회고 작성

**주의:** “1만 TPS”는 목표 부하 또는 실험 입력값이지, 사전에 보장하는 성능 수치가 아니다. 실제 처리량과 정합성 결과를 측정해 기록한다.

**최종 산출물**
1. 아키텍처 다이어그램 및 요청 흐름
2. 실험 환경과 부하 테스트 조건
3. 초기 성능과 병목 분석
4. 개선 전후 TPS / p95 / p99 / 오류율 비교
5. 쿠폰 초과 발급·중복 발급 검증 결과
6. 장애 재현·복구 과정
7. 선택한 설계의 장단점과 남은 한계

## 공통 테스트 및 실험 규칙

- JUnit 5와 Mockito로 단위 테스트를 작성한다.
- Spring Boot Test와 Testcontainers로 DB/Redis/Kafka 연동 테스트를 재현한다.
- 동시성 테스트는 단일 요청 테스트와 분리하고, 경쟁 요청 수·반복 횟수·초기 데이터 상태를 기록한다.
- 성능 비교 시 한 번에 하나의 변수만 변경한다.
- 워밍업, 측정 시간, VU/요청량, 데이터 건수, JVM 옵션을 명시한다.
- 평균 응답 시간만 보지 말고 p95/p99와 오류율을 함께 확인한다.
- 성공한 결과뿐 아니라 실패한 실험과 예상과 달랐던 결과도 기록한다.

## 주제별 기록 템플릿

각 실험 디렉터리에 아래 내용을 README로 남긴다.

1. **목적:** 무엇을 확인하려는가?
2. **문제 상황:** 실무에서 어떤 문제가 될 수 있는가?
3. **환경:** 버전, 설정, 데이터 건수, 실행 조건
4. **가설:** 어떤 결과가 나올 것으로 예상하는가?
5. **실험 방법:** 재현 절차와 명령어
6. **결과:** 로그, 실행 계획, TPS, p95/p99, 오류율 등
7. **분석:** 예상과 실제 결과가 다른 이유는 무엇인가?
8. **트레이드오프:** 성능·정합성·복잡도·운영 비용 비교
9. **결론:** 어떤 조건에서 어떤 방식을 선택할 것인가?
10. **참고 자료:** 공식 문서 및 신뢰할 수 있는 자료

## 권장 디렉터리 구조

```text
study/
├── README.md
├── docker-compose.yml
├── docs/
│   ├── environment.md
│   ├── experiment-template.md
│   └── architecture/
├── week-00-environment/
├── week-01-database/
├── week-02-redis-cache/
├── week-03-redis-coordination/
├── week-04-kafka-basics/
├── week-05-kafka-reliability/
├── week-06-resilience/
├── week-07-observability/
├── week-08-kubernetes-basics/
├── week-09-kubernetes-scaling/
└── final-coupon-system/
```

각 주차는 독립 실행이 가능하도록 구성하고, 공통 인프라 설정은 루트 또는 docs에 모은다. 실험 코드가 커지면 주제별 Spring Boot 애플리케이션으로 분리한다.

## 포트폴리오 관점의 우선순위

1. **깊게 수행:** DB, 동시성, Redis, Kafka, 장애 대응, 관측성과 성능 분석
2. **기본 수준으로 수행:** Kubernetes 배포, Probe, HPA, 자원 제한
3. **선택 과제:** CDC, KEDA, async-profiler, RabbitMQ 비교 등

기술을 많이 나열하는 것보다, 하나의 문제를 재현하고 원인을 좁혀 개선한 근거를 남기는 것을 우선한다.
