package com.talet.talet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    @Primary
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        StringRedisSerializer stringSer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSer);
        redisTemplate.setValueSerializer(stringSer);
        redisTemplate.setHashKeySerializer(stringSer);
        redisTemplate.setHashValueSerializer(stringSer);
        return redisTemplate;
    }
}
