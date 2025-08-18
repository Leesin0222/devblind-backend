# DevBlind 백엔드 API 스펙 문서

> **모바일 앱 개발자를 위한 백엔드 API 명세서**  
> 모든 API 엔드포인트, 요청/응답 형식, 에러 코드 정의

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-blue.svg)](https://swagger.io/specification/)
[![JWT](https://img.shields.io/badge/JWT-Authentication-orange.svg)](https://jwt.io/)

## 목차

- [API 개요](#api-개요)
- [인증 및 보안](#인증-및-보안)
- [공통 응답 형식](#공통-응답-형식)
- [에러 코드](#에러-코드)
- [사용자 인증 API](#사용자-인증-api)
- [사용자 관리 API](#사용자-관리-api)
- [매칭 시스템 API](#매칭-시스템-api)
- [채팅 시스템 API](#채팅-시스템-api)
- [결제 시스템 API](#결제-시스템-api)
- [파일 업로드 API](#파일-업로드-api)
- [WebSocket API](#websocket-api)
- [Webhook API](#webhook-api)
- [푸시 알림 API](#푸시-알림-api)
- [테스트 및 개발](#테스트-및-개발)

## API 개요

### 기본 정보
- **Base URL**: `http://localhost:8080` (개발), `https://api.devblind.com` (운영)
- **API 버전**: v1
- **인증 방식**: JWT Bearer Token
- **데이터 형식**: JSON
- **문자 인코딩**: UTF-8

### API 문서
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## 인증 및 보안

### JWT 토큰 구조
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

### 토큰 사용법
```http
Authorization: Bearer {accessToken}
```

### 토큰 갱신
```http
POST /auth/sms/token/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**응답:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## 공통 응답 형식

### 성공 응답
백엔드는 표준화된 응답 래퍼 없이 데이터를 직접 반환합니다:
```json
// 단일 객체 응답
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "nickname": "개발자"
}

// 리스트 응답
[
  {
    "id": 1,
    "nickname": "프론트엔드개발자",
    "age": 28,
    "location": "서울시 강남구"
  }
]
```

### 에러 응답
```json
{
  "code": "UNAUTHORIZED",
  "message": "인증이 필요합니다.",
  "timestamp": 1705123456789
}
```

### 페이지네이션 응답 (Spring Data Page)
```json
{
  "content": [
    // 데이터 목록
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 100,
  "totalPages": 5,
  "last": false,
  "first": true,
  "sort": {
    "sorted": true,
    "unsorted": false
  },
  "numberOfElements": 20,
  "size": 20,
  "number": 0
}
```

## 에러 코드

### 인증 관련 에러
- **UNAUTHORIZED**: 인증이 필요합니다.
- **INVALID_TOKEN**: 유효하지 않은 토큰입니다.
- **INVALID_REFRESH_TOKEN**: 유효하지 않은 리프레시 토큰입니다.
- **INVALID_CODE**: 유효하지 않은 인증 코드입니다.

### 사용자 관련 에러
- **USER_NOT_FOUND**: 사용자를 찾을 수 없습니다.
- **VALIDATION_ERROR**: 입력값이 올바르지 않습니다.

### 파일 업로드 관련 에러
- **FILE_UPLOAD_FAILED**: 파일 업로드에 실패했습니다.
- **INVALID_FILE_TYPE**: 지원하지 않는 파일 형식입니다. (JPG, JPEG, PNG, GIF, WEBP만 지원)
- **FILE_SIZE_EXCEEDED**: 파일 크기가 제한을 초과했습니다. (최대 5MB)

### 결제 관련 에러
- **INSUFFICIENT_COINS**: 코인이 부족합니다.
- **INSUFFICIENT_BALANCE**: 잔액이 부족합니다.
- **ADDITIONAL_RECOMMENDATION_LIMIT_EXCEEDED**: 오늘 추가 추천 사용 횟수를 초과했습니다.
- **PAYMENT_NOT_FOUND**: 결제 정보를 찾을 수 없습니다.
- **PRODUCT_NOT_FOUND**: 상품을 찾을 수 없습니다.
- **INVALID_REFUND_REQUEST**: 잘못된 환불 요청입니다.
- **REFUND_FAILED**: 환불 처리에 실패했습니다.

### 채팅 관련 에러
- **CHAT_ROOM_NOT_FOUND**: 채팅방을 찾을 수 없습니다.
- **CHAT_MESSAGE_NOT_FOUND**: 채팅 메시지를 찾을 수 없습니다.

### 매칭 관련 에러
- **MATCHING_NOT_FOUND**: 매칭을 찾을 수 없습니다.
- **MATCHING_PROFILE_NOT_FOUND**: 매칭 프로필을 찾을 수 없습니다.
- **DAILY_RECOMMENDATION_LIMIT_EXCEEDED**: 오늘 추천 한도를 초과했습니다.

### 권한 관련 에러
- **FORBIDDEN**: 접근 권한이 없습니다.

### SMS 관련 에러
- **SMS_SEND_FAILED**: SMS 발송에 실패했습니다.

### 서버 오류
- **SERVER_ERROR**: 서버 오류가 발생했습니다.

### HTTP 상태 코드
- **200**: 성공
- **201**: 생성됨
- **400**: 잘못된 요청
- **401**: 인증 필요
- **403**: 권한 없음
- **404**: 리소스 없음
- **500**: 서버 내부 오류

## 사용자 인증 API

### 1. SMS 인증 코드 발송
```http
POST /auth/sms/send
Content-Type: application/json

{
  "phoneNumber": "01012345678"  // 01[0-9]-?[0-9]{4}-?[0-9]{4} 형식 (하이픈 선택사항)
}
```

**응답:**
```http
HTTP/1.1 200 OK
```

### 2. SMS 인증 코드 검증
```http
POST /auth/sms/verify
Content-Type: application/json

{
  "phoneNumber": "01012345678",  // 01[0-9]-?[0-9]{4}-?[0-9]{4} 형식 (하이픈 선택사항)
  "code": "123456"  // 6자리 숫자
}
```

**응답:**
```json
{
  "isRegistered": true,
  "authResponse": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "nickname": "개발자"
  },
  "signupToken": null
}
```

> **참고**: DevBlind는 별도의 로그인 API가 없습니다. SMS 인증 코드 검증(`/auth/sms/verify`)에서 기존 사용자인 경우 자동으로 로그인되고, 신규 사용자인 경우 회원가입 토큰이 발급됩니다.

### 3. 회원가입
```http
POST /auth/sms/signup
Content-Type: application/json

{
  "phoneNumber": "01012345678",  // 01[0-9]-?[0-9]{4}-?[0-9]{4} 형식 (하이픈 선택사항)
  "nickname": "개발자",
  "birth": "1990-01-01",
  "gender": "MALE",  // MALE, FEMALE 중 선택
  "age": 35,
  "location": "서울시 강남구",
  "bio": "안녕하세요! 개발자입니다.",
  "profileImageUrl": "https://example.com/profile.jpg",
  "techStackIds": [1, 3],
  "signupToken": "signup_token_here"
}
```

**응답:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "nickname": "개발자"
}
```

### 4. 로그아웃
```http
POST /auth/sms/logout
Authorization: Bearer {accessToken}
```

**응답:**
```http
HTTP/1.1 200 OK
```

## 사용자 관리 API

### 1. 내 프로필 조회
```http
GET /users/me
Authorization: Bearer {accessToken}
```

**응답:**
```json
{
  "userId": 1,
  "nickname": "개발자",
  "bio": "안녕하세요! 개발자입니다.",
  "gender": "MALE",
  "age": 35,
  "location": "서울시 강남구",
  "profileImageUrl": "https://example.com/profile.jpg",
  "techStacks": [
    {
      "id": 1,
      "name": "Java"
    },
    {
      "id": 2,
      "name": "Spring Boot"
    }
  ],
  "balance": 1000
}
```

### 2. 프로필 수정
```http
PUT /users/me
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "nickname": "수정된닉네임",
  "bio": "수정된 자기소개",
  "gender": "MALE",  // MALE, FEMALE 중 선택
  "age": 35,
  "location": "서울시 서초구",
  "profileImageUrl": "https://example.com/profile.jpg",
  "birth": "1990-01-01",
  "techStackIds": [1, 3]
}
```

**응답:**
```http
HTTP/1.1 204 No Content
```

### 3. 닉네임 중복 확인
```http
GET /users/check-nickname?nickname=테스트닉네임
```

**응답:**
```json
{
  "isDuplicate": false
}
```

### 4. 기술 스택 목록 조회
```http
GET /tech-stacks
```

**응답:**
```json
[
  {
    "id": 1,
    "name": "Java"
  },
  {
    "id": 2,
    "name": "Spring Boot"
  },
  {
    "id": 3,
    "name": "React"
  }
]
```

### 5. 사용자 잔액 조회
```http
GET /user-balance
Authorization: Bearer {accessToken}
```

**응답:**
```json
1000
```

## 매칭 시스템 API

### 1. 추천 매칭 목록 조회
```http
GET /matching/recommendations?page=0&size=20
Authorization: Bearer {accessToken}
```

**응답:**
```json
[
  {
    "userId": 2,
    "nickname": "프론트엔드개발자",
    "bio": "React 전문가입니다!",
    "gender": "FEMALE",
    "age": 28,
    "location": "서울시 강남구",
    "profileImageUrl": "https://example.com/profile2.jpg",
    "techStacks": ["React", "TypeScript"],
    "score": 85.5,
    "techScore": 30.0,
    "locationScore": 25.0,
    "ageScore": 20.0,
    "preferenceScore": 10.5
  }
]
```

### 2. 추가 추천 목록 조회 (유료)
```http
GET /matching/additional-recommendations?page=0&size=20
Authorization: Bearer {accessToken}
```

**응답:** (추천 매칭과 동일한 형식)

### 3. 좋아요/싫어요
```http
POST /matching/like
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "targetUserId": 2,
  "likeType": "LIKE"  // LIKE, DISLIKE, PULL_REQUEST 중 선택
}
```

**응답:**
```json
{
  "isMatch": true,
  "message": "좋아요가 전송되었습니다.",
  "matchingId": 1
}
```

### 4. 받은 좋아요 목록
```http
GET /matching/received-likes?page=0&size=20
Authorization: Bearer {accessToken}
```

**응답:**
```json
[
  {
    "fromUserId": 3,
    "fromUserNickname": "백엔드개발자",
    "fromUserProfileImageUrl": "https://example.com/profile3.jpg",
    "likeType": "LIKE",
    "createdAt": "2025-01-13T15:30:00Z"
  }
]
```

### 5. 좋아요에 응답
```http
POST /matching/respond-like/{senderUserId}
Authorization: Bearer {accessToken}
```

**응답:**
```json
{
  "isMatch": true,
  "message": "매칭이 성공했습니다!",
  "matchingId": 1
}
```

### 6. 매칭 점수 계산
```http
GET /matching/score/{targetUserId}
Authorization: Bearer {accessToken}
```

**응답:**
```json
{
  "targetUserId": 2,
  "targetUserNickname": "프론트엔드개발자",
  "totalScore": 85.5,
  "techScore": 30.0,
  "locationScore": 25.0,
  "ageScore": 20.0,
  "preferenceScore": 10.5,
  "explanation": "매칭 점수가 계산되었습니다."
}
```

### 7. 내 매칭 목록
```http
GET /matching/my-matchings?page=0&size=20
Authorization: Bearer {accessToken}
```

**응답:**
```json
[
  {
    "id": 1,
    "matchedUser": {
      "id": 2,
      "nickname": "프론트엔드개발자",
      "age": 28,
      "location": "서울시 강남구",
      "profileImageUrl": "https://example.com/profile2.jpg"
    },
    "status": "MATCHED",
    "matchedAt": "2025-01-13T15:30:00Z",
    "chatRoomId": 1
  }
]
```

### 8. 채팅 시작 (코인 차감)
```http
POST /matching/start-chat/{matchingId}
Authorization: Bearer {accessToken}
```

**응답:**
```http
HTTP/1.1 200 OK
```

### 9. 매칭 프로필 생성/수정
```http
POST /matching/profile
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "nickname": "개발자",
  "bio": "안녕하세요! 개발자입니다.",
  "gender": "MALE",  // MALE, FEMALE 중 선택
  "age": 35,
  "location": "서울시 강남구",
  "techStacks": ["Java", "Spring Boot"],
  "preferredGender": "FEMALE",  // MALE, FEMALE 중 선택
  "minAge": 25,
  "maxAge": 40,
  "preferredLocation": "서울시",
  "introduction": "객체지향 프로그래밍을 좋아합니다",
  "idealType": "함께 성장할 수 있는 사람",
  "hobby": "코딩, 독서",
  "job": "백엔드 개발자"
}
```

**응답:**
```http
HTTP/1.1 200 OK
```

### 10. 오늘 추가 추천 사용 횟수 조회
```http
GET /matching/additional-recommendations/usage
Authorization: Bearer {accessToken}
```

**응답:**
```json
{
  "usedCount": 2,
  "maxCount": 5,
  "remainingCount": 3
}
```

### 11. 매칭 프로필 활성화/비활성화
```http
PUT /matching/active
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "active": true  // true: 활성화, false: 비활성화
}
```

**응답:**
```http
HTTP/1.1 200 OK
```

## 채팅 시스템 API

### 1. 채팅방 목록 조회
```http
GET /chat/rooms?page=0&size=20
Authorization: Bearer {accessToken}
```

**응답:**
```json
[
  {
    "id": 1,
    "matchingId": 1,
    "matchedUser": {
      "id": 2,
      "nickname": "프론트엔드개발자",
      "profileImageUrl": "https://example.com/profile2.jpg"
    },
    "lastMessage": {
      "content": "안녕하세요!",
      "sentAt": "2025-01-13T15:30:00Z",
      "isFromMe": false
    },
    "unreadCount": 2,
    "createdAt": "2025-01-13T15:30:00Z"
  }
]
```

### 2. 메시지 목록 조회
```http
GET /chat/rooms/{matchingId}/messages?page=0&size=50
Authorization: Bearer {accessToken}
```

**응답:**
```json
{
  "content": [
    {
      "id": 1,
      "content": "안녕하세요!",
      "senderId": 2,
      "isFromMe": false,
      "sentAt": "2025-01-13T15:30:00Z",
      "readAt": "2025-01-13T15:31:00Z"
    },
    {
      "id": 2,
      "content": "안녕하세요! 반갑습니다.",
      "senderId": 1,
      "isFromMe": true,
      "sentAt": "2025-01-13T15:32:00Z",
      "readAt": null
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 50,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 2,
  "totalPages": 1,
  "last": true,
  "first": true,
  "sort": {
    "sorted": true,
    "unsorted": false
  },
  "numberOfElements": 2,
  "size": 50,
  "number": 0
}
```

### 3. 메시지 전송
```http
POST /chat/rooms/{matchingId}/messages
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "content": "안녕하세요! 반갑습니다.",
  "messageType": "TEXT"  // TEXT, IMAGE, FILE, CODE 중 선택 (기본값: TEXT)
}
```

**응답:**
```json
{
  "id": 2,
  "content": "안녕하세요! 반갑습니다.",
  "senderId": 1,
  "isFromMe": true,
  "sentAt": "2025-01-13T15:32:00Z",
  "readAt": null
}
```

### 4. 읽지 않은 메시지 수 조회
```http
GET /chat/rooms/{matchingId}/unread-count
Authorization: Bearer {accessToken}
```

**응답:**
```json
2
```

## 결제 시스템 API

### 1. 결제 상품 목록 조회
```http
GET /payment-products
```

**응답:**
```json
[
  {
    "id": 1,
    "name": "추천 10개",
    "price": 5000,
    "coinAmount": 10,
    "description": "추천 매칭 10개 추가"
  },
  {
    "id": 2,
    "name": "추천 30개",
    "price": 12000,
    "coinAmount": 30,
    "description": "추천 매칭 30개 추가"
  }
]
```

### 2. 결제 요청
```http
POST /payments
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "productId": 1,
  "paymentMethod": "CARD",  // CARD, TRANSFER, VIRTUAL_ACCOUNT 중 선택
  "successUrl": "https://example.com/success",
  "failUrl": "https://example.com/fail"
}
```

**응답:**
```json
{
  "paymentId": "pay_123456789",
  "orderId": "order_123456789",
  "amount": 5000,
  "paymentUrl": "https://pay.toss.im/...",
  "status": "PENDING"
}
```

### 3. 결제 내역 조회
```http
GET /payment-histories?page=0&size=20
Authorization: Bearer {accessToken}
```

**응답:**
```json
[
  {
    "id": 1,
    "paymentId": "pay_123456789",
    "productName": "추천 10개",
    "amount": 5000,
    "status": "COMPLETED",
    "paidAt": "2025-01-13T15:35:00Z",
    "refundStatus": "NONE"
  }
]
```

### 4. 환불 내역 조회
```http
GET /refund-histories
Authorization: Bearer {accessToken}
```

**응답:**
```json
[
  {
    "id": 1,
    "paymentId": "pay_123456789",
    "amount": 5000,
    "reason": "상품 불만족",
    "status": "COMPLETED",
    "refundedAt": "2025-01-13T16:00:00Z",
    "productName": "추천 10개"
  }
]
```

### 3. 환불 요청
```http
POST /refunds
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "orderId": "order_123456789",
  "amount": 5000,
  "reason": "상품 불만족"
}
```

## 파일 업로드 API

### 1. 프로필 이미지 업로드
```http
POST /api/files/profile-image
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data

file: [이미지 파일]
```

**응답:**
```json
{
  "imageUrl": "https://devblind-profile-images.s3.ap-northeast-2.amazonaws.com/profile-images/123/abc123.jpg",
  "fileName": "profile.jpg",
  "fileSize": 1024000
}
```

## WebSocket API

### 연결 설정
```javascript
// STOMP WebSocket 연결
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({
  'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...'
}, (frame) => {
  console.log('Connected: ' + frame);
}, (error) => {
  console.log('Error: ' + error);
});
```

### 메시지 구독
```javascript
// 특정 매칭의 채팅방 구독
stompClient.subscribe('/topic/chat/{matchingId}', (message) => {
  const chatMessage = JSON.parse(message.body);
  console.log('새 메시지:', chatMessage);
});

// 개인 메시지 구독
stompClient.subscribe('/user/queue/messages', (message) => {
  const notification = JSON.parse(message.body);
  console.log('개인 알림:', notification);
});
```

### 메시지 전송
```javascript
// 채팅 메시지 전송
stompClient.send('/app/chat/{matchingId}', {}, JSON.stringify({
  content: '안녕하세요!',
  messageType: 'TEXT'  // TEXT, IMAGE, FILE, CODE 중 선택
}));

// 채팅방 입장
stompClient.send('/app/chat/{matchingId}/join', {}, JSON.stringify({
  content: '입장했습니다.',
  messageType: 'SYSTEM'  // TEXT, IMAGE, FILE, CODE 중 선택
}));
```

### 연결 해제
```javascript
// WebSocket 연결 해제
stompClient.disconnect(() => {
  console.log('Disconnected');
});
```

## Webhook API

### 1. 토스 결제 웹훅
```http
POST /webhooks/toss
Content-Type: application/json

{
  "paymentKey": "5zJ4xY7m0kZny6Ql",
  "orderId": "order_123456789",
  "status": "DONE",
  "totalAmount": 5000,
  "transactionKey": "txn_123456789"
}
```

**응답:**
```http
HTTP/1.1 200 OK
```

### 2. 토스 환불 웹훅
```http
POST /webhooks/toss-refund
Content-Type: application/json

{
  "orderId": "order_123456789",
  "refundId": "refund_123456789",
  "amount": 5000,
  "status": "CANCELED",
  "reason": "고객 요청"
}
```

**응답:**
```http
HTTP/1.1 200 OK
```

## 푸시 알림 API

### 1. 디바이스 토큰 등록
```http
POST /users/device-token
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "deviceToken": "fcm_token_here"
}
```

**응답:**
```http
HTTP/1.1 200 OK
```

### 2. 디바이스 토큰 삭제
```http
DELETE /users/device-token
Authorization: Bearer {accessToken}
```

**응답:**
```http
HTTP/1.1 200 OK
```

### 푸시 알림 유형
- **매칭 성공**: 새로운 매칭이 성공했을 때
- **새 메시지**: 채팅방에 새 메시지가 도착했을 때
- **좋아요**: 누군가 나를 좋아했을 때
- **시스템**: 공지사항, 업데이트 등

## 모바일 앱 연동 가이드

### 모바일 개발자를 위한 핵심 정보

#### 1. API 응답 구조 이해
모든 API 응답은 다음과 같은 구조를 따릅니다:
```json
// 성공 시: 데이터가 직접 반환됨
{
  "id": 1,
  "nickname": "개발자",
  "age": 25
}

// 실패 시: 에러 응답이 반환됨
{
  "code": "UNAUTHORIZED",
  "message": "인증이 필요합니다.",
  "timestamp": 1705123456789
}
```

#### 2. 페이지네이션 처리
목록 조회 API는 Spring Data `Page` 구조를 사용합니다:
```json
{
  "content": [
    // 데이터 배열
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,      // 0부터 시작
    "pageSize": 20,       // 페이지당 항목 수
    "offset": 0,          // 오프셋
    "paged": true,        // 페이지네이션 사용 여부
    "unpaged": false      // 페이지네이션 미사용 여부
  },
  "totalElements": 100,   // 전체 항목 수
  "totalPages": 5,        // 전체 페이지 수
  "last": false,          // 마지막 페이지 여부
  "first": true,          // 첫 페이지 여부
  "sort": {
    "sorted": true,
    "unsorted": false
  },
  "numberOfElements": 20, // 현재 페이지 항목 수
  "size": 20,             // 페이지 크기
  "number": 0             // 현재 페이지 번호
}
```

#### 3. 에러 처리 가이드
모바일 앱에서 백엔드 에러를 처리할 때:
```typescript
// 1. HTTP 상태 코드 확인
if (response.status === 401) {
  // 토큰 만료 → 로그인 화면으로 이동
}

// 2. 백엔드 에러 코드 확인
if (response.data.code === 'UNAUTHORIZED') {
  // 인증 필요 → 로그인 화면으로 이동
}

// 3. 사용자 친화적 메시지 표시
const errorMessage = getErrorMessage(response.data.code);
alert(errorMessage);
```

#### 4. JWT 토큰 관리
```typescript
// 1. 토큰 저장
await AsyncStorage.setItem('accessToken', response.data.accessToken);
await AsyncStorage.setItem('refreshToken', response.data.refreshToken);

// 2. API 요청 시 헤더에 포함
headers: {
  'Authorization': `Bearer ${accessToken}`
}

// 3. 토큰 만료 시 자동 갱신
if (response.status === 401) {
  const newToken = await refreshToken(refreshToken);
  // 원래 요청 재시도
}
```

#### 5. WebSocket 연결 관리
```typescript
// 1. 연결 시 토큰 인증
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({
  'Authorization': `Bearer ${accessToken}`
}, (frame) => {
  console.log('Connected:', frame);
}, (error) => {
  console.log('Error:', error);
});

// 2. 메시지 구독
stompClient.subscribe('/topic/chat/{matchingId}', (message) => {
  const chatMessage = JSON.parse(message.body);
  // 새 메시지 처리
});

// 3. 연결 상태 관리
stompClient.onWebSocketClose = () => {
  // 재연결 로직
};
```

### 모바일 앱 테스트 체크리스트

#### 인증 시스템
- [ ] SMS 인증 코드 발송/검증
- [ ] 회원가입 (필수 필드 검증)
- [ ] 로그인/로그아웃
- [ ] JWT 토큰 자동 갱신
- [ ] 토큰 만료 시 자동 로그아웃

#### 매칭 시스템
- [ ] 추천 매칭 목록 조회 (페이지네이션)
- [ ] 좋아요/싫어요 처리
- [ ] 받은 좋아요 목록 조회
- [ ] 좋아요에 응답 (수락/거절)
- [ ] 매칭 성공 시 채팅방 생성

#### 채팅 시스템
- [ ] 채팅방 목록 조회
- [ ] 메시지 전송/수신
- [ ] WebSocket 실시간 연결
- [ ] 읽지 않은 메시지 표시
- [ ] 채팅방 입장/퇴장

#### 파일 업로드
- [ ] 프로필 이미지 업로드
- [ ] 이미지 압축 및 리사이징
- [ ] 업로드 진행률 표시
- [ ] 업로드 실패 시 재시도

## 테스트 및 개발

### 테스트 환경
- **Base URL**: `http://localhost:8080`
- **데이터베이스**: PostgreSQL (devblind_local_db)
- **Redis**: localhost:6379

### 테스트 데이터
```sql
-- 사용자 테스트 데이터
INSERT INTO users (nickname, phone_number, age, location, bio) 
VALUES ('테스트사용자', '01012345678', 25, '서울시 강남구', '테스트용 계정입니다.');

-- 기술 스택 테스트 데이터
INSERT INTO tech_stacks (name, category, description) 
VALUES ('Java', 'BACKEND', '객체지향 프로그래밍 언어');
```

### API 테스트 도구
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **Postman**: API 테스트 및 문서화
- **curl**: 커맨드라인 테스트

### 테스트 시나리오
1. **회원가입 → 로그인 → 프로필 수정**
2. **매칭 추천 조회 → 좋아요 → 매칭 성공**
3. **채팅방 생성 → 메시지 전송 → 실시간 통신**
4. **결제 요청 → 결제 완료 → 추천 추가**

## 개발자

**Backend Developer**: 이용진

## 프로젝트 소개

DevBlind 백엔드 API는 개발자들을 위한 소개팅 플랫폼의 핵심 서버입니다.  
모바일 앱과의 원활한 연동을 위해 RESTful API와 WebSocket을 제공합니다.

### 주요 특징
- **RESTful API**: 표준 HTTP 메서드 사용
- **실시간 통신**: WebSocket 기반 채팅
- **JWT 인증**: 안전한 사용자 인증
- **파일 업로드**: AWS S3 연동
- **푸시 알림**: FCM 연동

<div align="center">

**DevBlind Backend API - 개발자를 위한 블라인드 소개팅 백엔드**

Made with ❤️ by 이용진

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-blue.svg)](https://swagger.io/specification/)
[![JWT](https://img.shields.io/badge/JWT-Authentication-orange.svg)](https://jwt.io/)

</div>
