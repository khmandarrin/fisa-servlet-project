
# 🖥️ WAS 계층: Tomcat 이중화 + 세션 클러스터링 구성 가이드

본 프로젝트의 3-tier 아키텍처 중 **WAS(Web Application Server) 계층** 구성을 위한 가이드입니다.  
Eclipse WTP 환경에서 Tomcat 9.0 인스턴스 2개를 구동하고, 세션 복제(Session Replication)를 설정합니다.

---

## 📌 0. 사전 준비 사항

1. **Eclipse IDE for Enterprise Java Developers** 설치
2. **Apache Tomcat 9.0** 설치 (Eclipse 내에서 Server Runtime으로 등록)
3. Data 계층(MySQL Source/Replica)이 이미 구동 중이어야 합니다 → `database/setting_guide.md` 참조
4. `smart-consumer`가 Eclipse에 Dynamic Web Project로 import 되어 있어야 합니다

---

## 🚀 1. Eclipse에서 Tomcat 서버 2개 생성

Eclipse 하단 **Servers** 탭에서 두 개의 Tomcat 인스턴스를 생성합니다.

| 구분 | Tomcat 1 | Tomcat 2 |
|------|----------|----------|
| HTTP 포트 | `8080` | `8090` |
| Shutdown 포트 | `8005` | `8006` |
| jvmRoute | `tomcat1` | `tomcat2` |
| 클러스터 Receiver 포트 | `4000` | `4001` |

### 1-1. Tomcat 1 생성
1. Servers 탭 → 우클릭 → **New** → **Server**
2. Apache → **Tomcat v9.0 Server** 선택
3. **smart-consumer** 추가 → Finish

### 1-2. Tomcat 2 생성
1. 동일하게 새 서버 생성 (자동으로 `Tomcat v9.0 Server at localhost (2)` 이름 부여)
2. **smart-consumer** 추가 → Finish
3. 서버 더블클릭 → **Ports** 섹션에서:
   - HTTP → `8090`으로 변경
   - Server Shutdown → `8006`으로 변경

---

## 🔧 2. server.xml 수정 (세션 클러스터링)

`Servers/` 디렉토리는 `.gitignore`에 포함되어 있으므로, **클론 후 수동으로 설정**해야 합니다.

### 2-1. Tomcat 1 (`Servers/Tomcat v9.0 Server at localhost-config/server.xml`)

`<Engine>` 태그에 `jvmRoute` 추가:

```xml
<Engine defaultHost="localhost" name="Catalina" jvmRoute="tomcat1">
```

`<Host>` 태그 안에 아래 `<Cluster>` 블록 추가 (⚠️ `<Host>` 안에 넣어야 합니다):

```xml
<Host appBase="webapps" autoDeploy="true" name="localhost" unpackWARs="true">

    <!-- ── Tomcat Session Clustering (Static Membership) ── -->
    <Cluster className="org.apache.catalina.ha.tcp.SimpleTcpCluster"
             channelSendOptions="6">
      <Manager className="org.apache.catalina.ha.session.DeltaManager"
               expireSessionsOnShutdown="false"
               notifyListenersOnReplication="true"/>
      <Channel className="org.apache.catalina.tribes.group.GroupChannel">
        <Receiver className="org.apache.catalina.tribes.transport.nio.NioReceiver"
                  address="127.0.0.1" port="4000" autoBind="100"
                  selectorTimeout="5000" maxThreads="6"/>
        <Sender className="org.apache.catalina.tribes.transport.ReplicationTransmitter">
          <Transport className="org.apache.catalina.tribes.transport.nio.PooledParallelSender"/>
        </Sender>
        <Interceptor className="org.apache.catalina.tribes.group.interceptors.TcpFailureDetector"/>
        <Interceptor className="org.apache.catalina.tribes.group.interceptors.MessageDispatchInterceptor"/>
        <Interceptor className="org.apache.catalina.tribes.group.interceptors.StaticMembershipInterceptor">
          <Member className="org.apache.catalina.tribes.membership.StaticMember"
                  host="127.0.0.1" port="4001" uniqueId="{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2}"/>
        </Interceptor>
      </Channel>
      <Valve className="org.apache.catalina.ha.tcp.ReplicationValve" filter=""/>
      <ClusterListener className="org.apache.catalina.ha.session.ClusterSessionListener"/>
    </Cluster>

    <!-- ... 기존 AccessLogValve, Context 등 ... -->
</Host>
```

### 2-2. Tomcat 2 (`Servers/Tomcat v9.0 Server at localhost (2)-config/server.xml`)

Tomcat 1과 동일한 구조이며, **아래 값만 다릅니다:**

| 항목 | Tomcat 1 | Tomcat 2 |
|------|----------|----------|
| `jvmRoute` | `tomcat1` | `tomcat2` |
| `Connector port` | `8080` | `8090` |
| `Server port` (shutdown) | `8005` | `8006` |
| `Receiver port` | `4000` | `4001` |
| `Member port` (상대방) | `4001` | `4000` |
| `Member uniqueId` (상대방) | `...0,2` | `...0,1` |

---

## 📋 3. 핵심 설정 항목 설명

| 설정 | 설명 |
|------|------|
| `channelSendOptions="6"` | 동기 복제 (SYNCHRONIZED_ACK). 세션 변경이 즉시 복제됨 |
| `DeltaManager` | 변경된 속성만 전송하는 세션 매니저 (all-to-all 복제) |
| `StaticMembershipInterceptor` | 멀티캐스트 대신 정적 IP로 멤버 지정 (로컬 환경에 적합) |
| `NioReceiver` | 비동기 I/O 기반 수신기 |
| `TcpFailureDetector` | TCP 연결 실패 감지 |
| `ReplicationValve` | 요청 처리 완료 후 세션 복제 트리거 |

---

## 🌐 4. web.xml 설정

`src/main/webapp/WEB-INF/web.xml`에 `<distributable/>` 태그가 반드시 있어야 합니다:

```xml
<web-app ...>
  <display-name>Smart Consumer</display-name>

  <!-- Tomcat Clustering: 이 앱이 세션 복제를 지원함을 선언 -->
  <distributable/>

  <!-- ... 나머지 설정 ... -->
</web-app>
```

---

## 🔗 5. Serializable 필수

세션에 저장되는 모든 객체는 `java.io.Serializable`을 구현해야 합니다:

```java
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    // ...
}
```

> ⚠️ `Serializable`을 구현하지 않으면 세션 복제 시 `NotSerializableException`이 발생합니다.

---

## ✅ 6. 확인 방법

1. **두 서버 모두 Start**
2. Tomcat 콘솔에서 아래 메시지 확인:
   ```
   INFO: Manager [/smart-consumer] expiring sessions upon shutdown
   ```
   → DeltaManager가 웹앱에 적용된 것
3. **Member 감지 확인:**
   ```
   INFO: Received member added: ...
   ```
4. **세션 복제 확인:** 로그인 후 한 Tomcat 중지 → 다른 Tomcat으로 요청이 전달될 때 세션 유지

---

## 🐛 트러블슈팅

| 증상 | 원인 | 해결 |
|------|------|------|
| `ClassNotFoundException` | Eclipse 빌드 누락 | Project → Clean → Build All (Ctrl+B) |
| `502 Bad Gateway` | Tomcat 미구동 | Eclipse Servers 탭에서 상태 확인 |
| 로그인 후 세션 유실 | DeltaManager 미적용 | `<Cluster>`가 `<Host>` 안에 있는지 확인 |
| `memberDisappeared` 후 복제 안 됨 | Receiver 포트 충돌/방화벽 | 포트 4000/4001 확인 |
| `NotSerializableException` | DTO에 Serializable 미구현 | `implements Serializable` 추가 |

