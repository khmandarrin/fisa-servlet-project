# 💳 Smart Consumer - 카드 소비 분석 서비스

카드 거래 데이터를 기반으로 **개인 소비 패턴 분석** 및 **또래 비교**를 제공하는 웹 서비스입니다.

---

## 🏗️ 3-Tier 아키텍처

```
┌─────────────────────────────────────────────────────┐
│  [Web 계층]  Nginx (:80)                             │
│  └─ 로드밸런서 (ip_hash Sticky Session)              │
├─────────────────────────────────────────────────────┤
│  [WAS 계층]  Tomcat 1 (:8080) ⟷ Tomcat 2 (:8090)   │
│  └─ 세션 클러스터링 (DeltaManager + Static Member)   │
│  └─ Java Servlet + JSP + HikariCP                    │
├─────────────────────────────────────────────────────┤
│  [Data 계층] MySQL Source (:3308) → Replica (:3309)  │
│  └─ Write/Read 분리 (Source-Replica Replication)     │
└─────────────────────────────────────────────────────┘
```

---

## 📁 프로젝트 구조

```
fisa-servlet-project/
├── database/                    # Data 계층
│   ├── docker-compose.yml       # MySQL Source/Replica 컨테이너
│   ├── setting_guide.md         # 📖 DB 구축 가이드
│   ├── source/init/             # Source DB 초기화 SQL
│   └── replica/                 # Replica DB 설정
├── nginx/                       # Web 계층
│   ├── nginx.conf               # Nginx 로드밸런서 설정
│   └── setting_guide.md         # 📖 Nginx 구성 가이드
├── tomcat/                      # WAS 계층
│   └── setting_guide.md         # 📖 Tomcat 클러스터링 가이드
└── sample-workspace/            # 애플리케이션 소스코드
    └── sample-project/
        └── src/main/
            ├── java/dev/smartconsumer/
            │   ├── common/      # DBUtil, Const, Listener
            │   ├── controller/  # Servlet (Login, Signup, Dashboard, API)
            │   ├── filter/      # AuthFilter, EncodingFilter
            │   └── model/       # DTO, DAO
            └── webapp/          # JSP, CSS, web.xml
```

---

## 🚀 환경 구축 순서

| 순서 | 계층 | 가이드 문서 |
|------|------|------------|
| 1️⃣ | Data | [`database/setting_guide.md`](database/setting_guide.md) |
| 2️⃣ | WAS | [`tomcat/setting_guide.md`](tomcat/setting_guide.md) |
| 3️⃣ | Web | [`nginx/setting_guide.md`](nginx/setting_guide.md) |

---

## ✨ 주요 기능

- **회원가입/로그인**: 고객번호(SEQ) + 비밀번호 + 성별/연령대
- **개인 소비 분석**: 10개 카테고리별 소비 Top 3 및 원형 차트
- **또래 비교**: 동일 연령대/성별 평균 소비와 비교
- **세션 클러스터링**: Tomcat 장애 시 세션 유지 (Failover)
- **DB 이중화**: Write/Read 분리로 성능 최적화

---

## 🗄️ DB 스키마

| 테이블 | 설명 |
|--------|------|
| `USER_INFO` | 고객 정보 (SEQ, PASSWORD, SEX_CD, AGE) |
| `EDU_DATA_F_2` | 카드 거래 데이터 (10개 소비 카테고리) |
| `AVERAGE_DATA_F` | 연령대/성별별 평균 소비 |

---

## 🛠️ 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 17+ |
| WAS | Apache Tomcat 9.0 |
| Web Server | Nginx |
| Database | MySQL 8.0 (Source-Replica) |
| Connection Pool | HikariCP |
| View | JSP + Chart.js |
| Build Tool | Eclipse WTP |
| IDE | Eclipse IDE for Enterprise Java |