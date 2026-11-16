# Backend Load Test — 2026-11-16

## 목적

Spring Boot + MSSQL로 구성한 실습 API에 k6 부하를 주고, 응답시간·처리량·오류율과 HikariCP 커넥션 사용량을 관찰한다.

## 테스트 환경

- API: `GET /products?category=1&page=0&size=20`
- 애플리케이션: Spring Boot 3.5.0 / Java 17
- DB: Microsoft SQL Server 2022 (Docker)
- Connection pool: HikariCP (`lab-pool`, maximum pool size 10)
- 부하 도구: k6 2.4.0 (Docker image `grafana/k6`)
- 테스트 시간: 각 단계 30초
- k6 실행 위치: `backend/load-test`

## 테스트 스크립트

`products.js`는 현재 10 VU 설정이다. 단계별 테스트 시 `vus` 값을 1, 5, 10으로 변경해 실행했다.

```javascript
import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 10,
  duration: '30s',
};

export default function () {
  const res = http.get(
    'http://host.docker.internal:8080/products?category=1&page=0&size=20'
  );

  check(res, {
    'status is 200': (r) => r.status === 200,
  });
}
```

PowerShell에서 `backend/load-test` 디렉터리로 이동한 뒤 실행:

```powershell
docker run --rm -i -v "${PWD}:/scripts" grafana/k6 run /scripts/products.js
```

## API 사전 확인

`category=1`, `page=0`, `size=20` 조회 결과:

- 현재 페이지 결과: 20건
- 전체 페이지 수: 52
- 해당 카테고리 전체 건수: 1,034건

## k6 결과

| 지표 | 1 VU | 5 VU | 10 VU |
|---|---:|---:|---:|
| 총 요청 수 | 2,029 | 6,352 | 6,085 |
| 처리량 (RPS) | 67.63 | 211.55 | 201.84 |
| 평균 응답시간 | 13.51 ms | 21.94 ms | 46.65 ms |
| 중앙값 | 12.99 ms | 19.43 ms | 31.46 ms |
| p90 | 16.40 ms | 33.80 ms | 81.26 ms |
| p95 | 17.41 ms | 38.26 ms | 85.33 ms |
| 최대 응답시간 | 25.46 ms | 77.35 ms | 122.77 ms |
| HTTP 실패율 | 0% | 0% | 0% |
| checks 성공률 | 100% | 100% | 100% |

## HikariCP 관찰 결과

Grafana에서 다음 Prometheus 메트릭을 함께 관찰했다.

- `hikaricp_connections_active`
- `hikaricp_connections_idle`
- `hikaricp_connections_pending`

관찰된 값(그래프 샘플 기준):

| 부하 | Active | Idle | Pending |
|---|---:|---:|---:|
| 유휴 상태 | 0 | 10 | 0 |
| 5 VU | 약 5까지 | 약 5까지 감소 | 0 |
| 10 VU | 약 9까지 | 약 1까지 감소 | 0 |

※ Grafana 그래프는 scrape 간격에 따른 표본이다. 순간 최대값을 완전히 포착했다고 단정하지 않는다.

## 해석

1. 1 VU에서 5 VU로 늘렸을 때 처리량은 약 3.13배 증가했다.
2. 5 VU에서 10 VU로 늘렸을 때 처리량은 211.55 RPS에서 201.84 RPS로 소폭 감소했다.
3. 평균 응답시간은 21.94 ms에서 46.65 ms로 증가했고, p95는 38.26 ms에서 85.33 ms로 증가했다.
4. 세 단계 모두 HTTP 실패율은 0%였다.
5. 5 VU와 10 VU 관찰 구간에서 HikariCP pending은 그래프상 0이었다. 따라서 해당 표본에서는 커넥션 대기가 관찰되지 않았다.
6. 10 VU에서 응답시간이 증가하고 처리량이 정체/감소했지만, 이 결과만으로 병목이 DB 인덱스, CPU, SQL 실행계획 중 무엇인지 확정할 수 없다.

## 다음 실험

- 동일 테스트 중 `process_cpu_usage`, `system_cpu_usage` 관찰
- MSSQL 실제 실행 SQL 및 실행계획 확인
- `category_id` 인덱스 추가 전후 비교
- 변경 전후 동일한 VU, duration, 데이터셋으로 재측정
- 가능하면 테스트를 여러 번 반복해 결과 편차 확인

## 결과 이미지

- `results/01-hikari-idle-baseline.png`: 유휴 상태의 HikariCP 풀 상태 및 5 VU 테스트 관찰 그래프
- `results/02-hikari-10vu.png`: 10 VU 테스트 중 HikariCP 커넥션 변화
