package com.chachae.exam.common.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 相关配置
 *
 * @author chachae
 * @since 2020/1/15 20:19
 */
@Configuration
public class RedisConfig {

  /** RedisTemplate 配置 */
  @Bean
  @ConditionalOnClass(RedisOperations.class)
  public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    Jackson2JsonRedisSerializer<Object> j2s = new Jackson2JsonRedisSerializer<>(Object.class);
    ObjectMapper mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    mapper.activateDefaultTyping(
        mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
    j2s.setObjectMapper(mapper);
    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    // key采用 String的序列化方式
    template.setKeySerializer(stringRedisSerializer);
    // hash的 key也采用 String的序列化方式
    template.setHashKeySerializer(stringRedisSerializer);
    // value序列化方式采用 jackson
    template.setValueSerializer(j2s);
    // hash的 value序列化方式采用 jackson
    template.setHashValueSerializer(j2s);
    template.afterPropertiesSet();

    return template;
  }
}
