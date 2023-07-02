/*
 * Copyright (c) 2023 DSGIMHANA
 * Author: H.G.D.S GIMHANA
 */
package com.dsgimhana.productservice.config;

import io.lettuce.core.ReadFrom;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig implements CachingConfigurer {

  private static final String CACHE_FAILURE_LOG_MESSAGE =
      "Failure getting from cache: %s, exception: %s";

  @Value("${spring.cache.redis.time-to-live}")
  private long redisTimeToLive;

  @Value("${spring.data.redis.timeout}")
  private Duration redisCommandTimeout;

  private final RedisProperties redisProperties;

  @Bean
  protected LettuceConnectionFactory redisConnectionFactory() {
    RedisSentinelConfiguration sentinelConfig =
        new RedisSentinelConfiguration().master(redisProperties.getSentinel().getMaster());
    redisProperties
        .getSentinel()
        .getNodes()
        .forEach(s -> sentinelConfig.sentinel(s, redisProperties.getPort()));
    sentinelConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));

    LettuceClientConfiguration clientConfig =
        LettuceClientConfiguration.builder()
            .commandTimeout(redisCommandTimeout)
            .readFrom(ReadFrom.REPLICA_PREFERRED)
            .build();
    return new LettuceConnectionFactory(sentinelConfig, clientConfig);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
    redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    return redisTemplate;
  }

  @Override
  @Bean
  public RedisCacheManager cacheManager() {
    return RedisCacheManager.builder(this.redisConnectionFactory())
        .cacheDefaults(this.cacheConfiguration())
        .build();
  }

  @Bean
  public RedisCacheConfiguration cacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(redisTimeToLive))
        .disableCachingNullValues()
        .serializeValuesWith(
            SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
  }

  @Override
  public CacheErrorHandler errorHandler() {
    return new CacheErrorHandler() {
      @Override
      public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.error(
            String.format(CACHE_FAILURE_LOG_MESSAGE, cache.getName(), exception.toString()),
            exception);
      }

      @Override
      public void handleCachePutError(
          RuntimeException exception, Cache cache, Object key, Object value) {
        log.error(
            String.format(CACHE_FAILURE_LOG_MESSAGE, cache.getName(), exception.toString()),
            exception);
      }

      @Override
      public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.error(
            String.format(CACHE_FAILURE_LOG_MESSAGE, cache.getName(), exception.toString()),
            exception);
      }

      @Override
      public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.error(
            String.format(CACHE_FAILURE_LOG_MESSAGE, cache.getName(), exception.toString()),
            exception);
      }
    };
  }
}
