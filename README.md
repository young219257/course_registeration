# 🛒수강신청 api

### 1. 프로젝트 실행 방법

### 2. 프로젝트 설계 배경

#### API

| Method | Endpoint               | Description                     | Request              | Response Example                    |
|--------|------------------------|---------------------------------|----------------------|-------------------------------------|
| GET    | /api/likes             | 찜한 상품 목록 조회 (페이징) | 없음                  | ```json<br>[{"id":1,"productId":101,"userId":1001},{"id":2,"productId":102,"userId":1002}]``` |
| POST   | /api/likes             | 찜하기 토글                   | ```json<br>{"productId": 101, "userId": 1001}``` | ```json<br>{"message": "찜 상태가 변경되었습니다."}``` |
| GET    | /api/likes/redis       | 찜하기 수 top5 상품 조회 (Redis) | 없음                  | ```json<br>[{"productId": 101,"likes": 50},{"productId": 102,"likes": 45}]``` |
| POST   | /api/likes/redis       | 찜하기 수 top5 상품 등록 (Redis) | ```json<br>{"productId": 101,"likes": 50}``` | ```json<br>{"message": "상품이 등록되었습니다."}``` |
| GET    | /api/likes/top5        | 찜하기 수 top5 상품 조회     | 없음                  | ```json<br>[{"productId": 101,"likes": 50},{"productId": 102,"likes": 45}]``` |

#### 주요 기능
#### 데이터베이스
● Erd : https://www.erdcloud.com/d/impWPBYKqvDYHnMJx
