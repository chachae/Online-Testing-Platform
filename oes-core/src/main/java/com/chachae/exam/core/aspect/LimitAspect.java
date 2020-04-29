package com.chachae.exam.core.aspect;

import com.chachae.exam.common.base.LimitType;
import com.chachae.exam.common.exception.LimitAccessException;
import com.chachae.exam.common.util.HttpUtil;
import com.chachae.exam.core.annotation.Limit;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Method;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

/**
 * 接口限流切面
 *
 * @author chachae
 */
@Slf4j
@Aspect
@Order(1)
@Component
public class LimitAspect extends BaseAspectSupport {

  @Resource
  private RedisTemplate<String, Object> redisTemplate;

  @Pointcut("@annotation(com.chachae.exam.core.annotation.Limit)")
  public void pointcut() {
  }

  @Around("pointcut()")
  public Object around(ProceedingJoinPoint point) throws Throwable {
    HttpServletRequest request = HttpUtil.getHttpServletRequest();
    Method method = resolveMethod(point);
    Limit limitAnnotation = method.getAnnotation(Limit.class);
    LimitType limitType = limitAnnotation.limitType();
    String key;
    String ip = HttpUtil.getIpAddr(request);
    int limitPeriod = limitAnnotation.period();
    int limitCount = limitAnnotation.count();
    switch (limitType) {
      case IP:
        key = ip;
        break;
      case CUSTOMER:
        key = limitAnnotation.key();
        break;
      default:
        key = method.getName().toUpperCase();
    }
    ImmutableList<String> keys =
        ImmutableList.of(StringUtils.join(limitAnnotation.prefix() + "_", key, ip));
    String luaScript = buildLuaScript();
    RedisScript<Number> redisScript = new DefaultRedisScript<>(luaScript, Number.class);
    Number count = redisTemplate.execute(redisScript, keys, limitCount, limitPeriod);
    if (count != null && count.intValue() <= limitCount) {
      return point.proceed();
    } else {
      throw new LimitAccessException("接口访问超出频率限制");
    }
  }

  /**
   * 限流脚本 调用的时候不超过阈值，则直接返回并执行计算器自加。
   *
   * @return lua脚本
   */
  private String buildLuaScript() {
    return "local c"
        + "\nc = redis.call('get',KEYS[1])"
        + "\nif c and tonumber(c) > tonumber(ARGV[1]) then"
        + "\nreturn c;"
        + "\nend"
        + "\nc = redis.call('incr',KEYS[1])"
        + "\nif tonumber(c) == 1 then"
        + "\nredis.call('expire',KEYS[1],ARGV[2])"
        + "\nend"
        + "\nreturn c;";
  }
}
