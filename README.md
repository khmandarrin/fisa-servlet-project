# 소비 성향 분석 리포트 : 내 소비는 평균일까? 💳

> 나의 소비 패턴을 동일 연령대 · 성별 그룹과 비교해보는 웹 서비스
<img width="1470" height="832" alt="스크린샷 2026-03-01 오후 4 04 09" src="https://github.com/user-attachments/assets/8e0b988f-9b52-4073-a8ac-ed1802c75f52" />
<img width="1470" height="829" alt="스크린샷 2026-03-02 오후 11 49 59" src="https://github.com/user-attachments/assets/375c888a-6bca-4cda-96e1-166fcd6299b3" />

## 📑 목차

- [📌 프로젝트 개요](#-프로젝트-개요)
- [✨ 주요 기능](#-주요-기능)
  - [🔐 로그인](#-로그인)
  - [📊 메인 페이지 (소비 분석 대시보드)](#-메인-페이지-소비-분석-대시보드)
  - [🔓 로그아웃](#-로그아웃)
- [🛠 기술 스택](#-기술-스택)
- [🏗 시스템 아키텍처](#-시스템-아키텍처)
- [🔑 세션 공유 전략](#-세션-공유-전략)
- [🏭 v1 → v2 마이그레이션 (Servlet → Spring IoC)](#-v1--v2-마이그레이션-servlet--spring-ioc)
- [🗄 DB 설계](#-db-설계)
- [🚀 실행 방법](#-실행-방법)
- [☠️ 데이터 모델링 이슈](#️-데이터-모델링-이슈-시계열-데이터-처리에-따른-외래키-설정-제약)
- [🚀 성능 개선](#-성능-개선-대시보드-로딩-속도-최적화)
---

## 📌 프로젝트 개요

본 서비스는 수백만 건의 카드 소비 데이터를 기반으로,  
로그인한 사용자의 소비 패턴을 동일 연령대 및 성별 그룹(또래 집단)과 비교 분석합니다.

사용자의 실제 소비 데이터와 또래 평균 소비 데이터를 시각적으로 비교하여  
자신의 소비 성향을 직관적으로 파악할 수 있도록 설계되었습니다.

---

## ✨ 주요 기능

### 🔐 로그인

- 고객번호(SEQ) + 비밀번호 기반 사용자 인증
- JSESSIONID 쿠키 기반 세션 관리
- **Tomcat 클러스터링 기반 세션 동기화 (2대 서버 구성)**
- `CharacterEncodingFilter`  
  → 전역 UTF-8 인코딩 처리
- `AuthFilter`  
  → 미인증 요청 차단 및 로그인 페이지로 리다이렉트

---

## 📊 메인 페이지 (소비 분석 대시보드)

로그인한 사용자의 소비 데이터를 또래 집단과 비교 분석합니다.

### 1️⃣ 개인 소비 분석
- 사용자의 소비 데이터 기준 **지출 상위 Top 5 카테고리** 추출
- 원형 차트(Pie Chart)로 시각화

### 2️⃣ 또래 소비 분석
- 동일 연령대 + 성별 기준 그룹 분류
- 해당 그룹의 평균 소비 금액 기준 **Top 5 카테고리** 추출
- 원형 차트(Pie Chart)로 시각화

### 3️⃣ 소비 패턴 비교
- 개인 소비 Top 3 vs 또래 평균 Top 3 비교
- 소비 성향 차이 직관적으로 확인 가능

---

### 🔓 로그아웃

- 세션 무효화 (invalidate)

---

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| Backend | Java 17, Servlet 4.0, JSP, **Spring Framework 5.3** |
| WAS | Apache Tomcat 9 × 2대 (8080, 8090) |
| DB | MySQL 8 (Docker), HikariCP Connection Pool |
| Proxy / LB | Nginx (리버스 프록시 + 부하분산) |
| 인증 | HttpSession (JSESSIONID) + Tomcat Clustering 기반 세션 복제 |
| 빌드 | Maven |
| 개발 도구 | Eclipse IDE, MySQL |

---

## 🏗 시스템 아키텍처

```
클라이언트 (웹 브라우저 / API 클라이언트)
        │ HTTP 요청
        ▼
┌─────────────────────┐
│       Nginx         │  ← Presentation 계층 (정적 리소스, 트래픽 제어)
│                     │    
└──────────┬──────────┘
         부하분산 
    ┌───────┴────────┐
    ▼                ▼
┌────────┐      ┌────────┐
│Tomcat 1│      │Tomcat 2│  ← Application 계층 (Spring IoC + Servlets & JSP)
│ :8080  │      │ :8090  │
└────┬───┘      └───┬────┘
     │   Read/Write │
     ▼              ▼
┌──────────────────────┐
│  MySQL Source (R/W)  │  ← Data 계층 (Docker Container)
└──────────────────────┘
         │ Replication
         ▼
┌──────────────────────┐
│  MySQL Replica (R)   │
└──────────────────────┘
```

---

## 🔑 세션 공유 전략

2대의 WAS 환경에서 발생할 수 있는 세션 불일치(Session Mismatch) 문제를 해결하기 위해  
**Tomcat Clustering (all-to-all) 기반 세션 복제 방식**을 적용했습니다.

### 🤔 왜 Tomcat Clustering인가?

세션 공유 방식을 선택할 때 Redis, DB 기반 세션 저장소 등의 대안을 검토했으나,  
아래의 기술적 판단에 의해 **Tomcat Clustering**을 선택했습니다.

| 방식 | 장점 | 단점 | 채택 여부 |
|------|------|------|-----------|
| **Tomcat Clustering (all-to-all)** | 추가 인프라 불필요, WAS 메모리만으로 충분, 구현이 빠름 | 노드 수 증가 시 복제 부하 증가 | ✅ **채택** |
| Redis 기반 세션 저장소 | 대규모 클러스터에 적합, 빠른 I/O | 별도 Redis 서버 필요 → **메모리 비용 증가**, 운영 복잡도 상승 | ❌ |
| DB 기반 세션 저장소 | 영속성 보장 | 매 요청 시 DB I/O 발생 → 성능 저하, 세션 만료 관리 필요 | ❌ |
| Sticky Session | 구현 간단 | 특정 서버 장애 시 세션 유실, 부하 편중 가능 | ❌ |

**결정 근거:**

1. **서비스 특성**: 소비 분석 리포트 서비스는 동시 접속자가 대규모가 아닌 내부/교육용 서비스로, 요청 빈도가 상대적으로 낮음
2. **인프라 비용**: Redis 서버를 별도로 운영하면 메모리 비용이 추가로 발생하는 반면, WAS 2대의 JVM 힙 메모리만으로도 세션 데이터를 충분히 수용 가능
3. **구현 속도**: `server.xml`의 `<Cluster>` 설정과 `web.xml`의 `<distributable/>` 선언만으로 즉시 적용 가능하여, 제한된 개발 기간 내에 빠르게 구현 완료
4. **2대 WAS 규모**: all-to-all 방식은 노드 수가 적을 때(2~3대) 가장 효율적이며, 현재 구성에 최적

---

### 📌 적용 효과

- 로드밸런싱 환경에서도 로그인 상태 유지
- 별도의 외부 세션 저장소 없이 구성 가능
- 2대 WAS 규모에 적합한 구조

---

## 🏭 v1 → v2 마이그레이션 (Servlet → Spring IoC)

### 버전 개요

| 구분 | v1 (Servlet) | v2 (Spring IoC) |
|------|-------------|-----------------|
| 객체 관리 | 서블릿이 직접 `new`로 생성 | Spring 컨테이너가 빈으로 관리 |
| 의존성 주입 | 없음 (수동 생성) | 생성자 주입 (`@Autowired`) |
| DataSource | `ApplicationContextListener`에서 수동 생성 → `ServletContext` 저장 | Spring XML 빈으로 선언 |
| 계층 구조 | Controller → DAO | Controller → **Service** → DAO |
| 빌드 | `WEB-INF/lib`에 JAR 수동 배치 | Maven `pom.xml`로 의존성 관리 |

### 비즈니스 객체(Bean) 식별

| 클래스 | 역할 | 어노테이션 | 의존성 주입 |
|--------|------|-----------|------------|
| `UserDAO` | 데이터 접근 | `@Repository` | DataSource 2개 (`@Qualifier`) |
| `AnalysisDAO` | 데이터 접근 | `@Repository` | DataSource 1개 (`@Qualifier`) |
| `UserService` | 비즈니스 로직 | `@Service` | `UserDAO` 생성자 주입 |
| `AnalysisService` | 비즈니스 로직 | `@Service` | `AnalysisDAO` 생성자 주입 |

> **Bean이 아닌 것**: `UserDTO`, `StatDTO` (데이터 전송 객체), `Const` (상수 클래스)

### 의존성 흐름

```
v1:  Servlet ──new──→ DAO ──ServletContext──→ DataSource

v2:  Servlet ──getBean──→ Service ──주입──→ DAO ──주입──→ DataSource (Spring Bean)
```

> 상세 변경 내역은 [`v1_vs_v2_comparison.md`](smart-consumer-workspace/smart-consumer/v1_vs_v2_comparison.md) 참고

---

## 🗄 DB 설계

| 테이블 | 설명 |
|--------|------|
| `EDU_DATA_F_2` | 전처리된 소비 이력 데이터 (중분류 제거, 대분류 기준) |
| `USER_INFO` | 사용자 정보 (고객번호, 비밀번호, 성별, 연령대) |
| `AVERAGE_DATA_F` | 연령대 + 성별 기준 평균 소비 통계 |
<img width="50%" alt="ERD_dark" src="https://github.com/user-attachments/assets/027f7890-0c5b-4f06-8332-cdf91bd8100a" />

---

## 🚀 실행 방법



| 순서 | 계층 | 환경 구축 가이드 문서 |
|------|------|----------------|
| 1️⃣ | Data | [`database/setting_guide.md`](database/setting_guide.md) |
| 2️⃣ | WAS | [`tomcat/setting_guide.md`](tomcat/setting_guide.md) |
| 3️⃣ | Web | [`nginx/setting_guide.md`](nginx/setting_guide.md) |

```bash
# 1. MySQL Docker 컨테이너 실행
docker start [mysql-container]

# 2. Tomcat 1 (8080) 실행

# 3. Tomcat 2 (8090) 실행

# 4. Nginx 실행
sudo service nginx restart

# 5. 브라우저에서 접속
http://localhost/smart-consumer
```

---

## ☠️ 데이터 모델링 이슈: 시계열 데이터 처리에 따른 외래키 설정 제약

1. **현황**: USER_INFO는 SEQ를 PK로 가져가기 위해 AGE를 단일값(MAX)으로 가공.

2. **충돌**: EDU_DATA_F_2는 연도별 이력을 관리하므로 동일 SEQ에 대해 다수의 AGE 존재.

3. **결정**: (SEQ, AGE) 복합키 기반의 외래키 설정 시 참조 무결성 오류가 발생하여 FK 제약 조건 제외.

4. **한계**: DB 수준의 참조 무결성을 보장할 수 없으며, 상위 테이블 수정 시 하위 테이블의 데이터 정합성을 수동으로 관리해야 하는 관리적 부담 발생.


## 🚀 성능 개선: 대시보드 로딩 속도 최적화

### 1️⃣ 문제 상황
로그인 후 대시보드로 이동하는 과정에서 소비 데이터 집계 쿼리 처리로 인해  
약 **1분 이상의 로딩 지연** 발생.

### 2️⃣ 원인 분석
`EDU_DATA_F_2` 테이블에서 회원 번호(`SEQ`) 기준으로 `SUM` 집계를 수행했으나,  
해당 컬럼에 인덱스가 존재하지 않아 **Full Table Scan** 발생.

### 3️⃣ 개선 조치
`EDU_DATA_F_2` 테이블의 `SEQ` 컬럼에 인덱스를 추가하여 조회 성능 개선.

```sql
CREATE INDEX idx_edu_data_f_2_seq
ON EDU_DATA_F_2 (SEQ);
```
