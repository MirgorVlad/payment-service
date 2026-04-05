package org.mirgor.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new CaffeineCacheManager() {
            @Override
            protected Cache<Object, Object> createNativeCaffeineCache(String name) {
                return switch (name) {
                    case "prices"    -> build(500,  Duration.ofMinutes(30));
                    case "snapshots" -> build(1000,  Duration.ofMinutes(5));
                    case "workspace-snapshots"   -> build(100,  Duration.ofHours(1));
                    default          -> build(1000, Duration.ofMinutes(10));
                };
            }
        };
    }

    private Cache<Object, Object> build(int maxSize, Duration ttl) {
        return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(ttl)
                .recordStats()
                .build();
    }
}
