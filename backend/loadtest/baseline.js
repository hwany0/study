import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const MAX_ID = 100000;
const RATE = Number(__ENV.RATE || 100);      // 초당 요청 수(목표 TPS)
const DURATION = __ENV.DURATION || '1m';
const API = __ENV.API || 'single';           // single | list

export const options = {
  scenarios: {
    baseline: {
      executor: 'constant-arrival-rate',     // VU 수가 아닌 "초당 요청 수"를 고정
      rate: RATE,
      timeUnit: '1s',
      duration: DURATION,
      preAllocatedVUs: 50,
      maxVUs: 500,
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],
  },
};

export default function () {
  let res;
  if (API === 'list') {
    const category = Math.floor(Math.random() * 100) + 1;
    res = http.get(`${BASE_URL}/products?category=${category}&page=0&size=20`);
  } else {
    const id = Math.floor(Math.random() * MAX_ID) + 1;
    res = http.get(`${BASE_URL}/products/${id}`);
  }
  check(res, { 'status 200': (r) => r.status === 200 });
}
