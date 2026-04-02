package dalbit.adapter.persistence.redis.config;

import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
public class RedisCacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 공통 기본 설정 (키는 String, 값은 기본 JdkSerializationRedisSerializer 사용)
        RedisCacheConfiguration baseConfig = RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        // Long 타입 전용 설정 (userExternalIdToId 캐시용)
        RedisCacheConfiguration longConfig = baseConfig
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericToStringSerializer<>(Long.class)));

        // Boolean 타입 전용 설정 (queueAuth 캐시용)
        RedisCacheConfiguration booleanConfig = baseConfig
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericToStringSerializer<>(Boolean.class)));

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(baseConfig.entryTtl(Duration.ofHours(1)))
            .withCacheConfiguration("userExternalIdToId", longConfig.entryTtl(Duration.ofDays(7)))
            .withCacheConfiguration("queueAuth", booleanConfig.entryTtl(Duration.ofHours(1)))
            .build();
    }
}
