# Coding Conventions

## Java (Backend)

### 스타일 가이드

**기본:** Google Java Style Guide

### 네이밍 규칙

```java
// 클래스: PascalCase
public class UserService { }
public class OrderController { }

// 인터페이스: PascalCase (I 접두사 사용 안 함)
public interface UserRepository { }  // ✅
public interface IUserRepository { } // ❌

// 메서드: camelCase (동사로 시작)
public User findById(Long id) { }
public void saveUser(User user) { }
public boolean isActive() { }
public List<User> getUsers() { }

// 변수: camelCase
private String userName;
private int totalCount;
private boolean isActive;

// 상수: UPPER_SNAKE_CASE
public static final int MAX_RETRY_COUNT = 3;
public static final String API_BASE_URL = "https://api.example.com";

// 패키지: lowercase (단어 구분 없음)
package com.inbeom.api.auth;
package com.inbeom.api.trading.service;
```

### 코드 포맷팅

```java
// 들여쓰기: 2칸 (스페이스)
public class UserService {
  private final UserRepository userRepository;
  
  public User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다"));
  }
}

// 중괄호: K&R 스타일 (같은 줄)
if (condition) {
  doSomething();
} else {
  doOtherThing();
}

// 한 줄에 하나의 명령문
int a = 1;  // ✅
int b = 2;  // ✅

int a = 1; int b = 2;  // ❌

// 메서드 체이닝: 줄바꿈
return userRepository.findById(id)
    .map(User::getEmail)
    .orElse(null);

// 람다: 간단하면 한 줄, 복잡하면 여러 줄
list.forEach(item -> System.out.println(item));  // ✅

list.forEach(item -> {
  validateItem(item);
  processItem(item);
  saveItem(item);
});  // ✅
```

### 패키지 구조 (도메인 기반)

```
com.inbeom.api
├── auth                    # 인증/인가 도메인
│   ├── controller
│   │   └── AuthController.java
│   ├── service
│   │   ├── AuthService.java
│   │   └── TokenService.java
│   ├── repository
│   │   └── UserRepository.java
│   ├── domain
│   │   ├── User.java
│   │   └── RefreshToken.java
│   └── dto
│       ├── request
│       │   ├── LoginRequest.java
│       │   └── RegisterRequest.java
│       └── response
│           └── AuthResponse.java
│
├── trading                 # 거래 도메인
│   ├── controller
│   ├── service
│   ├── repository
│   ├── domain
│   │   ├── Order.java
│   │   ├── Execution.java
│   │   └── OrderType.java
│   └── dto
│
├── asset                   # 자산 도메인
│   ├── controller
│   ├── service
│   ├── repository
│   ├── domain
│   └── dto
│
└── common                  # 공통
    ├── config
    │   ├── SecurityConfig.java
    │   ├── JpaConfig.java
    │   └── SwaggerConfig.java
    ├── exception
    │   ├── GlobalExceptionHandler.java
    │   ├── BusinessException.java
    │   └── ErrorCode.java
    ├── dto
    │   ├── ApiResponse.java
    │   └── PageResponse.java
    └── util
        ├── DateUtil.java
        └── StringUtil.java
```

### 클래스 구조 순서

```java
public class UserService {
  
  // 1. 상수
  private static final int MAX_LOGIN_ATTEMPTS = 5;
  
  // 2. 필드 (의존성 주입)
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  
  // 3. 생성자 (Lombok @RequiredArgsConstructor 사용 시 생략)
  public UserService(UserRepository userRepository, 
                     PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }
  
  // 4. public 메서드
  public User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다"));
  }
  
  // 5. private 메서드
  private void validatePassword(String password) {
    if (password.length() < 8) {
      throw new InvalidPasswordException("비밀번호는 8자 이상이어야 합니다");
    }
  }
}
```

### 주석

```java
/**
 * 사용자 서비스
 * 
 * 사용자 인증, 조회, 관리 기능을 제공합니다.
 */
public class UserService {
  
  /**
   * 사용자 ID로 조회
   * 
   * @param id 사용자 ID
   * @return 사용자 엔티티
   * @throws NotFoundException 사용자를 찾을 수 없는 경우
   */
  public User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다"));
  }
  
  // 복잡한 로직에만 인라인 주석
  private void processOrder(Order order) {
    // 주문 상태가 PENDING인 경우에만 처리
    if (order.getStatus() == OrderStatus.PENDING) {
      // 잔고 확인 후 주문 실행
      validateBalance(order);
      executeOrder(order);
    }
  }
}
```

### 예외 처리

```java
// 구체적인 예외 사용
try {
  // ...
} catch (IllegalArgumentException e) {  // ✅
  log.error("잘못된 인자", e);
  throw new BusinessException(ErrorCode.INVALID_ARGUMENT);
}

// 일반적인 Exception은 피하기
try {
  // ...
} catch (Exception e) {  // ❌ (최상위에서만 사용)
  log.error("예외 발생", e);
}

// 비즈니스 예외 생성
public class BusinessException extends RuntimeException {
  private final ErrorCode errorCode;
  
  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
```

### Lombok 사용

```java
// Entity
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private String email;
  private String password;
}

// Service
@Service
@RequiredArgsConstructor  // final 필드 자동 주입
@Slf4j                    // log 사용 가능
public class UserService {
  private final UserRepository userRepository;
  
  public User findById(Long id) {
    log.info("사용자 조회: {}", id);
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("사용자 없음"));
  }
}

// DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
  private Long id;
  private String email;
  private String name;
}
```

---

## JavaScript (Frontend)

### 스타일 가이드

**기본:** Vue 3 Composition API + ESLint

### 네이밍 규칙

```javascript
// 파일명: PascalCase (컴포넌트)
LoginForm.vue
UserProfile.vue
TheHeader.vue

// 파일명: camelCase (유틸, composables)
useAuth.js
apiClient.js
formatDate.js

// 변수/함수: camelCase
const userName = 'John'
const isActive = true
function fetchUserData() { }

// 상수: UPPER_SNAKE_CASE
const API_BASE_URL = 'http://localhost:8080'
const MAX_RETRY_COUNT = 3

// 컴포넌트 이름: PascalCase
export default {
  name: 'LoginForm'
}

// Props/Events: camelCase
defineProps({
  userId: Number,
  isActive: Boolean
})

defineEmits(['update:modelValue', 'submit'])
```

### 코드 포맷팅

```javascript
// 들여쓰기: 2칸 (스페이스)
// 따옴표: 작은따옴표
// 세미콜론: 사용

import { ref, computed } from 'vue'

export default {
  setup() {
    const count = ref(0)
    
    const doubleCount = computed(() => {
      return count.value * 2
    })
    
    function increment() {
      count.value++
    }
    
    return {
      count,
      doubleCount,
      increment
    }
  }
}

// 객체: 마지막 콤마 사용
const user = {
  name: 'John',
  age: 30,
  email: 'john@example.com',  // ✅
}

// 배열: 긴 경우 줄바꿈
const numbers = [1, 2, 3, 4, 5]  // ✅

const users = [
  { id: 1, name: 'John' },
  { id: 2, name: 'Jane' },
  { id: 3, name: 'Bob' },
]  // ✅
```

### 폴더 구조 (도메인 기반)

```
src/
├── features/                   # 기능별 모듈
│   ├── auth/
│   │   ├── components/
│   │   │   ├── LoginForm.vue
│   │   │   └── RegisterForm.vue
│   │   ├── composables/
│   │   │   └── useAuth.js
│   │   ├── api/
│   │   │   └── authApi.js
│   │   └── views/
│   │       ├── LoginView.vue
│   │       └── RegisterView.vue
│   │
│   ├── trading/
│   │   ├── components/
│   │   │   ├── OrderForm.vue
│   │   │   ├── OrderBook.vue
│   │   │   └── StockChart.vue
│   │   ├── composables/
│   │   │   ├── useOrders.js
│   │   │   └── useWebSocket.js
│   │   ├── api/
│   │   │   └── tradingApi.js
│   │   └── views/
│   │       ├── TradingView.vue
│   │       └── OrderHistoryView.vue
│   │
│   └── asset/
│       ├── components/
│       ├── composables/
│       ├── api/
│       └── views/
│
├── shared/                     # 공통
│   ├── components/
│   │   ├── BaseButton.vue
│   │   ├── BaseInput.vue
│   │   └── LoadingSpinner.vue
│   ├── composables/
│   │   ├── useLoading.js
│   │   └── useToast.js
│   ├── utils/
│   │   ├── format.js
│   │   └── validate.js
│   └── api/
│       └── axios.js
│
├── router/
│   └── index.js
│
├── stores/
│   └── user.js
│
├── App.vue
└── main.js
```

### Vue 컴포넌트 구조

```vue
<script setup>
// 1. imports
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 2. props & emits
const props = defineProps({
  userId: {
    type: Number,
    required: true
  },
  isActive: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update', 'delete'])

// 3. composables & stores
const router = useRouter()
const authStore = useAuthStore()

// 4. reactive state
const count = ref(0)
const userName = ref('')

// 5. computed
const displayName = computed(() => {
  return userName.value.toUpperCase()
})

// 6. methods
function handleSubmit() {
  emit('update', userName.value)
}

// 7. lifecycle hooks
onMounted(() => {
  loadUserData()
})
</script>

<template>
  <!-- 템플릿 내용 -->
  <div class="container">
    <h1>{{ displayName }}</h1>
    <button @click="handleSubmit">Submit</button>
  </div>
</template>

<style scoped>
/* 컴포넌트 스타일 */
.container {
  padding: 20px;
}
</style>
```

### Composables 작성

```javascript
// src/features/auth/composables/useAuth.js
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../api/authApi'

export function useAuth() {
  const router = useRouter()
  const user = ref(null)
  const isLoading = ref(false)
  const error = ref(null)
  
  async function login(email, password) {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await authApi.login({ email, password })
      user.value = response.data.user
      localStorage.setItem('token', response.data.accessToken)
      router.push('/')
    } catch (err) {
      error.value = err.response?.data?.error?.message || '로그인 실패'
    } finally {
      isLoading.value = false
    }
  }
  
  function logout() {
    user.value = null
    localStorage.removeItem('token')
    router.push('/login')
  }
  
  return {
    user,
    isLoading,
    error,
    login,
    logout
  }
}
```

### API 클라이언트

```javascript
// src/shared/api/axios.js
import axios from 'axios'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 요청 인터셉터
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 응답 인터셉터
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default apiClient
```

```javascript
// src/features/auth/api/authApi.js
import apiClient from '@/shared/api/axios'

export const authApi = {
  login: (data) => apiClient.post('/api/v1/auth/login', data),
  register: (data) => apiClient.post('/api/v1/auth/register', data),
  logout: () => apiClient.post('/api/v1/auth/logout'),
  getMe: () => apiClient.get('/api/v1/auth/me')
}
```

---

## Git 컨벤션

### 커밋 메시지 (Conventional Commits)

**형식:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type:**
- `feat`: 새로운 기능
- `fix`: 버그 수정
- `docs`: 문서 변경
- `style`: 코드 포맷팅 (기능 변경 없음)
- `refactor`: 리팩토링
- `test`: 테스트 추가/수정
- `chore`: 빌드/설정 변경

**Scope:**
- `auth`, `trading`, `asset`, `news`, `ai`, `common`

**Subject:**
- 명령형 현재형 (Add, Fix, Update)
- 첫 글자 소문자
- 마침표 없음
- 50자 이내

**예시:**
```bash
# 기능 추가
feat(auth): add JWT authentication

JWT 기반 인증 방식 적용
- Access Token (1시간)
- Refresh Token (7일)
- HttpOnly Cookie 저장

Closes #123

# 버그 수정
fix(trading): prevent duplicate order submission

주문 생성 시 중복 제출 방지 로직 추가

# 리팩토링
refactor(asset): simplify profit calculation logic

# 문서
docs: update API specification

# 스타일
style(trading): format code with prettier

# 테스트
test(auth): add login service unit tests

# 설정
chore: update dependencies
```

### 브랜치 전략

**현재 (초기):**
```
main  # 모든 작업을 직접 커밋
```

**향후 (팀 확장 시):**
```
main
├── develop
├── feature/user-auth
├── feature/stock-trading
├── hotfix/order-bug
└── release/v1.0.0
```

---

## 테스트

### Backend (JUnit 5)

```java
@SpringBootTest
class OrderServiceTest {
  
  @Autowired
  private OrderService orderService;
  
  @MockBean
  private OrderRepository orderRepository;
  
  @MockBean
  private StockApiClient stockApiClient;
  
  @Test
  @DisplayName("매수 주문 생성 성공")
  void createBuyOrder_Success() {
    // given
    OrderRequest request = OrderRequest.builder()
        .symbol("005930")
        .type(OrderType.BUY)
        .price(75000)
        .quantity(10)
        .build();
    
    given(stockApiClient.getCurrentPrice("005930"))
        .willReturn(75000);
    
    // when
    Order result = orderService.createOrder(request);
    
    // then
    assertThat(result).isNotNull();
    assertThat(result.getType()).isEqualTo(OrderType.BUY);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
  }
  
  @Test
  @DisplayName("잔고 부족 시 주문 실패")
  void createOrder_InsufficientBalance_ThrowsException() {
    // given
    OrderRequest request = OrderRequest.builder()
        .symbol("005930")
        .type(OrderType.BUY)
        .price(75000)
        .quantity(1000)  // 잔고 초과
        .build();
    
    // when & then
    assertThatThrownBy(() -> orderService.createOrder(request))
        .isInstanceOf(InsufficientBalanceException.class)
        .hasMessage("잔고가 부족합니다");
  }
}
```

**테스트 네이밍:**
- 클래스: `{ClassName}Test`
- 메서드: `{methodName}_{scenario}_{expectedResult}`
- 한글 `@DisplayName` 사용

---

## 문서화

### Java (Swagger)

```java
@RestController
@RequestMapping("/api/v1/trading")
@Tag(name = "Trading", description = "주식 거래 API")
@RequiredArgsConstructor
public class TradingController {
  
  private final OrderService orderService;
  
  @Operation(
    summary = "주문 생성",
    description = "매수/매도 주문을 생성합니다"
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "201",
      description = "주문 생성 성공"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "잘못된 요청"
    )
  })
  @PostMapping("/orders")
  public ResponseEntity<ApiResponse<Order>> createOrder(
      @RequestBody @Valid OrderRequest request) {
    Order order = orderService.createOrder(request);
    return ResponseEntity.ok(ApiResponse.success(order));
  }
}
```

### JavaScript (JSDoc)

```javascript
/**
 * 사용자 로그인
 * 
 * @param {string} email - 이메일
 * @param {string} password - 비밀번호
 * @returns {Promise<Object>} 로그인 결과
 * @throws {Error} 로그인 실패 시
 */
export async function login(email, password) {
  const response = await apiClient.post('/api/v1/auth/login', {
    email,
    password
  })
  return response.data
}
```
