package com.chachae.exam.common.util;

import cn.hutool.core.util.StrUtil;

/**
 * 拓展 HuTool NumberUtil
 *
 * @author yzn
 * @since 2020/2/6 21:24
 */
public class NumberUtil extends cn.hutool.core.util.NumberUtil {

  public static Integer strToInteger(String s) {
    if (StrUtil.isNotEmpty(s)) {
      return Integer.parseInt(s);
    } else {
      return 0;
    }
  }

  public static Double strToDouble(String s) {
    if (StrUtil.isNotEmpty(s)) {
      return Double.valueOf(s);
    } else {
      return 0.0;
    }
  }
}
