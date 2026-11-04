# 00. 베이스라인
- 목표: 인덱스/캐시 없는 상태에서의 TPS·지연 시간 기준점 확보

## 체크리스트
- [ ] docker compose up 후 Grafana에서 Prometheus 데이터소스 확인
- [ ] /products/{id} 단건 조회 k6 실행 → TPS, p95 기록
- [ ] /products?category=X (인덱스 없음) 실행 → 느린 쿼리 확인
- [ ] HikariCP active/pending 메트릭 확인

## 결과
| API | RPS | 실제 TPS | p95 | 에러율 |
|---|---|---|---|---|
| GET /products/{id} | | | | |
| GET /products?category= | | | | |
