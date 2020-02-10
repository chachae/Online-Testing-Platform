package com.exam.util;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * 日期工具类
 *
 * @author yzn
 * @date 2019/12/17 10:46
 */
public class DateUtil {

  private DateUtil() {}

  /** 日期格式化表达式 */
  private static final DateTimeFormatter FMT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

  /** Example: Mon Jan 20 10:28:25 CST 2020 */
  public static Date getDate() {
    return LocalDateTime.now().toDate();
  }

  /** Example: 2020-01-20 */
  public static LocalDate getLocalDate() {
    return LocalDate.now();
  }

  /** Example: 2020-01-20 */
  public static String getLocalDateStr() {
    return LocalDate.now().toString();
  }

  /** Example: 2020-01-20T10:28:25.801 */
  public static LocalDateTime getLocalDateTime() {
    return LocalDateTime.now();
  }

  /** Example: 2020-01-20T10:28:25.801 */
  public static String getLocalDateTimeStr() {
    return LocalDateTime.now().toString();
  }

  /** Example: 2020-01-20 10:46:01 */
  public static String getFormatLocalDateTimeStr() {
    return FMT.print(LocalDateTime.now());
  }

  /** Example: 1579489127989 */
  public static Long getLocalDateTimeMillis() {
    return LocalDateTime.now().toDate().getTime();
  }

  /**
   * 自定义格式化
   *
   * @param pattern 格式化格式
   * @return /
   */
  public static String getFormatLocalDateTimeStr(String pattern) {
    DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
    return fmt.print(LocalDateTime.now());
  }
}
