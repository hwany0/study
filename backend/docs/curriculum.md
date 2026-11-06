# 백엔드 실험 커리큘럼

주당 5~7시간(하루 약 1시간)을 기준으로 한다. 전체 목표 기간은 약 14~18주이며, 주차는 학습 순서이지 반드시 달력상 1주 안에 끝내야 한다는 뜻은 아니다.

## 학습 원칙
- 개념 20% · 구현/실험 55% · 결과 분석/문서화 25%
- 성능 수치보다 재현 가능한 조건과 결과 해석을 우선한다.
- 비교 실험은 한 번에 하나의 변수만 바꾸고, 워밍업 후 최소 3회 반복한다.
- TPS와 목표 도착률을 구분하고 p95/p99, 오류율, CPU, 메모리, 커넥션 대기 등을 함께 본다.
- Java 17을 기본으로 사용한다. Virtual Thread는 JDK 21 이상 별도 환경에서 비교한다.

## Week 0 — 기반 환경 및 베이스라인 (2~3일)
- [ ] Spring Boot 애플리케이션 및 Actuator 실행
- [ ] Docker Compose로 SQL Server 2022, Prometheus, Grafana 구성
- [ ] Micrometer 메트릭: JVM, HTTP, HikariCP 확인
- [ ] k6로 단건/목록 API 부하 테스트
- [ ] 베이스라인 TPS, p50/p95/p99, 오류율 기록
- [ ] PC 사양, JVM 옵션, DB 설정, 데이터 건수 기록

산출물: 실행 가능한 환경, k6 스크립트, 베이스라인 결과

## Week 1 — DB 성능과 커넥션 풀
- [ ] 인덱스 유무 및 단일/복합 인덱스 비교
- [ ] SQL Server Actual Execution Plan 및 SET STATISTICS IO, TIME 분석
- [ ] N+1 재현 및 fetch 전략 변경 전후 비교
- [ ] HikariCP 풀 크기별 TPS, Pending, Active, DB 부하 비교
- [ ] 커넥션 풀 고갈 및 대기 재현
- [ ] 트랜잭션 격리 수준과 Dirty / Non-repeatable / Phantom Read 실험
- [ ] 재고 차감: 비관적 락 vs 낙관적 락 vs 원자적 UPDATE
- [ ] 데드락과 락 대기 재현 및 정합성 검증

핵심 질문: 풀 크기를 키웠는데 TPS가 오르지 않는다면 병목은 어디에 있는가?

## Week 2 — Redis 캐시
- [ ] Cache-aside 및 TTL 구현
- [ ] Hit/Miss 비율과 DB 부하 측정
- [ ] Cache Stampede 재현 및 TTL jitter / 동시 갱신 제어
- [ ] Cache Penetration 재현 및 Null 캐싱 / Bloom Filter 검토
- [ ] 캐시 무효화와 DB-캐시 정합성 전략 비교
- [ ] TTL·키 설계에 따른 메모리 사용량 관찰

핵심 질문: 캐시 적중률이 높아도 정합성이 깨질 수 있는 이유는 무엇인가?

## Week 3 — Redis 원자 연산과 분산 제어
- [ ] Redisson 분산락으로 재고 차감 구현
- [ ] DB 락과 Redis 락의 처리량·대기 시간·장애 특성 비교
- [ ] 락 TTL 만료, 작업 지연, 소유권 확인, 중복 해제 실험
- [ ] Fixed Window / Sliding Window / Token Bucket Rate Limiter 비교
- [ ] 선착순 쿠폰 수량 제어: Lua Script 및 Sorted Set 실험
- [ ] Redis 장애 시 캐시와 핵심 기능의 Fallback 검증
- [ ] MULTI/EXEC와 Lua Script의 원자성 및 차이 정리

핵심 질문: Redis 분산락은 어떤 조건에서 안전하며 어떤 실패 가능성이 있는가?

## Week 4 — Kafka 기본과 전달 보장
- [ ] Broker / Topic / Partition / Consumer Group 구성
- [ ] 주문 이벤트 발행 및 소비
- [ ] Partition 수와 Consumer 수에 따른 처리량 비교
- [ ] Partition Key에 따른 분산 및 메시지 순서 확인
- [ ] Consumer 강제 종료·재시작 및 Rebalance 관찰
- [ ] Auto Commit / 수동 Commit 시점별 유실·중복 가능성 실험
- [ ] At-most-once / At-least-once / Exactly-once 보장 범위 정리

핵심 질문: 메시지 순서는 어디까지 보장되며 병렬성은 무엇으로 제한되는가?

## Week 5 — Kafka 신뢰성 및 Outbox
- [ ] 멱등 컨슈머 및 중복 메시지 처리
- [ ] 재시도 정책, Retry Topic, DLQ 설계
- [ ] DB 커밋 후 메시지 발행 실패 상황 재현
- [ ] Outbox 패턴 구현 및 재처리 검증
- [ ] Outbox Polling과 CDC 방식의 개념·트레이드오프 비교
- [ ] Producer acks / linger.ms / batch.size 튜닝 및 처리량 비교
- [ ] Consumer Lag 측정 및 적체 해소 실험

핵심 질문: DB 트랜잭션과 메시지 발행 사이의 이중 쓰기 문제를 어떻게 다룰 것인가?

## Week 6 — 장애 전파 방지와 요청 처리
- [ ] 외부 API 지연·오류를 가정한 Timeout 적용
- [ ] Retry / Circuit Breaker / Bulkhead 적용 및 검증
- [ ] Retry Storm 재현 및 재시도 정책의 부작용 관찰
- [ ] Tomcat 스레드 풀 포화와 요청 대기 관찰
- [ ] 플랫폼 스레드와 Virtual Thread 비교 (JDK 21+ 별도 환경)
- [ ] Nginx로 Spring Boot 인스턴스 2~3대 로드밸런싱
- [ ] 다중 인스턴스 세션·상태 관리 방식 비교
- [ ] Graceful Shutdown 중 진행 중 요청 처리 확인
- [ ] HTTP Keep-Alive 및 연결/읽기 Timeout 영향 확인

핵심 질문: 외부 의존 서비스 지연이 애플리케이션 스레드와 커넥션을 어떻게 고갈시키는가?

## Week 7 — 관측성과 병목 분석
- [ ] k6 Load / Stress / Spike / Soak 시나리오 작성
- [ ] Grafana 대시보드: TPS, p95/p99, 오류율, CPU, Heap/GC
- [ ] HikariCP Active / Idle / Pending 및 DB 지표 시각화
- [ ] JFR로 CPU·메모리·스레드 병목 분석 (필요 시 async-profiler)
- [ ] OpenTelemetry Tracing으로 요청 경로와 지연 구간 추적
- [ ] 부하 단계별 최초 병목 지점 및 한계 확인
- [ ] 동일 조건에서 개선 전후 재측정

산출물: 부하가 증가할 때 어디가 먼저 한계에 도달하는가 분석 문서

## Week 8 — Kubernetes 기초
- [ ] kind 또는 minikube 클러스터 구성
- [ ] Deployment / Service / ConfigMap / Secret 배포
- [ ] Liveness / Readiness Probe 정상·오설정 사례 비교
- [ ] Resource Request / Limit 설정
- [ ] Rolling Update 중 요청 처리 확인
- [ ] Docker Compose와 Kubernetes 배포 구조 차이 정리

## Week 9 — Kubernetes 스케일링과 장애
- [ ] HPA 적용 후 k6 부하로 Scale-out 관찰
- [ ] 스케일 아웃 반응 지연 및 처리량 한계 분석
- [ ] CPU Limit Throttling 재현
- [ ] JVM 컨테이너 메모리 설정 및 OOMKilled 재현
- [ ] Pod 강제 종료 시 요청 유실 여부 확인
- [ ] Graceful Shutdown 및 종료 유예 시간 검증
- [ ] (선택) KEDA로 Kafka Consumer Lag 기반 스케일링

## Week 10~12 — 종합 프로젝트: 선착순 쿠폰 발급 시스템
목표: 순간적인 대량 요청에서도 쿠폰 초과 발급과 사용자 중복 발급을 방지하고 장애 상황에서 복구 가능한 시스템을 구현한다.
- [ ] 정합성 기준 정의: 총 발급량, 사용자당 발급 횟수, 중복 요청 처리
- [ ] 초기 동기식 DB 구현 및 기준 부하 측정
- [ ] Redis 원자 연산 또는 Lua Script로 선착순 수량 제어
- [ ] Kafka 비동기 처리 및 DB 영속화
- [ ] 중복 메시지·재시도·DLQ·Outbox 적용 범위 결정
- [ ] 부하 테스트로 병목 발견 및 단계별 개선
- [ ] Redis / Kafka / 애플리케이션 장애와 재시작 검증
- [ ] 최종 아키텍처, 성능 결과, 트레이드오프 및 회고 작성

주의: 1만 TPS는 목표 부하 또는 실험 입력값이다. 실제 처리량과 정합성 결과를 측정해 기록하며 사전에 성능을 보장하는 표현으로 사용하지 않는다.

### 최종 산출물
1. 아키텍처 다이어그램 및 요청 흐름
2. 실험 환경과 부하 테스트 조건
3. 초기 성능과 병목 분석
4. 개선 전후 TPS / p95 / p99 / 오류율 비교
5. 쿠폰 초과 발급·중복 발급 검증 결과
6. 장애 재현·복구 과정
7. 기술 선택의 장단점과 남은 한계

## 공통 테스트 전략
- JUnit 5와 Mockito로 단위 테스트 작성
- Spring Boot Test와 Testcontainers로 DB / Redis / Kafka 통합 테스트
- 동시성 테스트는 경쟁 요청 수, 반복 횟수, 초기 데이터 상태 기록
- 성능 비교는 한 번에 하나의 변수만 변경
- 워밍업, 측정 시간, VU 또는 도착률, 데이터 건수, JVM 옵션 명시
- 평균 응답 시간뿐 아니라 p95/p99와 오류율 확인

## 포트폴리오 우선순위
1. 깊게 수행: DB, 동시성, Redis, Kafka, 장애 대응, 관측성과 성능 분석
2. 기본 수준: Kubernetes 배포, Probe, HPA, 자원 제한
3. 선택 과제: CDC, KEDA, async-profiler, RabbitMQ 비교

기술을 많이 나열하는 것보다 문제를 재현하고 원인을 좁혀 개선한 근거를 남기는 것을 우선한다.

실험 문서는 [공통 템플릿](../experiments/_template.md)을 사용한다.
