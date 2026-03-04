
# ⚖️ Web 계층: Nginx 로드밸런서 구성 가이드

본 프로젝트의 3-tier 아키텍처 중 **Web 계층(로드밸런싱)** 구성을 위한 가이드입니다.  
Nginx를 역방향 프록시로 사용하여 두 Tomcat 인스턴스(8080, 8090)에 트래픽을 분산합니다.

---

## 📌 0. 사전 준비 사항

1. **Nginx**가 설치되어 있어야 합니다
   ```bash
   # Ubuntu/WSL
   sudo apt install nginx

   # macOS
   brew install nginx
   ```
2. 포트 `80`이 비어있어야 합니다
3. Tomcat 2개가 `8080`, `8090` 포트에서 구동 중이어야 합니다 → `tomcat/setting_guide.md` 참조

---

## 🚀 1. Nginx 설정 파일 적용

본 프로젝트의 `nginx/nginx.conf` 파일을 시스템 Nginx 설정으로 복사합니다.

```bash
# 기존 설정 백업
sudo cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.bak

# 프로젝트 설정 적용
sudo cp nginx/nginx.conf /etc/nginx/nginx.conf

# 설정 문법 검증
sudo nginx -t

# Nginx 재시작
sudo systemctl restart nginx
```

---

## 📋 2. 설정 파일 설명 (`nginx.conf`)

```nginx
events {
    worker_connections 1024;  # 한 워커 프로세스당 동시 연결 수
}

http {
    upstream backend_servers {
        ip_hash;                      # 같은 클라이언트 IP → 같은 서버로 고정
        server 127.0.0.1:8080;        # Tomcat 1
        server 127.0.0.1:8090;        # Tomcat 2
    }

    server {
        listen       80;
        server_name  localhost;

        location / {
            proxy_pass http://backend_servers;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }
    }
}
```

### 핵심 설정

| 설정 | 설명 |
|------|------|
| `ip_hash` | 클라이언트 IP 기반 Sticky Session. 같은 사용자는 항상 같은 Tomcat으로 라우팅 |
| `proxy_pass` | 클라이언트 요청을 `backend_servers` 업스트림 그룹으로 전달 |
| `proxy_set_header` | 원본 클라이언트 정보를 백엔드에 전달 (IP, Host 등) |

---

## ✅ 3. 확인 방법

```bash
# Nginx 상태 확인
sudo systemctl status nginx

# 접속 테스트 (브라우저에서도 가능)
curl -I http://localhost/smart-consumer/login
```

정상 응답 시 `HTTP/1.1 200 OK` 또는 `302 Found` 가 반환됩니다.

---

## 📊 4. 로그 확인

```bash
# 실시간 접속 로그 확인
sudo tail -f /var/log/nginx/access.log

# 에러 로그 확인
sudo tail -f /var/log/nginx/error.log

# 또는 systemd 저널
sudo journalctl -u nginx -f
```

로그에서 `upstream:` 항목을 통해 어떤 Tomcat으로 요청이 전달되었는지 확인할 수 있습니다.

---

## 🐛 트러블슈팅

| 증상 | 원인 | 해결 |
|------|------|------|
| `502 Bad Gateway` | Tomcat이 구동되지 않음 | Tomcat 시작 상태 확인 |
| `Connection refused` | 포트 불일치 | `upstream`의 포트와 Tomcat 포트 일치 여부 확인 |
| `ERR_TOO_MANY_REDIRECTS` | Redirect Loop | `ip_hash` 활성화 여부 확인 |
| 포트 80 `Address already in use` | 다른 프로세스가 80 사용 중 | `sudo lsof -i :80` 으로 확인 |

