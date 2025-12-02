# Spring Boot Slack Slash Command Example

Slack Slash Command를 처리하는 Spring Boot 애플리케이션 예제입니다.

## 프로젝트 개요

이 프로젝트는 Slack의 Slash Command 기능을 Spring Boot 백엔드와 통합하는 방법을 보여주는 예제 애플리케이션입니다. 사용자가 Slack에서 `/hello` 명령어를 입력하면, 서버가 이를 처리하고 응답을 반환합니다.

## 기술 스택

- **Java 24**
- **Spring Boot 3.5.0**
- **Gradle**
- **Slack API Client 1.39.0**
- **Lombok**

## 프로젝트 구조

```
src/
├── main/
│   ├── java/io/pinkspider/example/
│   │   ├── SlackSlashCommandExampleApplication.java  # 메인 애플리케이션
│   │   └── controller/
│   │       └── SlackCommandController.java           # Slack 명령어 핸들러
│   └── resources/
│       └── application.yml                           # 애플리케이션 설정
└── test/
    └── java/io/pinkspider/example/
        └── SlackSlashCommandExampleApplicationTests.java
```

## 지원 명령어

| 명령어 | 설명 | 예시 |
|--------|------|------|
| `/hello [이름]` | 인사말을 반환합니다 | `/hello 홍길동` → "홍길동 님 안녕하세요." |

## 설치 및 실행

### 1. 프로젝트 빌드

```bash
./gradlew build
```

### 2. 애플리케이션 실행

```bash
./gradlew bootRun
```

또는

```bash
java -jar build/libs/slack-slash-command-example-0.0.1-SNAPSHOT.jar
```

애플리케이션은 기본적으로 `http://localhost:8080`에서 실행됩니다.

## ngrok 설정

Slack은 HTTPS를 통해 외부에서 접근 가능한 URL이 필요합니다. 로컬 개발 환경에서는 **ngrok**을 사용하여 로컬 서버를 인터넷에 노출할 수 있습니다.

### ngrok이란?

ngrok은 로컬 서버를 인터넷에 안전하게 노출시켜주는 터널링 도구입니다. 로컬에서 실행 중인 애플리케이션에 외부에서 접근할 수 있는 공용 URL을 제공합니다.

### ngrok 설치

#### macOS

**Homebrew 사용 (권장)**
```bash
brew install ngrok
```

**직접 다운로드**
1. [ngrok 다운로드 페이지](https://ngrok.com/download)에서 macOS 버전을 다운로드합니다.
2. 다운로드한 zip 파일의 압축을 해제합니다.
3. ngrok 실행 파일을 `/usr/local/bin` 디렉토리로 이동합니다:
   ```bash
   sudo mv ngrok /usr/local/bin/
   ```

#### Windows

**Chocolatey 사용 (권장)**
```powershell
choco install ngrok
```

**Scoop 사용**
```powershell
scoop install ngrok
```

**직접 다운로드**
1. [ngrok 다운로드 페이지](https://ngrok.com/download)에서 Windows 버전을 다운로드합니다.
2. 다운로드한 zip 파일의 압축을 해제합니다.
3. `ngrok.exe` 파일을 원하는 디렉토리에 배치합니다.
4. (선택사항) 해당 디렉토리를 시스템 PATH 환경변수에 추가하면 어디서든 ngrok을 실행할 수 있습니다:
   - **시스템 속성** → **고급** → **환경 변수** → **Path** 편집 → ngrok이 있는 디렉토리 추가

### ngrok 계정 설정 (선택사항)

무료 계정을 생성하면 더 긴 세션 시간과 추가 기능을 사용할 수 있습니다.

```bash
# ngrok 계정 생성 후 authtoken 설정
ngrok config add-authtoken YOUR_AUTH_TOKEN
```

### ngrok 실행

```bash
ngrok http 8080
```

실행 후 터미널에 다음과 같은 출력이 표시됩니다:

```
Session Status                online
Account                       your-email@example.com
Version                       3.x.x
Region                        Japan (jp)
Forwarding                    https://xxxx-xxx-xxx-xxx-xxx.ngrok-free.app -> http://localhost:8080
```

`Forwarding`에 표시된 HTTPS URL (예: `https://xxxx-xxx-xxx-xxx-xxx.ngrok-free.app`)을 Slack 앱 설정에서 사용합니다.

## Slack 앱 설정

### 1. Slack 앱 생성

1. [Slack API](https://api.slack.com/apps)에 접속합니다.
2. **Create New App** 버튼을 클릭합니다.
3. **From scratch**를 선택합니다.
4. 앱 이름과 워크스페이스를 선택하고 생성합니다.

### 2. Slash Command 설정

1. 왼쪽 메뉴에서 **Slash Commands**를 선택합니다.
2. **Create New Command**를 클릭합니다.
3. 다음 정보를 입력합니다:
   - **Command**: `/hello`
   - **Request URL**: `https://[ngrok-url]/slack/commands`
   - **Short Description**: 인사말 명령어
   - **Usage Hint**: [이름]

### 3. 앱 설치

1. 왼쪽 메뉴에서 **Install App**을 선택합니다.
2. **Install to Workspace**를 클릭하여 워크스페이스에 앱을 설치합니다.

## API 엔드포인트

| Method | Endpoint | Content-Type | 설명 |
|--------|----------|--------------|------|
| POST | `/slack/commands` | `application/x-www-form-urlencoded` | Slack Slash Command 처리 |

### 요청 파라미터 (Slack에서 자동 전송)

| 파라미터 | 설명 |
|----------|------|
| `command` | 실행된 명령어 (예: `/hello`) |
| `text` | 명령어 뒤에 입력된 텍스트 |
| `user_id` | 명령어를 실행한 사용자 ID |
| `channel_id` | 명령어가 실행된 채널 ID |

## 동작 흐름

```
┌─────────┐         ┌─────────┐         ┌─────────────────┐
│  Slack  │         │  ngrok  │         │  Spring Boot    │
│  User   │         │  Tunnel │         │  Application    │
└────┬────┘         └────┬────┘         └────────┬────────┘
     │                   │                       │
     │ /hello 홍길동     │                       │
     │──────────────────>│                       │
     │                   │ POST /slack/commands  │
     │                   │──────────────────────>│
     │                   │                       │
     │                   │    "홍길동 님 안녕하세요."   │
     │                   │<──────────────────────│
     │  응답 표시        │                       │
     │<──────────────────│                       │
     │                   │                       │
```

## 개발 참고사항

- 로컬 개발 시 ngrok URL은 재시작할 때마다 변경됩니다. Slack 앱의 Request URL을 업데이트해야 합니다.
- 유료 ngrok 플랜을 사용하면 고정 도메인을 사용할 수 있습니다.
- 프로덕션 환경에서는 ngrok 대신 실제 도메인과 HTTPS 인증서를 사용하세요.

## 라이선스

MIT License
