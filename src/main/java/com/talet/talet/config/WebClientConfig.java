package com.talet.talet.config;

import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient ttsWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8000/")
                .build();
    }

    @Bean
    public WebClient ttsApiWebClient(@Value("${supertone.api.key}") String apiKey) {
        return WebClient.builder()
                .baseUrl("https://supertoneapi.com/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, "*/*")
                .defaultHeader("x-sup-api-key", apiKey)
                .build();
    }
}
