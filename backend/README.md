# backend-lab

점심 1시간 × 11주. 미니 이커머스(상품 조회/주문/재고/선착순 쿠폰) 하나를 키워가며
Redis, Kafka, Resilience4j, Kubernetes 등을 **직접 부하를 걸어 터뜨려 보고** 트레이드오프를 기록하는 레포.

- Java 21 / Spring Boot 3.3 / Gradle / MySQL 8.4
- 실험마다 `experiments/NN-xxx/README.md`에 **가설 → 방법 → 결과 → 트레이드오프** 기록

## Quick Start
```bash
# 1) 인프라 (MySQL, Prometheus, Grafana)
cd infra/docker && docker compose up -d

# 2) 앱 실행 (호스트에서)
cd ../../app && ./gradlew bootRun      # 최초 1회: gradle wrapper (아래 참고)

# 3) 부하 테스트 (k6를 Docker로 실행)
cd .. && docker run --rm -i -e BASE_URL=http://host.docker.internal:8080 \
  grafana/k6 run - < loadtest/baseline.js
```
- Grafana: http://localhost:3000 (admin / admin)
- Prometheus: http://localhost:9090
- 앱 메트릭: http://localhost:8080/actuator/prometheus

> Gradle Wrapper가 없다면 `app/` 에서 `gradle wrapper --gradle-version 8.10` 실행
> (또는 start.spring.io에서 생성한 wrapper 파일 복사)

k6 옵션: `-e RATE=200 -e DURATION=2m -e API=list`
(Linux에서 host.docker.internal이 안 되면 `--add-host=host.docker.internal:host-gateway` 추가)

## 커리큘럼
[docs/curriculum.md](docs/curriculum.md)

## 실험 로그
| 주차 | 주제 | 링크 |
|---|---|---|
| 0 | 베이스라인 | [00-baseline](experiments/00-baseline/README.md) |
