package ru.freelib.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.ai")
public class AiClientConfig {
    private String genUrl;
    private String embedUrl;
    private String modelGen;
    private String modelEmbed;
    private int timeoutSec;

    @Bean
    public OkHttpClient aiHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(timeoutSec, TimeUnit.SECONDS)
                .readTimeout(timeoutSec, TimeUnit.SECONDS)
                .writeTimeout(timeoutSec, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public ObjectMapper aiObjectMapper() {
        return new ObjectMapper();
    }
}