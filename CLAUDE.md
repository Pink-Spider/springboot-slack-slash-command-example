# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

Slack Slash Command를 받아 처리하는 Spring Boot 예제 애플리케이션이다.
ngrok 설정과 Slack 앱 설정 절차는 `README.md`에 있다.

- Java 25, Spring Boot 4.1.1, Gradle 9.8.2 (wrapper)
- Spring Boot 3.x 버전 코드는 `spring-boot-3x` 브랜치에 있다. `main`은 Boot 4.x이다.
- `build.gradle`이 Java toolchain을 25로 고정한다. 로컬에 JDK 25가 없으면 `settings.gradle`의 foojay resolver가 자동으로 내려받는다.
- Boot 4에서는 web 스타터 이름이 `spring-boot-starter-webmvc`이다. `spring-boot-starter-web`을 다시 쓰지 않는다.
- README, 코드 주석, Slack 응답 문구는 한국어로 쓴다.

## 명령어

```bash
./gradlew build      # 컴파일 + 테스트 + jar 생성 (build/libs/slack-slash-command-example-0.0.1-SNAPSHOT.jar)
./gradlew bootRun    # http://localhost:8080 에서 실행
./gradlew test       # 전체 테스트
./gradlew test --tests 'io.pinkspider.example.SlackSlashCommandExampleApplicationTests'   # 테스트 클래스 하나
./gradlew test --tests '*ApplicationTests.contextLoads'                                   # 테스트 메서드 하나
```

lint나 formatter 설정은 없다.

Slack 없이 엔드포인트를 확인하려면, Slack이 보내는 form 요청을 curl로 흉내 낸다.

```bash
curl -X POST http://localhost:8080/slack/commands \
  --data-urlencode 'command=/hello' \
  --data-urlencode 'text=홍길동'
```

## 구조

요청 처리 로직은 모두 `SlackCommandController` 하나에 있다.

- Slack은 모든 Slash Command를 같은 URL `POST /slack/commands`로 보낸다. 형식은 `application/x-www-form-urlencoded`이다.
- 컨트롤러는 form 필드를 `Map<String, String>`으로 받는다. 그리고 `command` 값으로 if 분기를 해서 명령어를 고른다.
- 응답 본문은 평문 문자열이다. Slack은 이 문자열을 사용자에게 그대로 보여 준다.
- 오류가 나도 HTTP 200과 안내 문구를 돌려준다. Slack 사용자가 오류 안내를 볼 수 있게 하려는 의도이다.

새 명령어를 추가하는 순서:

1. `SlackCommandController.handleCommand`에 `command` 분기를 추가한다.
2. 마지막의 "알 수 없는 명령어" 안내 문구에 새 명령어를 추가한다.
3. `README.md`의 "지원 명령어" 표에 새 명령어를 추가한다.
4. Slack 앱 설정의 Slash Commands에 같은 Request URL로 명령어를 등록한다. 이 단계는 코드 밖의 작업이므로 사용자에게 알린다.

## 알아 둘 점

- `com.slack.api:slack-api-client` 의존성은 선언되어 있다. 하지만 아직 어떤 코드도 이 라이브러리를 쓰지 않는다.
- Slack 요청 서명(signing secret으로 요청이 Slack에서 왔는지 확인하는 절차) 검증이 없다. 누구나 엔드포인트를 호출할 수 있다.
- Slack은 Slash Command 응답을 3초 안에 받아야 한다. 오래 걸리는 작업을 추가하면, 먼저 짧게 응답하고 요청의 `response_url`로 결과를 나중에 보내야 한다.
- 테스트는 Spring context 로딩 테스트(`contextLoads`) 하나뿐이다.
