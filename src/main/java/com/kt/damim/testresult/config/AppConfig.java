package com.kt.damim.testresult.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    /**
     * Jackson ObjectMapper 빈.
     * 참고:
     * - application.yml의 `spring.jackson.time-zone` 설정(예: Asia/Seoul)이 적용됨.
     * - 필요 시, 타임스탬프 대신 ISO-8601 문자열 출력을 원하면
     *   SerializationFeature.WRITE_DATES_AS_TIMESTAMPS 비활성화를 추가로 설정.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}

