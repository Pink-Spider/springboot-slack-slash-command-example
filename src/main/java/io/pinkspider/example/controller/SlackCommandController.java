package io.pinkspider.example.controller;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/slack")
@Slf4j
public class SlackCommandController {

    @PostMapping(value = "/commands", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleCommand(@RequestParam Map<String, String> payload) {

        try {
            // 디버깅 로그
            log.info("Slack command received: {}", payload);

            String command = payload.get("command");
            String text = payload.get("text");
            String userId = payload.get("user_id");
            String channelId = payload.get("channel_id");

            // 명령어 처리 로직
            if ("/hello".equalsIgnoreCase(StringUtils.trimToEmpty(command))) {
                if (StringUtils.isBlank(text)) {
                    return ResponseEntity.ok("올바른 숫자를 입력하세요. 사용법: /hello somebody");
                } else {
                    return ResponseEntity.ok(text.trim() + " 님 안녕하세요.");
                }
            }

            return ResponseEntity.ok("알 수 없는 명령어입니다. 사용 가능한 명령어: /hello [문자]");

        } catch (Exception e) {
            log.error("Slack command processing error", e);
            return ResponseEntity.ok("명령어 처리 중 오류가 발생했습니다.");
        }
    }
}
