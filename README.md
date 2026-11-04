# Backend Study

백엔드 기술을 직접 구현하고 부하·장애를 재현해 성능, 정합성, 운영 관점의 트레이드오프를 검증하는 실습 저장소입니다.

## 프로젝트 구성
- [backend](backend/README.md): Spring Boot 실험 애플리케이션과 인프라
- [커리큘럼](backend/docs/curriculum.md): 주차별 학습 로드맵
- [실험 기록](backend/experiments/): 가설·방법·결과·분석
- [k6 스크립트](backend/loadtest/): 부하 테스트
- [인프라](backend/infra/): Docker Compose, Prometheus, Grafana, Kubernetes

## 학습 원칙
1. 개념을 확인한 뒤 직접 재현한다.
2. 한 번에 하나의 변수만 바꿔 비교한다.
3. TPS뿐 아니라 p95/p99, 오류율, 자원 사용량을 함께 기록한다.
4. 성공뿐 아니라 실패한 실험과 기술적 트레이드오프도 문서화한다.

## 시작하기
실행 방법과 환경 요구사항은 [backend README](backend/README.md)를 참고하세요.
