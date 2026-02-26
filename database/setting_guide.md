
# 🗄️ Data 계층: MySQL Source-Replica (이중화) 환경 구축 가이드

본 프로젝트의 3-tier 아키텍처 중 **Data 계층(데이터 영속성)** 구성을 위한 가이드입니다. 
Docker를 이용하여 읽기/쓰기용 Source DB와 읽기 전용 Replica DB를 띄우고, 530만 건의 대용량 카드 트랜잭션 데이터를 고속으로 적재합니다.

---

## 📌 0. 사전 준비 사항
1. **Docker Desktop**이 설치되어 있고 실행 중이어야 합니다.
2. 로컬 포트 `3308`(Source), `3309`(Replica)가 비어있어야 합니다. (기존 로컬 MySQL 충돌 방지)
3. 대용량 데이터 파일인 `EDU_DATA_F.dat` 파일이 필요합니다. 
   > ⚠️ **주의:** 용량이 매우 크므로(약 874MB) 절대 Git에 커밋하지 마세요! (`.gitignore`에 `*.dat` 추가 필수)
4. 스키마 생성용 `schema.sql` 파일이 `docker-compose.yml`과 같은 폴더에 있어야 합니다.

---

## 🚀 1. Docker 컨테이너 실행

터미널(또는 CMD)을 열고 `database` 폴더로 이동한 뒤, 아래 명령어를 실행하여 컨테이너를 백그라운드에서 실행합니다.

```bash
# 기존에 꼬인 데이터가 있다면 초기화 후 실행 (초기 구축 시 권장)
docker-compose down -v

# Source 및 Replica 컨테이너 실행
docker-compose up -d
```

컨테이너가 완전히 켜질 때까지 약 10~20초 정도 대기해 주세요.

## 🔄 2. Replication (데이터 복제) 연동
Windows 환경의 파일 권한 문제 등을 방지하기 위해 my.cnf 대신 docker-compose.yml의 command로 설정을 주입했습니다. 이제 두 DB를 연결합니다.

### 2-1. Source DB 상태 확인
```Bash
docker exec -it mysql-source mysql -u root -p1234
```
접속 후 아래 쿼리를 실행하여 File과 Position 값을 꼭 메모해 둡니다.

```
SQL
SHOW MASTER STATUS;
```

-- 예: File: mysql-bin.000001 / Position: 157
```sql
exit
```

### 2-2. Replica DB에 복제 연결

```Bash
docker exec -it mysql-replica mysql -u root -p1234
```

접속 후, 메모한 값을 넣어 아래 쿼리를 실행합니다. (MySQL 8.0 보안 정책에 따라 GET_SOURCE_PUBLIC_KEY=1 옵션 필수)

``` SQL
-- 1. 복제 설정
CHANGE REPLICATION SOURCE TO 
  SOURCE_HOST='mysql-source', 
  SOURCE_USER='repl_user', 
  SOURCE_PASSWORD='repl_password', 
  SOURCE_LOG_FILE='메모한 File 값', 
  SOURCE_LOG_POS=메모한 Position 값,
  GET_SOURCE_PUBLIC_KEY=1;

-- 2. 복제 시작
START REPLICA;

-- 3. 상태 확인
SHOW REPLICA STATUS \G
```

출력 결과 중 Replica_IO_Running: Yes, Replica_SQL_Running: Yes 두 개가 보이면 연동 성공입니다! (exit로 빠져나옵니다.)

## 🏗️ 3. 스키마 및 테이블 생성
대용량 데이터를 적재하기 전, Source DB에 스키마(card_db)와 테이블을 세팅합니다.

### 3-1. 데이터 파일 및 SQL 파일을 컨테이너 내부로 복사
CMD 창에서 아래 명령어를 실행합니다. (.dat 파일 경로 주의)

```Bash
docker cp schema.sql mysql-source:/var/lib/mysql-files/schema.sql
docker cp EDU_DATA_F.dat mysql-source:/var/lib/mysql-files/EDU_DATA_F.dat
```

### 3-2. 스키마 적용
Source DB 내부로 접속합니다.

```Bash
docker exec -it mysql-source mysql -u root -p1234
``` 
데이터베이스를 생성하고, 복사한 SQL 파일을 실행합니다.

```SQL
CREATE DATABASE card_db;
USE card_db;
source /var/lib/mysql-files/schema.sql;
```

## ⚡ 4. 530만 건 대용량 데이터 초고속 적재
일반적인 INSERT를 사용하면 며칠이 걸릴 수 있습니다. LOAD DATA INFILE과 트랜잭션 제어를 통해 약 1분 30초 만에 데이터를 고속 적재합니다.

여전히 Source DB(mysql-source)에 접속해 있는 상태에서 아래 쿼리를 실행합니다.

```SQL
USE card_db;

-- 1. 적재 속도 최적화를 위해 인덱스/외래키 검사 임시 해제
SET autocommit=0;
SET unique_checks=0;
SET foreign_key_checks=0;

-- 2. 고속 데이터 적재 (약 1~2분 소요)
LOAD DATA INFILE '/var/lib/mysql-files/EDU_DATA_F.dat'
INTO TABLE CARD_TRANSACTION
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\r\n'
IGNORE 1 LINES;

-- 3. 적재 완료 후 설정 복구 및 커밋
SET autocommit=1;
SET foreign_key_checks=1;
SET unique_checks=1;
COMMIT;
```

## ✅ 5. 최종 확인
Source DB와 Replica DB 모두 데이터가 정상적으로 들어갔는지 카운트 쿼리로 확인합니다.

```SQL
-- 기대 결과: 약 5,382,734 건
SELECT COUNT(*) FROM CARD_TRANSACTION;
```

💡 참고: Replica DB는 Source DB의 데이터를 복제해 오는 중이므로(Replication Lag), 일시적으로 Source DB보다 개수가 적게 나올 수 있습니다. 시간이 지나면 100% 동일해집니다.

🎉 이제 Application(Tomcat/Java) 층에서 HikariCP를 설정할 때, 쓰기(Write)는 localhost:3308, 읽기(Read)는 localhost:3309로 분기하여 연결하면 됩니다!