# backend-lab

미니 이커머스(상품 조회 → 재고/주문 → 선착순 쿠폰)를 하나의 실험 도메인으로 확장하며 DB, Redis, Kafka, 장애 대응, 관측성, Kubernetes를 검증하는 실습 프로젝트입니다.

## 기술 스택
- Java 17 / Spring Boot 3.5.x / Gradle
- MySQL 8.4
- Redis 7 (Week 2~3), Kafka (Week 4~5)
- Micrometer + Prometheus + Grafana, k6
- JUnit 5 / Mockito / Testcontainers (테스트 실습 단계에서 확장)

기본 애플리케이션은 Java 17로 유지합니다. Virtual Thread 비교 실험은 JDK 21 이상 별도 환경에서 수행합니다.

## 디렉터리 구조

    backend/
    ├── app/                 # Spring Boot 애플리케이션
    ├── docs/                # 커리큘럼 및 공통 문서
    ├── experiments/         # 주제별 실험 기록
    ├── infra/docker/        # MySQL, Prometheus, Grafana
    ├── infra/k8s/           # Kubernetes 실습
    └── loadtest/            # k6 스크립트

## 사전 준비
- JDK 17
- Docker Desktop 및 Docker Compose v2
- Gradle은 최초 Wrapper 생성 시에만 필요

현재 저장소에는 Gradle Wrapper 파일이 없습니다. 최초 1회 Gradle이 설치된 환경에서 아래 명령으로 Wrapper를 생성하고 커밋하는 것을 권장합니다.

    cd backend/app
    gradle wrapper --gradle-version 8.14

이후 macOS/Linux는 ./gradlew, Windows는 gradlew.bat를 사용합니다.

## Quick Start

1. MySQL, Prometheus, Grafana 실행

    cd backend/infra/docker
    docker compose up -d
    docker compose ps

2. Spring Boot 실행 (호스트에서 실행)

    cd ../../app
    ./gradlew bootRun

Windows PowerShell에서는 gradlew.bat bootRun을 실행합니다.

3. 접속 확인
- 상품 목록: http://localhost:8080/products?category=1
- 단건 조회: http://localhost:8080/products/1
- Actuator Health: http://localhost:8080/actuator/health
- Prometheus Metrics: http://localhost:8080/actuator/prometheus
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (초기 계정 admin / admin)

앱 시작 시 lab.seed.enabled=true이면 상품 데이터가 비어 있을 때 기본 100,000건을 적재합니다. 최초 실행은 적재가 끝날 때까지 시간이 걸릴 수 있습니다.

4. k6 베이스라인 실행 (backend 디렉터리에서)

    docker run --rm -i -e BASE_URL=http://host.docker.internal:8080 grafana/k6 run - < loadtest/baseline.js

옵션 예시: RATE=200, DURATION=2m, API=list. k6를 로컬 설치했다면 k6 run -e RATE=200 -e DURATION=2m -e API=list loadtest/baseline.js로 실행할 수 있습니다. Linux에서 Docker 컨테이너가 호스트 앱에 접근하지 못하면 host.docker.internal host-gateway 매핑을 추가합니다.

## 실험 순서
- [커리큘럼](docs/curriculum.md)
- [실험 기록 템플릿](experiments/_template.md)
- [Week 0 베이스라인](experiments/00-baseline/README.md)

## 실험 로그
| 주차 | 주제 | 기록 |
|---|---|---|
| 0 | 기반 환경 및 베이스라인 | [00-baseline](experiments/00-baseline/README.md) |

## 실험 원칙
- 비교 실험에서는 데이터 건수, 하드웨어 제한, JVM 옵션, 부하 조건을 동일하게 유지합니다.
- 워밍업 후 최소 3회 측정하고 p50/p95/p99, TPS, 오류율을 함께 기록합니다.
- 목표 TPS와 실제 처리량을 구분합니다. 부하 생성기가 요청을 충분히 발생시키지 못한 경우도 기록합니다.
- 실험 결과를 미리 정해두지 않고 관찰값과 원인을 근거로 결론을 작성합니다.
