package com.chachae.exam.common.util;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chachae
 * @since 2020/1/16 17:23
 */
@Slf4j
public class JSONUtil {

  private JSONUtil() {}

  private static ObjectMapper objectMapper = new ObjectMapper();

  // 加载Jackson配置
  static {
    // config
    // 在反序列化时忽略在 json 中存在但 Java 对象不存在的属性
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // 在序列化时日期格式默认为 yyyy-MM-dd'T'HH:mm:ss
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    // 在序列化时忽略值为 null 的属性
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    // 忽略值为默认值的属性
    objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT);
  }

  /**
   * obj转String
   *
   * @param src 传入参数
   * @param <T> 类型
   * @return 返回String
   */
  public static <T> String obj2String(T src) {
    if (src == null) {
      return null;
    }
    try {
      return src instanceof String ? (String) src : objectMapper.writeValueAsString(src);
    } catch (Exception e) {
      log.error(ExceptionUtil.stacktraceToString(e));
      return null;
    }
  }

  /**
   * String转obj,反序列化
   *
   * @param src 传入参数
   * @param typeReference 对进行泛型的反序列化，使用TypeReference可以明确的指定反序列化的类型，
   * @param <T> 类型
   * @return obj
   */
  @SuppressWarnings("unchecked")
  public static <T> T toBean(String src, TypeReference<T> typeReference) {
    if (src == null || typeReference == null) {
      return null;
    }
    try {
      return (T)
          (typeReference.getType().equals(String.class)
              ? src
              : objectMapper.readValue(src, typeReference));
    } catch (Exception e) {
      log.error(ExceptionUtil.stacktraceToString(e));
      return null;
    }
  }

  /**
   * JSON字符串转JSONObject对象
   *
   * @param jsonStr JSON字符串
   * @return JSONObject
   */
  public static JSONObject parseObj(String jsonStr) {
    return new JSONObject(jsonStr);
  }

  /**
   * JSON字符串转JSONObject对象<br>
   * 此方法会忽略空值，但是对JSON字符串不影响
   *
   * @param obj Bean对象或者Map
   * @return JSONObject
   */
  public static JSONObject parseObj(Object obj) {
    return new JSONObject(obj);
  }
}
