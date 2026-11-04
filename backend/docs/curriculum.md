# 커리큘럼 (11주)

| 주차 | 주제 | 핵심 실험 |
|---|---|---|
| 0 | 기반 세팅 | Compose + Prometheus/Grafana + k6 베이스라인 TPS |
| 1 | DB & 커넥션 풀 | 인덱스/EXPLAIN, N+1, HikariCP 사이즈별 TPS, 재고 차감(비관/낙관/원자적 UPDATE) |
| 2 | Redis 캐시 | Cache-aside, Stampede, Penetration, 정합성 |
| 3 | Redis 활용 | Redisson 분산락, Rate Limiter 3종, 선착순 쿠폰(Lua), Redis 장애 Fallback |
| 4 | Kafka 기본 | 파티션/컨슈머 수별 처리량, 리밸런싱, offset 커밋 방식 |
| 5 | Kafka 심화 | 멱등 컨슈머, DLQ, Outbox, Producer 튜닝, Lag |
| 6 | 장애 전파 방지 | Timeout/Retry/CB/Bulkhead, Retry Storm, Virtual Thread, Nginx LB |
| 7 | 관측성 | k6 Load/Stress/Spike/Soak, Grafana, JFR, OpenTelemetry |
| 8 | K8s 기초 | Probe, Resource, Rolling Update |
| 9 | K8s 스케일링 | HPA, Throttling, OOMKilled, Graceful Shutdown, (KEDA) |
| 10 | 종합 | 선착순 쿠폰 1만 TPS 시나리오, 회고 |

## 점심 1시간 템플릿
- 0~10분 가설 / 10~40분 구현·실험 / 40~55분 측정·캡처 / 55~60분 README 정리·커밋

## 실험 README 템플릿
`experiments/_template.md` 참고
