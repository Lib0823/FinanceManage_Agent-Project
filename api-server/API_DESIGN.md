# 인증 API 설계서

## 1. 로그인 API

### POST /api/auth/login
사용자 로그인 (JWT 토큰 발급)

**Request Body:**
```json
{
  "username": "testuser",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "tokenType": "Bearer",
    "expiresIn": 3600000,
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "name": "홍길동"
    }
  }
}
```

**Error Responses:**
- 401: Invalid credentials
- 404: User not found

---

## 2. 회원가입 API

### POST /api/auth/register
신규 회원가입 (기본정보 + 금융정보)

**Request Body:**
```json
{
  "username": "newuser",
  "password": "password123",
  "passwordConfirm": "password123",
  "email": "newuser@example.com",
  "name": "홍길동",
  "phone": "01012345678",
  "birthDate": "1990-01-01",
  "kisAccount": {
    "accountNumber": "50112345",
    "appKey": "PSxxxxx",
    "appSecret": "xxxxxx"
  }
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "userId": 1,
    "username": "newuser",
    "email": "newuser@example.com"
  }
}
```

**Error Responses:**
- 400: Validation error (password mismatch, invalid data)
- 409: Username or email already exists

---

## 3. 비밀번호 재설정 API

### POST /api/auth/reset-password
비밀번호 재설정 (본인 인증 후)

**Request Body:**
```json
{
  "username": "testuser",
  "phone": "01012345678",
  "newPassword": "newpassword123",
  "passwordConfirm": "newpassword123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Password reset successful",
  "data": null
}
```

**Error Responses:**
- 400: Password mismatch or validation error
- 404: User not found or phone number mismatch

---

## 4. 유틸리티 API

### GET /api/auth/check-username?username={username}
아이디 중복 확인

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Username is available",
  "data": {
    "available": true
  }
}
```

### GET /api/auth/check-email?email={email}
이메일 중복 확인

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Email is available",
  "data": {
    "available": true
  }
}
```

### POST /api/auth/refresh
Access Token 갱신

**Request Body:**
```json
{
  "refreshToken": "eyJhbGc..."
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Token refreshed",
  "data": {
    "accessToken": "eyJhbGc...",
    "tokenType": "Bearer",
    "expiresIn": 3600000
  }
}
```

---

## 데이터베이스 테이블 매핑

### 회원가입 시 생성되는 데이터:
1. **users** 테이블: username, email, password (BCrypt), name, phone, birth_date
2. **user_kis_accounts** 테이블: user_id, account_number, app_key, app_secret
3. **user_trade_config** 테이블: user_id, order_amount (default), max_holdings (default), is_active (false)

### 로그인 시 생성되는 데이터:
1. **refresh_tokens** 테이블: user_id, token, expires_at

---

## Validation 규칙

### 회원가입:
- username: 4-20자, 영문+숫자
- password: 8자 이상, 영문+숫자+특수문자
- email: 이메일 형식
- phone: 010으로 시작하는 11자리 숫자
- birthDate: YYYY-MM-DD 형식

### 비밀번호 재설정:
- newPassword: 기존 비밀번호와 동일 규칙
- passwordConfirm: newPassword와 일치

---

## 보안 고려사항

1. 비밀번호는 BCrypt로 암호화 저장
2. JWT Access Token: 1시간 유효
3. JWT Refresh Token: 24시간 유효
4. KIS API Key/Secret은 암호화 저장 권장
5. 비밀번호 재설정 시 본인 인증 필수 (현재 phone 번호 일치 확인)

---

## MVP 범위 제외 사항

- SMS 인증번호 발송 (실제 SMS 발송 기능은 제외, 프론트엔드 UI만 구현)
- 이메일 인증
- 소셜 로그인
- 2FA
