package com.sak.wifi.config;

import com.sak.wifi.dto.DashboardResponseDTO;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory connectionFactory
    ) {

            JacksonJsonRedisSerializer<DashboardResponseDTO> serializer =
                    new JacksonJsonRedisSerializer<>(DashboardResponseDTO.class);

            RedisCacheConfiguration config =
                    RedisCacheConfiguration.defaultCacheConfig()
                            .entryTtl(Duration.ofMinutes(10))
                            .serializeValuesWith(
                                    RedisSerializationContext.SerializationPair
                                            .fromSerializer(serializer)
                            );

            return RedisCacheManager
                    .builder(connectionFactory)
                    .cacheDefaults(config)
                    .build();
    }
}
