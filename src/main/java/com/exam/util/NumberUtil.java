package com.exam.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 拓展 HuTool NumberUtil
 *
 * @author yzn
 * @since 2020/2/6 21:24
 */
public class NumberUtil extends cn.hutool.core.util.NumberUtil {

  public static Integer strToInteger(String s) {
    if (StringUtils.isNotEmpty(s)) {
      return Integer.parseInt(s);
    } else {
      return 0;
    }
  }

  public static Double strToDouble(String s) {
    if (StringUtils.isNotEmpty(s)) {
      return Double.valueOf(s);
    } else {
      return 0.0;
    }
  }
}
