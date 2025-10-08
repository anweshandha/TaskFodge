package org.example.service.Implementation.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    // central cache names and TTLs
    public static final String TASKS_CACHE = "tasks";
    public static final String TASKS_CLOSE_DEADLINE_CACHE = "tasks_close_deadline";
    public static final String USERS_CACHE = "users";

    @Bean
    public RedisCacheConfiguration defaultCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // default TTL; override per cache below
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = defaultCacheConfig();

        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        // tasks list cache live for 5 minutes
        cacheConfigs.put(TASKS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        // close-deadline queries are short lived
        cacheConfigs.put(TASKS_CLOSE_DEADLINE_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(1)));
        // users cache
        cacheConfigs.put(USERS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(30)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .transactionAware()
                .build();
    }
}